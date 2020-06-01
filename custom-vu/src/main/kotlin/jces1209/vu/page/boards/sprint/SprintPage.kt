package jces1209.vu.page.boards.sprint

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions.and
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class SprintPage(
    private val driver: WebDriver
) {
    private val issueLocator = By.className("ghx-issue")
    private val issueSummaryLocator = By.className("ghx-summary")
    private val completeButtonLocator = By.id("ghx-complete-sprint")

    private fun columns() = driver
        .findElements(By.cssSelector(".ghx-columns .ghx-column"))

    fun maxColumnIssuesNumber(): Int = columns()
        .map { it.findElements(issueLocator).size }
        .max()!!

    fun reorderIssue() {
        val columns = columns()
        val columnIndex = columns
            .indexOfFirst { it.findElements(issueLocator).size > 2 }

        val issueFirstText = columns[columnIndex]
            .findElements(issueSummaryLocator)
            .first()
            .text

        val issues = columns[columnIndex]
            .findElements(By.cssSelector(".ghx-card-footer, .ghx-grabber"))
        Actions(driver)
            .dragAndDrop(issues.first(), issues.last())
            .perform()

        driver.wait(
            ExpectedCondition {
                columns()[columnIndex]
                    .findElements(issueSummaryLocator)
                    .last()
                    .text == issueFirstText
            })
    }

    fun isCompleteButtonPresent(): Boolean {
        return driver
            .findElements(completeButtonLocator)
            .isNotEmpty()
    }

    fun completeSprint() {
        driver
            .findElement(completeButtonLocator)
            .click()

        driver
            .wait(visibilityOfElementLocated(By.className("ghx-complete-button")))
            .click()

        driver
            .wait(
                and(
                    visibilityOfElementLocated(By.className("aui-message-success")),
                    visibilityOfElementLocated(By.id("subnav-trigger-report")
                    )))
    }
}
