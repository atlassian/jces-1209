package jces1209.vu.page

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable
import org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated
import jces1209.vu.wait
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class ClassicCloudCommenting(
    private val driver: WebDriver
) : Commenting {

    override fun openEditor() {
        waitForPage()
        driver
            .wait(elementToBeClickable(By.id("footer-comment-button")))
            .click()
        waitForEditor()
    }

    private fun waitForPage() {
        val executor = driver as JavascriptExecutor
        WebDriverWait(driver, 1)
            .until { executor.executeScript("return document.readyState") == "complete" }
    }

    private fun waitForEditor() {
        driver
            .wait(elementToBeClickable(By.id("comment")))
            .click()
    }

    override fun typeIn(comment: String) {
        Actions(driver)
            .sendKeys(comment)
            .perform()
    }

    override fun saveComment() {
        driver.findElement(By.id("issue-comment-add-submit")).click()
    }

    override fun waitForTheNewComment() {
        driver.wait(visibilityOfElementLocated(By.cssSelector(".activity-comment.focused")))
    }

    override fun mentionUser() {
        Actions(driver)
            .sendKeys("@assignee")
            .perform()
        driver
            .wait(ExpectedConditions.presenceOfElementLocated(By.id("mentionDropDown")))
        driver
            .wait(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class = 'jira-mention-issue-roles']//*[. = 'assignee']")))
            .click()
    }
}
