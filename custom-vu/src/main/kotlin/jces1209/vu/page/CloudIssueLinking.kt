package jces1209.vu.page;

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.ExpectedConditions.*

class CloudIssueLinking(
    private val driver: WebDriver
) : IssueLinking {

    override fun openEditor() {
        driver
            .wait(visibilityOfElementLocated(
                By.cssSelector("[data-test-id='issue.issue-view.views.issue-base.foundation.quick-add.quick-add-item.link-issue']")))
            .click();

        driver
            .wait(
                visibilityOfElementLocated(By.cssSelector(".issue-links-search__value-container")))
    }

    override fun searchAndChooseIssue(issuePrefix: String) {
        val searchForm = driver
            .wait(
                visibilityOfElementLocated(By.cssSelector(".issue-links-search__value-container")))


        Actions(driver)
            .click(searchForm)
            .sendKeys(issuePrefix)
            .perform()

        val searchGroup = driver
            .wait(visibilityOfElementLocated(By.className("issue-links-search__group")))

        Actions(driver)
            .sendKeys(Keys.ARROW_DOWN, Keys.ARROW_DOWN, Keys.ENTER)
            .perform()

        driver
            .wait(
                and(
                    invisibilityOf(searchGroup),
                    visibilityOfElementLocated(By.cssSelector(".issue-links-search__clear-indicator"))
                ))
    }

    override fun submitIssue() {
        val cardViewSelector = By
            .cssSelector("[data-test-id='issue.issue-view.views.common.issue-line-card.issue-line-card-view.key']")

        val linkedIssueNumber = driver.findElements(cardViewSelector).size;

        val submitButton = driver
            .wait(
                visibilityOfElementLocated(By
                    .cssSelector("[data-test-id='issue.issue-view.views.issue-base.content.issue-links.add.issue-links-add-view.link-button']")))

        submitButton.click()

        driver
            .wait(
                and(
                    visibilityOfElementLocated(cardViewSelector),
                    invisibilityOf(submitButton),
                    ExpectedCondition {
                        driver
                            .findElements(By.cssSelector("[data-test-id ^= 'issue.fields.status.common.ui.status']")).size ==
                            driver
                                .findElements(cardViewSelector).size
                    },
                    ExpectedCondition {
                        driver
                            .findElements(cardViewSelector).size > linkedIssueNumber
                    }
                ))
    }
}
