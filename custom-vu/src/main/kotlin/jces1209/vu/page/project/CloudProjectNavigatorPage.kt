package jces1209.vu.page.project

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.CloudIssueNavigator
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions


class CloudProjectNavigatorPage(
    private val jira: WebJira
) : ProjectNavigatorPage {


    override fun openProject(projectKey: String): CloudProjectNavigatorPage {
        jira.driver.navigate().to("/projects/")

        jira.driver
            .wait(ExpectedConditions
                .visibilityOfElementLocated(
                    By.xpath(
                        "//*[@data-test-id='global-pages.directories.directory-base.content.table.container']" +
                            "//*[contains(@href,'/browse/')]")))
        jira.navigateTo("projects/$projectKey/issues")
        waitForNavigator()
        return this
    }

    override fun waitForNavigator() {
        CloudIssueNavigator(jira).waitForNavigator()
    }
}
