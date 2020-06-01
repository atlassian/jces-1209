package jces1209.vu.page.boards.view.dc

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.page.boards.configure.CloudConfigureBoard
import jces1209.vu.page.boards.configure.ConfigureBoard
import jces1209.vu.page.boards.configure.DcConfigureBoard
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.BoardPage
import jces1209.vu.page.boards.view.KanbanBoardPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import java.net.URI
import java.time.Duration

class DcKanbanBoardPage(
    jira: WebJira,
    boardId: String
) : KanbanBoardPage(
    driver = jira.driver,
    uri = jira.base.resolve("secure/RapidBoard.jspa?rapidView=$boardId")
) {
    private val dcBoardPage = DcBoardPage(driver, issueSelector)

    override fun waitForBoardPageToLoad(): BoardContent {
        return dcBoardPage.waitForBoardPageToLoad()
    }

    override fun previewIssue(): DcKanbanBoardPage {
        dcBoardPage.previewIssue()
        return this
    }

    override fun configure(): ConfigureBoard {
        return DcConfigureBoard(driver)
    }
}
