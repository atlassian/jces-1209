package jces1209.vu.page.dashboard.cloud

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.dashboard.DashboardPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class CloudDashboardPage(
    jira: WebJira
) : DashboardPage(driver = jira.driver) {

    override fun createDashboard(): String {
        driver
            .findElement(By.xpath("//*[@data-test-id='directory.dashboards-v2.create-button']"))
            .click()
        val dashboardName = "TestDasboard" + System.currentTimeMillis()
        driver
            .wait(visibilityOfElementLocated(By.id("shareable-entity-dialog.input-name")))
            .sendKeys(dashboardName)
        driver
            .wait(elementToBeClickable(By.xpath("(//button[@type='submit'])")))
            .click()

        waitForGadgetsLoad()
        return dashboardName;

    }
}



