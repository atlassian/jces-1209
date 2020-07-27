package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.VIEW_BOARD
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.observation.IssuesOnBoard
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.contextoperation.ContextOperationBoard
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.WebDriver

class ViewBoard(
    private val driver: WebDriver,
    private val meter: ActionMeter,
    private val boardsMemory: SeededMemory<BoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val random: SeededRandom,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float,
    private val changeIssueStatusProbability: Float
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val jiraTips = JiraTips(driver)

    override fun run() {
        val board = boardsMemory.recall()
        if (board == null) {
            logger.debug("I cannot recall board, skipping...")
            return
        }
        val boardType = board.getTypeLabel()
        val boardContent = viewBoard(boardType, board)

        if (random.random.nextFloat() < viewIssueProbability) {
            if (boardContent.getIssueKeys().isEmpty()) {
                logger.debug("It requires some issues on board to test preview issue")
            } else {
                previewIssue(boardType, board)
                contextOperation(boardType)
            }
        }
        jiraTips.closeTips()
        configureBoard(boardType, board)
        changeIssueStatus(board)
    }

    private fun viewBoard(boardType: String, board: BoardPage): BoardContent {
        return meter.measure(
            key = VIEW_BOARD,
            action = {
                meter.measure(
                    key = if (boardType == "Backlog") {
                        ActionType("View $boardType") { Unit }
                    } else {
                        ActionType("View $boardType Board ") { Unit }
                    },
                    action = {
                        board
                            .goToBoard()
                            .waitForBoardPageToLoad()
                    },
                    observation = {
                        issueKeyMemory.remember(it.getIssueKeys())
                        IssuesOnBoard(it.getIssueCount()).serialize()
                    }
                )
            }
        )
    }

    private fun contextOperation(boardType: String) {
        if (random.random.nextFloat() < contextOperationProbability) {
            meter.measure(MeasureType.CONTEXT_OPERATION_BOARD) {
                meter.measure(ActionType("Context operation ($boardType board)") { Unit }) {
                    ContextOperationBoard(driver)
                        .open()
                }
            }
                .close()
        }
    }

    private fun previewIssue(boardType: String, board: BoardPage) {
        jiraTips.closeTips()
        meter.measure(MeasureType.ISSUE_PREVIEW_BOARD) {
            var message: String
            if (boardType == "Backlog") {
                    message = "Old issue view $boardType"
                } else {
                    message = "Old issue view $boardType board"
                }
            meter.measure(ActionType(message) { Unit }) {
                board.previewIssue()
            }
        }
        board.closePreviewIssue()
    }

    private fun configureBoard(boardType: String, board: BoardPage) {
        if (random.random.nextFloat() < configureBoardProbability) {
            meter.measure(MeasureType.CONFIGURE_BOARD) {
                meter.measure(ActionType(MeasureType.CONFIGURE_BOARD.label + " ($boardType board)") { Unit }) {
                    board
                        .configureBoard()
                        .waitForLoadPage()
                }
            }
        }
    }

    private fun changeIssueStatus(board: BoardPage) {
        if (random.random.nextFloat() < changeIssueStatusProbability) {
            if (board is KanbanBoardPage) {
                meter.measure(MeasureType.MOVE_ISSUE_STATUS_BOARD) {
                    val movingIssue = board.movingIssue()

                    val issue = movingIssue.moveIssueToOtherColumn()
                    if (movingIssue.isModalWindowDisplayed()) {
                        if (movingIssue.isContinueButtonEnabled()) {
                            meter.measure(MeasureType.MOVE_ISSUE_STATUS_BOARD_SUBMIT_WINDOW) {
                                movingIssue.clickContinueButton(issue)
                                if (movingIssue.isModalWindowDisplayed()) {
                                    movingIssue.closeWindows()

                                    val errorMessage = "Failed to submit issue transition [${issue.key}]"
                                    logger.debug(errorMessage)
                                    throw InterruptedException(errorMessage)
                                }
                            }
                        } else {
                            movingIssue.closeWindows()

                            val errorMessage = "Issue [${issue.key}] can't be moved to other column"
                            logger.debug(errorMessage)
                            throw InterruptedException(errorMessage)
                        }
                    }
                }
            }
        }
    }
}
