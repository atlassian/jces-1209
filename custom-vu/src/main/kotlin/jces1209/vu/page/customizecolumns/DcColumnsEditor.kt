package jces1209.vu.page.customizecolumns

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class DcColumnsEditor(
    private val driver: WebDriver
) : ColumnsEditor {

    override fun openEditor() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.className(
                "aui-button aui-button-subtle column-picker-trigger")),
            timeout = Duration.ofSeconds(5)
        ).click()

    }

    override fun selectItems(itemsCount: Int) {
        val items: List<WebElement>
        items = driver.findElements(By.xpath("(//input[@type='checkbox'])"))
        for (i: Int in 0 until itemsCount) {
            items.get(i).click()
        }
    }

    override fun submitSelection() {
        driver.wait(
            condition = ExpectedConditions.elementToBeClickable(By.className(
                "aui-button submit")),
            timeout = Duration.ofSeconds(5)
        ).click()
    }
}
