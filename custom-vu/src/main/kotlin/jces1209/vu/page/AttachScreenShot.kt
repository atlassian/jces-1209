package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

open class AttachScreenShot(
    protected val driver: WebDriver,
    private var countBefore: Int,
    private val screenShotLocator: String = "(//*[contains(@class,'overlay image persistent sc-lbihag')])"
) {
    fun makeScreenShot() {
        (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

    fun pasteScreenShot() {
        countBefore = getIssueScreenShotsCount(By.xpath(screenShotLocator))
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
    }

    fun waitForTheScreenShotAttached() {
        driver.wait(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.xpath(screenShotLocator), countBefore))
        driver.wait(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.xpath("(//*[@data-test-id=" +
                "'issue.views.issue-base.content.attachment.filmstrip-panel']" +
                "//span[contains(text(),'Attachments')])")))
        waitForAttributeValueForElements(By.xpath(
            "//*[@data-testid='media-file-card-view']"),
            "data-test-status",
            "complete")
    }

    private fun waitForAttributeValueForElements(by: By, attribute: String, expected: String) {
        val elements = driver.findElements(by)
        elements.forEach {
            driver.wait(ExpectedConditions.attributeContains(it, attribute, expected))
        }
    }

    private fun getIssueScreenShotsCount(by: By): Int {
        return driver.findElements(by).size
    }
}
