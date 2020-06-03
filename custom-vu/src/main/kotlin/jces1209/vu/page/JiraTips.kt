package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions

class JiraTips(
    private val driver: WebDriver
) {

    fun closeTips() {
        driver
            .findElements(By.className("jira-help-tip"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.className("helptip-close"))
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
        driver
            .findElements(By.cssSelector("[role='dialog']"))
            .filter { it.isDisplayed }
            .forEach {
                it
                    .findElement(By.tagName("footer"))
                    .findElement(By.tagName("button"))
                    .click()
                driver.wait(ExpectedConditions.invisibilityOf(it))
            }
    }
}
