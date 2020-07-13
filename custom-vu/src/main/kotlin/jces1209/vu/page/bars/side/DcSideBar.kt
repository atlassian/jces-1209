package jces1209.vu.page.bars.side

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage
import jces1209.vu.page.boards.view.dc.DcKanbanBoardPage
import jces1209.vu.page.boards.view.dc.DcScrumBacklogPage
import jces1209.vu.page.boards.view.dc.DcScrumSprintPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable

class DcSideBar(
    jira: WebJira
) : SideBar(jira) {
    val backlogLocator = By.cssSelector("a[data-link-id='com.pyxis.greenhopper.jira:project-sidebar-plan-scrum']")
    val sprintLocator = By.cssSelector("a[data-link-id='com.pyxis.greenhopper.jira:project-sidebar-work-scrum']")

    override fun isBacklogPresent(): Boolean {
        return driver
            .findElements(backlogLocator)
            .isNotEmpty()
    }

    override fun clickBacklog(): ScrumBacklogPage {
        return DcScrumBacklogPage(jira, processBoardLink(driver.wait(elementToBeClickable(backlogLocator))))
    }

    override fun isSprintPresent(): Boolean {
        return driver
            .findElements(sprintLocator)
            .isNotEmpty()
    }

    override fun clickSprint(): ScrumSprintPage {
        return DcScrumSprintPage(jira, processBoardLink(driver.wait(elementToBeClickable(sprintLocator))))
    }

    override fun isSelectBoardPresent(): Boolean {
        return getOtherBoardsList().isNotEmpty()
    }

    override fun selectOtherBoard(): KanbanBoardPage {
        return DcKanbanBoardPage(jira, processBoardLink(getOtherBoardsList().first()))
    }

    private fun getOtherBoardsList(): List<WebElement> {
        Actions(driver)
            .moveToElement(driver.findElement(By.className("collapsed-scope-filter-container")))
            .perform()

        return driver
            .findElements(By.cssSelector(".collapsed-scope-list > .scope-filter:not(selected-scope-filter) > a"))
            .filter { it.isDisplayed }
    }

    private fun processBoardLink(boardLink: WebElement): String {
        val boardId = "rapidView=(\\d+)".toRegex().find(boardLink.getAttribute("href"))!!.value

        boardLink.click()
        return boardId;
    }
}
