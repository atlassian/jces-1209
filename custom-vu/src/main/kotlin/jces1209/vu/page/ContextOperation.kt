package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.and
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated

class ContextOperation(
    private val driver: WebDriver
) {
    private val closeButtonLocator = By.id("aui-dialog-close")

    fun openContextOperation() {
        Actions(driver)
            .sendKeys(".")
            .perform()

        driver
            .wait(and(
                visibilityOfElementLocated(By.className("jira-dialog")),
                visibilityOfElementLocated(By.id("issueactions-queryable-container")),
                visibilityOfElementLocated(closeButtonLocator)
            ))
    }

    fun close() {
        driver
            .findElement(closeButtonLocator)
            .click()
    }
}
