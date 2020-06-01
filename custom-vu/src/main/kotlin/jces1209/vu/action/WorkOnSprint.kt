package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import jces1209.vu.MeasureType
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.JiraTips
import jces1209.vu.page.boards.sprint.SprintPage
import jces1209.vu.page.boards.view.ScrumBoardPage
import jces1209.vu.page.boards.view.SprintBoardComponent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class WorkOnSprint(
    private val meter: ActionMeter,
    private val jiraTips: JiraTips,
    private val scrumBoardsMemory: SeededMemory<ScrumBoardPage>
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val scrumBoard = scrumBoardsMemory.recall()
        if (scrumBoard == null) {
            logger.debug("I cannot recall scrum board, skipping...")
            return
        }

        val sprintBoardComponent = scrumBoard.goToBacklog()
        createSprint(sprintBoardComponent)

        jiraTips.closeTips()
        if (sprintBoardComponent.backlogIssuesNumber() > 0) {
            moveIssuesToSprint(sprintBoardComponent)

            if (sprintBoardComponent.isStartSprintEnabled()) {
                startSprint(sprintBoardComponent)
            } else {
                logger.debug("Can't start sprint - the option is disabled")
            }
        } else {
            logger.debug("Scrum backlog doesn't contain issues in backlog")
        }

        val sprintPage = scrumBoard.goToActiveSprint()

        if (sprintPage.maxColumnIssuesNumber() > 1) {
            reorderIssue(sprintPage)
        } else {
            logger.debug("Scrum backlog doesn't contain enough issues to make reorder")
        }

        if (sprintPage.isCompleteButtonPresent()) {
            completeSprint(sprintPage)
        } else {
            logger.debug("Scrum backlog doesn't contain sprint which is ready to be completed")
        }
    }

    private fun completeSprint(sprintPage: SprintPage) {
        meter.measure(MeasureType.SPRINT_COMPLETE) {
            sprintPage.completeSprint()
        }
    }

    private fun reorderIssue(sprintPage: SprintPage) {
        meter.measure(MeasureType.SPRINT_REORDER_ISSUE) {
            sprintPage.reorderIssue()
        }
    }

    private fun startSprint(sprintBoardComponent: SprintBoardComponent) {
        meter.measure(MeasureType.SPRINT_START_SPRINT) {
            meter.measure(MeasureType.SPRINT_START_SPRINT_EDITOR) {
                sprintBoardComponent.openStartSprintPopUp()
            }
            meter.measure(MeasureType.SPRINT_START_SPRINT_SUBMIT) {
                sprintBoardComponent.submitStartSprint()
            }
        }
    }

    private fun moveIssuesToSprint(sprintBoardComponent: SprintBoardComponent) {
        meter.measure(MeasureType.SPRINT_MOVE_ISSUE) {
            sprintBoardComponent.moveIssueToSprint()
        }

        // Move additional times for reordering
        repeat(10) {
            sprintBoardComponent.moveIssueToSprint()
        }
    }

    private fun createSprint(sprintBoardComponent: SprintBoardComponent) {
        meter.measure(MeasureType.SPRINT_CREATE) {
            sprintBoardComponent.createSprint()
        }
    }
}
