package jces1209.vu.action.boards

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.memories.IssueKeyMemory
import jces1209.vu.Measure
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.NextGenBoardPage
import org.openqa.selenium.WebDriver

class ViewNextGenBoard(
    private val driver: WebDriver,
    private val measure: Measure,
    private val nextGenBoardsMemory: SeededMemory<NextGenBoardPage>,
    private val issueKeyMemory: IssueKeyMemory,
    private val viewIssueProbability: Float,
    private val configureBoardProbability: Float,
    private val contextOperationProbability: Float
) : ViewBoard(
    driver = driver,
    measure = measure,
    issueKeyMemory = issueKeyMemory,
    configureBoardProbability = configureBoardProbability,
    contextOperationProbability = contextOperationProbability), Action {

    override fun run() {
        val board = getBoard(nextGenBoardsMemory as SeededMemory<BoardPage>)
        if (board == null) {
            logger.debug("I cannot recall board, skipping...")
            return
        }

        val boardContent = viewBoard(MeasureType.VIEW_NEXT_GEN_BOARD, board)

        if (null != boardContent) {
            measure.roll(viewIssueProbability) {
                if (boardContent.getIssueKeys().isEmpty()) {
                    logger.debug("It requires some issues on board to test preview issue")
                } else {
                    repeat(2) {
                        previewIssue(MeasureType.ISSUE_PREVIEW_NEXT_GEN_BOARD, board)
                    }
                    contextOperation(MeasureType.CONTEXT_OPERATION_NEXT_GEN_BOARD)
                }
            }

            jiraTips.closeTips()
            configureBoard(MeasureType.CONFIGURE_NEXT_GEN_BOARD, board)
        }
    }
}
