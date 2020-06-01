package jces1209.vu.page.boards.view.cloud

import jces1209.vu.page.boards.sprint.SprintPage
import jces1209.vu.page.boards.view.BoardContent
import jces1209.vu.page.boards.view.ScrumBoardPage
import jces1209.vu.page.boards.view.SprintBoardComponent
import org.openqa.selenium.WebDriver
import java.net.URI

class CloudScrumBoardPage(
    driver: WebDriver, uri: URI
) : ScrumBoardPage(driver, uri) {
    private val cloudClassicBoardPage =  CloudClassicBoardPage(driver, issueSelector)

    override fun waitForBoardPageToLoad(): BoardContent {
        return cloudClassicBoardPage.waitForBoardPageToLoad()
    }

    override fun previewIssue(): CloudScrumBoardPage {
        cloudClassicBoardPage.previewIssue()
        return this
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
}
