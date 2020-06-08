package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

class CloudAddScreenShot(
    driver: WebDriver
) : AttachScreenShot(driver) {
    override var screenShotLocator: String = "(//*[contains(@class,'overlay image persistent')])"
    override fun pasteScreenShot() {
        countBefore = getIssueScreenShotsCount(By.xpath(screenShotLocator))
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
    }

    override fun waitForTheScreenShotAttached() {
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
}
