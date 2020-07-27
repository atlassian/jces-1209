package jces1209.vu.page.dashboard.dc

import com.atlassian.performance.tools.jiraactions.api.WebJira
import jces1209.vu.page.FalliblePage
import jces1209.vu.page.dashboard.DashboardPage
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class DcDashboardPage(
    jira: WebJira
) : DashboardPage(driver = jira.driver) {

    override fun createDashboard(): String {
        driver
            .wait(elementToBeClickable(By.id("create_page")))
            .click()
        val dashboardName = "TestDasboard" + System.currentTimeMillis()
        driver
            .wait(visibilityOfElementLocated(By.name("portalPageName")))
            .sendKeys(dashboardName)
        driver
            .wait(
                ExpectedConditions.elementToBeClickable(By.id("create_dashboard"))
            ).click()
        driver.findElement(
            By.id("edit-entity-dashboard-name"))
            .sendKeys("TestDasboard" + System.currentTimeMillis())
        driver.findElement(
            By.id("edit-entity-submit"))
            .findElement(By.cssSelector("#add-dashboard-submit, #edit-entity-submit"))
            .click()

        waitForDashboards()
        return dashboardName;

    }
}



