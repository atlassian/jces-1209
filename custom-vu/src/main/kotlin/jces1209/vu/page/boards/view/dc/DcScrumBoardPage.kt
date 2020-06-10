package jces1209.vu.page.boards.view.dc

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.boards.configure.ConfigureBoard
import jces1209.vu.page.boards.configure.DcConfigureBoard
import jces1209.vu.page.boards.sprint.SprintPage
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.ScrumBoardPage
import jces1209.vu.page.boards.view.SprintBoardComponent
import org.openqa.selenium.By
import java.net.URI

class DcScrumBoardPage(
    jira: WebJira,
    boardId: String
) : ScrumBoardPage(
    driver = jira.driver,
    uri = jira.base.resolve("secure/RapidBoard.jspa?rapidView=$boardId")
) {
    override val issueSelector = By.className("js-issue")
    private val dcBoardPage = DcBoardPage(driver, issueSelector)

    override fun waitForBoardPageToLoad(): BoardContent {
        return dcBoardPage.waitForBoardPageToLoad()
    }

    override fun previewIssue(): DcScrumBoardPage {
        dcBoardPage.previewIssue()
        return this
    }

    override fun closePreviewIssue() {
        dcBoardPage.closePreviewIssue()
    }

    override fun goToBacklog(): SprintBoardComponent {
        driver.navigate().to(
            URI(
                uri.scheme,
                uri.authority,
                uri.path,
                uri.query + "&view=planning&issueLimit=100",
                uri.fragment)
                .toURL())
        return SprintBoardComponent(driver)
    }

    override fun goToActiveSprint(): SprintPage {
        goToBoard()
        return SprintPage(driver)
    }

    override fun configure(): ConfigureBoard {
        return DcConfigureBoard(driver)
    }
}
