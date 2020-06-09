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
    override var screenShotLocator = By.xpath("(//*[@class='attachment-thumb])")
    override fun pasteScreenShot(): Int {
        countBefore = getIssueScreenShotsCount(screenShotLocator)
        Actions(driver).keyDown(Keys.CONTROL).sendKeys("v").perform()
        driver.wait(
            ExpectedConditions.elementToBeClickable(
                By.className("aui-button aui-button-primary")))
            .click()
        driver.wait(
            ExpectedConditions.invisibilityOfElementLocated(By.id("attach-screenshot-dialog")))
        driver.wait(ExpectedConditions.numberOfElementsToBeMoreThan(
            screenShotLocator, countBefore))
        driver.wait(ExpectedConditions.visibilityOfAllElementsLocatedBy(
            screenShotLocator))
        waitForAttributeValueForElements(By.xpath("//*[contains(@class,'js-file-attachment')]"),
            "data-attachment-thumbnail",
            "true", countBefore)
        return getIssueScreenShotsCount(screenShotLocator)
    }
}
