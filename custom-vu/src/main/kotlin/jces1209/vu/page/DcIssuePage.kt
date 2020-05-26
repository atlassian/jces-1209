package jces1209.vu.page

import com.atlassian.performance.tools.jiraactions.api.page.JiraErrors
import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class DcIssuePage(
    private val driver: WebDriver
) : AbstractIssuePage {

    override fun waitForSummary(): DcIssuePage {
        val jiraErrors = JiraErrors(driver)
        driver.wait(
            or(
                visibilityOfElementLocated(By.id("key-val")),
                jiraErrors.anyCommonError()
            )
        )
        jiraErrors.assertNoErrors()
        return this
    }

    override fun comment(): Commenting {
        return DcCommenting(driver)
    }

    override fun editDescription(description: String): DcIssuePage {
        driver
            .wait(ExpectedConditions
                .elementToBeClickable(By.id("description-val")))
            .click()

        val descriptionForm = driver
            .wait(
                visibilityOfElementLocated(By.id("description-form"))
            )

        Actions(driver)
            .sendKeys(description)
            .perform()

        descriptionForm
            .findElement(By.cssSelector("button[type='submit']"))
            .click()

        driver.wait(
            ExpectedConditions
                .invisibilityOfAllElements(descriptionForm)
        )
        return this
    }

    override fun addAttachment() {
        TODO("Not yet implemented")
    }
    override fun linkIssue(): DcIssueLinking {
        return DcIssueLinking(driver)
    }
}
