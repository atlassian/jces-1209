package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

abstract class AttachScreenShot(
    protected val driver: WebDriver
) {
    abstract val screenShotLocator: By

    fun makeScreenShot() {
        (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

    abstract fun pasteScreenShot()

    protected fun waitForAttributeValueForElements(by: By, attributeName: String, attributeValue: String) {
        driver
            .findElements(by)
            .forEach {
                driver.wait(ExpectedConditions.attributeContains(it, attributeName, attributeValue))
            }
    }

    protected fun getScreenShotsCount(): Int {
        return driver.findElements(screenShotLocator).size
    }
}
