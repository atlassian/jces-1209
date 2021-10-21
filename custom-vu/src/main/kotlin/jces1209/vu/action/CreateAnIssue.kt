package jces1209.vu.action;

import com.atlassian.performance.tools.jiraactions.api.CREATE_ISSUE
import com.atlassian.performance.tools.jiraactions.api.CREATE_ISSUE_SUBMIT
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import com.atlassian.performance.tools.jiraactions.api.page.form.IssueForm
import com.atlassian.performance.tools.jiraactions.api.page.wait
import jces1209.vu.MeasureType.Companion.CREATE_ISSUE_MODAL
import jces1209.vu.api.issue.IssueApi
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.support.ui.ExpectedConditions.*
import java.time.Duration

class CreateAnIssue(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectMemory: ProjectMemory,
    private val issueApi: IssueApi,
    private val createIssueButtons: List<By>
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    private val summaryLocator = By.id("summary-field")
    override fun run() {
        val project = projectMemory.recall()
        if (project == null) {
            logger.debug("Skipping Create issue action. I have no knowledge of projects.")
            return
        }
        var issueKey: String? = null
        try {
            meter.measure(CREATE_ISSUE) {
                jira.goToDashboard().dismissAllPopups()
                openDialog().fillRequiredFields() // TODO: to be fair, we should pick a random project and random issue type
                jira.driver.wait(
                    condition = elementToBeClickable(summaryLocator),
                    timeout = Duration.ofSeconds(30)
                ).click()
                jira.driver.findElement(summaryLocator).sendKeys("Summary")
                jira.driver.wait(
                    condition = elementToBeClickable(By.xpath("//button[starts-with(@data-testid,'issue-create-commons.ui.assignee-field.assing-to-me-button')]")),
                    timeout = Duration.ofSeconds(10)
                ).click()
                meter.measure(CREATE_ISSUE_SUBMIT) {
                    jira.driver.wait(
                        condition = elementToBeClickable(By.xpath("//button[starts-with(@form,'issue-create')]")),
                        timeout = Duration.ofSeconds(50)
                    ).click()
                    jira.driver.wait(
                        condition = visibilityOfElementLocated(By.xpath("//span[contains(text(),'created')]")),
                        timeout = Duration.ofSeconds(30)
                    )
                }
            }
        } finally {
            if (issueApi.isReady()) {
                issueKey?.let { issueApi.deleteIssue(it) }
            }
        }
    }

    private fun openDialog(): IssueForm {
        val driver = jira.driver
        meter.measure(CREATE_ISSUE_MODAL) {
            driver
                .wait(
                    condition = or(*createIssueButtons.map { elementToBeClickable(it) }.toTypedArray()),
                    timeout = Duration.ofSeconds(10)
                )

            createIssueButtons
                .flatMap { driver.findElements(it) }
                .filter { it.isDisplayed }
                .first()
                .click()
            driver.wait(
                condition = and(
                    visibilityOfElementLocated(By.xpath("//form[starts-with(@id,'issue-create')]")),
                    visibilityOfElementLocated(summaryLocator)
                ),
                timeout = Duration.ofSeconds(30)
            )
            (driver as JavascriptExecutor).executeScript("window.onbeforeunload = null")
        }
        return IssueForm(By.id("issue-create.ui.modal.create-form"), driver)
    }
}
