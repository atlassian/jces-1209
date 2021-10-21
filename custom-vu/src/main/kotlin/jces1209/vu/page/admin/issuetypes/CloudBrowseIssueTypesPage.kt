package jces1209.vu.page.admin.issuetypes

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.lang.Exception
import java.time.Duration

class CloudBrowseIssueTypesPage(
    private val jira: WebJira
) : BrowseIssueTypesPage(jira) {

    private val falliblePage = FalliblePage.Builder(
        jira.driver,
        or(
            loadingPageExpectedCondition,
            or(
                presenceOfAllElementsLocatedBy(By.xpath("//*[@data-testid = 'admin-pages-issue-types-directory.ui.table.dynamic-table-stateless--cell-0']")),
                and(
                    presenceOfElementLocated(By.xpath("//*[. = 'Issue types']")),
                    presenceOfElementLocated(By.xpath("//*[@data-test-id = 'searchfield']"))
                )
            )
        )
    )
        .cloudErrors()
        .timeout(Duration.ofSeconds(30))
        .build()

    override fun waitForBeingLoaded(): CloudBrowseIssueTypesPage {
        falliblePage.waitForPageToLoad()
        return this
    }
}
