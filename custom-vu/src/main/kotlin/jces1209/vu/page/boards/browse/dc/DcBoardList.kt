package jces1209.vu.page.boards.browse.dc

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.boards.browse.BoardList
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.ScrumBoardPage
import jces1209.vu.page.boards.view.dc.DcKanbanBoardPage
import jces1209.vu.page.boards.view.dc.DcScrumBoardPage
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class DcBoardList(
    private val jira: WebJira
) : BoardList() {
    val boardsTableSelector = By.className("boards-table")

    override fun listBoards(): MixedBoards {
        return MixedBoards(getKanbanBoards(), getScrumBoards(), emptyList())
    }

    private fun getKanbanBoards(): Collection<KanbanBoardPage> =
        filterAndGetBoards("type-filter-kanban")
            .map {
                DcKanbanBoardPage(jira, it)
            }

    private fun getScrumBoards(): Collection<ScrumBoardPage> =
        filterAndGetBoards("type-filter-scrum")
            .map {
                DcScrumBoardPage(jira, it)
            }

    private fun getBoards(): Collection<String> =
        jira.driver
            .findElements(By.cssSelector(".boards-list tr"))
            .mapNotNull {
                it.getAttribute("data-board-id")
            }

    private fun filterAndGetBoards(filterClassName: String): Collection<String> {
        var boardsTable = jira.driver.wait(
            Duration.ofSeconds(5),
            ExpectedConditions.visibilityOfElementLocated(boardsTableSelector)
        )

        jira.driver.wait(
            Duration.ofSeconds(5),
            ExpectedConditions.elementToBeClickable(By.cssSelector("#ghx-manage-boards-filter a"))
        ).click()

        val selectedFilters = jira.driver
            .findElements(By.cssSelector("#board-types input[type='checkbox']"))
            .filter { it.isSelected }

        selectedFilters
            .forEach {
                it.findElement(By.xpath("./..")).click()
            }

        if (selectedFilters.isNotEmpty()) {
            jira.driver.wait(
                Duration.ofSeconds(15),
                ExpectedConditions.stalenessOf(boardsTable)
            )
            boardsTable = jira.driver.findElement(boardsTableSelector)
        }

        jira.driver.wait(
            Duration.ofSeconds(5),
            ExpectedConditions.elementToBeClickable(By.className(filterClassName))
        ).click()

        jira.driver.wait(
            Duration.ofSeconds(15),
            ExpectedConditions.stalenessOf(boardsTable)
        )

        jira.driver
            .findElement(By.id("ghx-rv-visibility"))
            .click()

        return getBoards()
    }
}
