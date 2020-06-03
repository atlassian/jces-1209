package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

abstract class AttachScreenShot(
    protected val driver: WebDriver
) {

    fun makeScreenShot() {
        val src = (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }

    fun pasteScreenShot() {
        val builder = Actions(driver)
        builder.keyDown(Keys.CONTROL).sendKeys("v").perform()
    }

    fun waitForTheScreenShotAttached() {
        driver.wait(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@data-test-id=" +
            "'issue.views.issue-base.content.attachment.filmstrip-panel']" +
            "//span[contains(text(),'Attachments')])")))
    }
}
