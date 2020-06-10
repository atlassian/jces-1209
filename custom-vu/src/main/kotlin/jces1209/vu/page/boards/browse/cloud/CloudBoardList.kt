package jces1209.vu.page.boards.browse.cloud

import jces1209.vu.page.boards.browse.BoardList
import jces1209.vu.page.boards.view.cloud.CloudKanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudNextGenBoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumBoardPage
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.net.URI

class CloudBoardList(
    private val element: WebElement,
    private val driver: WebDriver
) : BoardList() {

    override fun listBoards(): MixedBoards {
        val columnNames = element
            .findElements(By.tagName("th"))
            .map { it.text.trim() }
        val nameColumnIndex = columnNames.indexOf("Name")
        val typeColumnIndex = columnNames.indexOf("Type")
        val kanbanBoards = mutableListOf<CloudKanbanBoardPage>()
        val scrumBoards = mutableListOf<CloudScrumBoardPage>()
        val nextGenBoards = mutableListOf<CloudNextGenBoardPage>()
        element
            .findElements(By.cssSelector("tbody tr"))
            .map { row -> row.findElements(By.tagName("td")) }
            .forEach { cells ->
                val type = cells[typeColumnIndex]!!.text.trim()
                val name = cells[nameColumnIndex]!!
                val uri = name.findElement(By.tagName("a")).getAttribute("href").let { URI(it) }
                when (type) {
                    "Kanban" -> kanbanBoards.add(CloudKanbanBoardPage(driver, uri))
                    "Scrum" -> scrumBoards.add(CloudScrumBoardPage(driver, uri))
                    "Next-gen" -> nextGenBoards.add(CloudNextGenBoardPage(driver, uri))
                    else -> throw Exception("Unknown board type: $type")
                }
            }
        return MixedBoards(kanbanBoards, scrumBoards, nextGenBoards)
    }
}
