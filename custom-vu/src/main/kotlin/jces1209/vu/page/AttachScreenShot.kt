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
    protected var countBefore: Int = 0
    abstract val screenShotLocator: String

    fun makeScreenShot() {
        (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

    abstract fun pasteScreenShot()
    abstract fun waitForTheScreenShotAttached()

    protected fun waitForAttributeValueForElements(by: By, attribute: String, expected: String) {
        val elements = driver.findElements(by)
        elements.forEach {
            driver.wait(ExpectedConditions.attributeContains(it, attribute, expected))
        }
    }

    protected fun getIssueScreenShotsCount(by: By): Int {
        return driver.findElements(by).size
    }
}
