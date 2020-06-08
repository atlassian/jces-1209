package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

class DcAddScreenShot(
    driver: WebDriver
) : AttachScreenShot(driver) {
    override var screenShotXPath: String = "(//*[@class='attachment-thumb])"

    override fun pasteScreenShot() {
        countBefore = getIssueScreenShotsCount(By.xpath(screenShotXPath))
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
        driver.wait(
            ExpectedConditions.elementToBeClickable(
                By.className("aui-button aui-button-primary")))
            .click()
    }

    override fun waitForTheScreenShotAttached() {
        driver.wait(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.xpath(screenShotXPath), countBefore))
        driver.wait(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.xpath(screenShotXPath)))
        waitForAttributeValueForElements(By.xpath(
            "//*[@data-testid='media-file-card-view']"),
            "data-test-status",
            "complete")
    }
}
