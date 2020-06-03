package jces1209.vu.page

import jces1209.vu.wait
import org.openqa.selenium.*
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class CloudIssuePage(
    private val driver: WebDriver
) : AbstractIssuePage {
    private val bentoSummary = By.cssSelector("[data-test-id='issue.views.issue-base.foundation.summary.heading']")
    private val classicSummary = By.id("key-val")
    private val falliblePage = FalliblePage.Builder(
        expectedContent = listOf(bentoSummary, classicSummary),
        webDriver = driver
    )
        .cloudErrors()
        .build()

    override fun waitForSummary(): AbstractIssuePage {
        falliblePage.waitForPageToLoad()
        return this
    }

    override fun comment(): Commenting {
        return if (isCommentingClassic()) {
            ClassicCloudCommenting(driver)
        } else {
            BentoCommenting(driver)
        }
    }

    override fun editDescription(description: String): CloudIssuePage {
        driver
            .wait(elementToBeClickable(By.cssSelector("[data-test-id = 'issue.views.field.rich-text.description']")))
            .click();

        val descriptionForm = driver
            .wait(
                presenceOfElementLocated(By.cssSelector("[data-test-id='issue.views.field.rich-text.editor-container']"))
            )

        Actions(driver)
            .sendKeys(description)
            .perform()

        descriptionForm
            .findElement(By.cssSelector("[data-testid='comment-save-button']"))
            .click()

        driver.wait(
            invisibilityOfAllElements(descriptionForm)
        )
        return this;
    }

    override fun linkIssue(): CloudIssueLinking {
        return CloudIssueLinking(driver)
    }

    override fun addAttachment(): CloudAddScreenShot {
        return CloudAddScreenShot(driver)
    }

    private fun isCommentingClassic(): Boolean = driver
        .findElements(By.id("footer-comment-button"))
        .isNotEmpty()
}
