package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions

class DcAddScreenShot(private val driver: WebDriver
) : AttachScreenShot {
    override fun makeScreenShot(){
        val src = (driver as TakesScreenshot).getScreenshotAs(OutputType.BYTES)
    }
    override fun pasteScreenShot(){
        val builder = Actions(driver)
        builder.keyDown(Keys.CONTROL).sendKeys("v").perform()
    }
    override fun waitForTheScreenShotAttached(){
        driver.wait(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@data-test-id=" +
            "'issue.views.issue-base.content.attachment.filmstrip-panel']" +
            "//span[contains(text(),'Attachments')])")))
    }
}
