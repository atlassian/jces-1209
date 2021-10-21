package jces1209.vu.page.admin.fieldscreen

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class CloudBrowseFieldScreensPage(
    jira: WebJira
) : BrowseFieldScreensPage(jira) {

    private val falliblePage = FalliblePage.Builder(
        jira.driver,
        presenceOfElementLocated(By.xpath("//td[@data-testid='admin-pages-screens-page.ui.screens-table.dynamic-table-stateless--cell-0']"))
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(60))
        .build()

    override fun waitForBeingLoaded(): CloudBrowseFieldScreensPage {
        falliblePage.waitForPageToLoad()
        return this
    }
}
