package jces1209.vu.page.customizecolumns

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

abstract class ColumnsEditor(
    private val driver: WebDriver
) {

    fun selectView(view: Int) {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//button[@id='layout-switcher-button']")),
            timeout = Duration.ofSeconds(15)
        ).click()
        if (view == 1) {
            driver.wait(
                condition = ExpectedConditions.elementToBeClickable(By.xpath(
                    ".//div[@class='aui-list']" +
                        "//*[@data-layout-key='list-view']")),
                timeout = Duration.ofSeconds(5)
            ).click()
        } else if (view == 0) {
            driver.wait(
                condition = ExpectedConditions.elementToBeClickable(By.xpath(
                    ".//div[@class='aui-list']" +
                        "//*[@data-layout-key='split-view']")),
                timeout = Duration.ofSeconds(5)
            ).click()
        }
    }

    fun openEditor() {
        openColumnsList()
        restoreDefaults()
        //adding delay to restore defaults
        Thread.sleep(3000)
        openColumnsList()
    }

    private fun openColumnsList() {
        driver.wait(
                condition = ExpectedConditions.elementToBeClickable(By.xpath(
                    ".//*[@class=" +
                        "'aui-button aui-button-subtle column-picker-trigger']")),
                timeout = Duration.ofSeconds(15)
        ).click()
    }

    fun selectItems(itemsCount: Int) {
        driver.wait(
            condition = ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("(.//*[@class='check-list-item'])"), 0),
            timeout = Duration.ofSeconds(15)
        )
        val items = driver.findElements(By.xpath(".//*[contains(@class,'check-list-item')]"))
        for (i: Int in 0 until itemsCount) {
            items.get(i).click()
        }
        //adding delay to render selected items
        Thread.sleep(3000)
    }

    fun submitSelection() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//*[@class='aui-button submit']")),
            timeout = Duration.ofSeconds(5)
        ).click()
    }

    private fun restoreDefaults() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.xpath(
                ".//*[@class='aui-button aui-button-link restore-defaults']")),
            timeout = Duration.ofSeconds(5)
        ).click()
    }
}
