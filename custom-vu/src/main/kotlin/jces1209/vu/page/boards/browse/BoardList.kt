package jces1209.vu.page.boards.browse

import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.NextGenBoardPage
import jces1209.vu.page.boards.view.ScrumBoardPage

abstract class BoardList {

    abstract fun listBoards(): MixedBoards

    class MixedBoards(
        val kanban: Collection<KanbanBoardPage>,
        val scrum: Collection<ScrumBoardPage>,
        val nextGen: Collection<NextGenBoardPage>
    )
}
