package jces1209.vu.page.bars.side

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.page.boards.view.ScrumBacklogPage
import jces1209.vu.page.boards.view.ScrumSprintPage
import jces1209.vu.page.boards.view.cloud.CloudKanbanBoardPage
import jces1209.vu.page.boards.view.cloud.CloudScrumBacklogPage
import jces1209.vu.page.boards.view.cloud.CloudScrumSprintPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.net.URI

class CloudSideBar(
    jira: WebJira
) : SideBar(jira) {
    val backlogLocator = By.xpath("//a[@data-testid='NavigationItem' and //div[text()='Backlog']]")
    val sprintLocator = By.xpath("//a[@data-testid='NavigationItem' and //div[text()='Active sprints']]")

    override fun isBacklogPresent(): Boolean {
        return driver
            .findElements(backlogLocator)
            .isNotEmpty()
    }

    override fun clickBacklog(): ScrumBacklogPage {
        return CloudScrumBacklogPage(driver, processBoardLink(driver.wait(elementToBeClickable(backlogLocator))))
    }

    override fun isSprintPresent(): Boolean {
        return driver
            .findElements(sprintLocator)
            .isNotEmpty()
    }

    override fun clickSprint(): ScrumSprintPage {
        return CloudScrumSprintPage(driver, processBoardLink(driver.wait(elementToBeClickable(sprintLocator))))
    }

    override fun isSelectBoardPresent(): Boolean {
        return getOtherBoardsList().isNotEmpty()
    }

    override fun selectOtherBoard(): KanbanBoardPage {
        return CloudKanbanBoardPage(driver, processBoardLink(getOtherBoardsList().first()))
    }

    private fun getOtherBoardsList(): List<WebElement> {
        driver
            .wait(visibilityOfElementLocated(By.cssSelector("[data-test-id='navigation-apps.scope-switcher-v2']")))
            .click()

        return driver
            .wait(visibilityOfAllElementsLocatedBy(By.cssSelector("a[id^='react-select']:not([id\$='-0'])")))
    }

    private fun processBoardLink(boardLink: WebElement): URI {
        val boardId = boardLink.getAttribute("href")

        boardLink.click()
        return URI(boardId);
    }
}
