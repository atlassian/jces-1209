package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

open class AttachScreenShot(
    protected val driver: WebDriver,
    private var countBefore: Int
) {
    fun makeScreenShot() {
        (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

    fun pasteScreenShot() {
        countBefore = getIssueScreenShotsCount(By.xpath("(//*[contains(@class,'overlay image persistent sc-lbihag')])"))
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
    }

    fun waitForTheScreenShotAttached() {
        driver.wait(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.xpath("(//*[contains(@class,'overlay image persistent sc-lbihag')])"), countBefore))
        driver.wait(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("(//*[@data-test-id=" +
                "'issue.views.issue-base.content.attachment.filmstrip-panel']" +
                "//span[contains(text(),'Attachments')])")))
        waitForAttributeValueForElements(By.xpath(
            "//*[@data-testid='media-file-card-view']"),
            "data-test-status",
            "complete")
        val after = getIssueScreenShotsCount(By.xpath("(//*[contains(@class,'overlay image persistent sc-lbihag')])"))
    }

    private fun waitForAttributeValueForElements(by: By, attribute: String, expected: String) {
        val elements = driver.findElements(by)
        elements.forEach {
            driver.wait(ExpectedConditions.attributeContains(it, attribute, expected))
        }
    }

    fun getIssueScreenShotsCount(by: By): Int {
        return driver.findElements(by).size
    }
}
