package jces1209.vu.memory

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import jces1209.vu.page.boards.browse.BoardList
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.NextGenBoardPage
import jces1209.vu.page.boards.view.ScrumBoardPage

class BoardPagesMemory(
    val seededRandom: SeededRandom
) {
    val all = SeededMemory<BoardPage>(seededRandom)
    val kanban = SeededMemory<KanbanBoardPage>(seededRandom)
    val scrum = SeededMemory<ScrumBoardPage>(seededRandom)
    val nextGen = SeededMemory<NextGenBoardPage>(seededRandom)

    fun rememberBoardsList(boardsList: BoardList.MixedBoards) {
        all.remember(boardsList.kanban)
        all.remember(boardsList.scrum)
        all.remember(boardsList.nextGen)

        kanban.remember(boardsList.kanban)
        scrum.remember(boardsList.scrum)
        nextGen.remember(boardsList.nextGen)
    }
}
