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
    override var screenShotLocator: String = "(//*[@class='attachment-thumb])"

    override fun pasteScreenShot() {
        countBefore = getIssueScreenShotsCount(By.xpath(screenShotLocator))
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
        driver.wait(
            ExpectedConditions.elementToBeClickable(
                By.className("aui-button aui-button-primary")))
            .click()
    }

    override fun waitForTheScreenShotAttached() {
        driver.wait(ExpectedConditions.numberOfElementsToBeMoreThan(
            By.xpath(screenShotLocator), countBefore))
        driver.wait(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            By.xpath(screenShotLocator)))
        waitForAttributeValueForElements(By.xpath(
            "//*[@data-testid='media-file-card-view']"),
            "data-test-status",
            "complete")
    }
}
