package jces1209.vu.page.bars.side

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage

abstract class SideBar(
    protected val jira: WebJira
) {
    protected val driver = jira.driver

    abstract fun isBacklogPresent(): Boolean
    abstract fun clickBacklog(): ScrumBacklogPage

    abstract fun isSprintPresent(): Boolean
    abstract fun clickSprint(): ScrumSprintPage

    abstract fun isSelectBoardPresent(): Boolean
    abstract fun selectOtherBoard(): KanbanBoardPage
}
