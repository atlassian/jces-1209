package jces1209.vu.page.boards.view

import jces1209.vu.wait
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedCondition
import org.openqa.selenium.support.ui.ExpectedConditions
import java.net.URI

abstract class ScrumBacklogPage(
    driver: WebDriver,
    uri: URI
) : BoardPage(
    driver = driver,
    uri = uri
) {
    private val issueLocator = By.className("ghx-summary")
    private val startSprintLocator = By.className("js-sprint-start")

    override fun getTypeLabel(): String {
        return "Backlog"
    }

    private fun sprintContainer() = driver
        .findElements(By.className("js-sprint-container"))
        .last()

    private fun sprintIssuesNumber() = sprintContainer()
        .findElements(issueLocator)
        .size

    private fun backlogContainer() = driver
        .findElement(By.className("ghx-backlog-group"))

    fun backlogIssuesNumber(): Int {
        return backlogContainer()
            .findElements(issueLocator)
            .size
    }

    fun createSprint(): ScrumBacklogPage {
        val sprintContainerLocator = By.className("js-sprint-header")

        val createSprintButton = driver.wait(
            ExpectedConditions.elementToBeClickable(By.className("js-add-sprint"))
        )

        val sprintsNumberBeforeCreate = driver
            .findElements(sprintContainerLocator)
            .size

        createSprintButton.click()

        driver
            .wait(ExpectedCondition {
                driver.findElements(sprintContainerLocator).size > sprintsNumberBeforeCreate
            })

        return this
    }

    fun moveIssueToSprint(): ScrumBacklogPage {
        val sprintIssuesNumberBeforeMove = sprintIssuesNumber()

        val issueFromBacklog = backlogContainer()
            .findElement(issueLocator)

        Actions(driver)
            .dragAndDrop(issueFromBacklog, sprintContainer())
            .perform()

        driver
            .wait(ExpectedConditions.invisibilityOf(issueFromBacklog))

        driver
            .wait(ExpectedCondition {
                sprintIssuesNumber() > sprintIssuesNumberBeforeMove
            })

        return this
    }

    fun isStartSprintEnabled(): Boolean {
        return enabledStartSprintButton().isNotEmpty()
    }

    private fun enabledStartSprintButton(): List<WebElement> {
        return driver
            .findElements(startSprintLocator)
            .filter {
                it.getAttribute("aria-disabled") == null
            }
    }

    fun openStartSprintPopUp(): ScrumBacklogPage {
        enabledStartSprintButton()
            .last()
            .click()

        driver
            .wait(ExpectedConditions.visibilityOfElementLocated(By.id("ghx-dialog-start-sprint")))

        return this
    }

    fun submitStartSprint() {
        driver
            .wait(ExpectedConditions.elementToBeClickable(By.className("button-panel-button")))
            .click()

        driver
            .wait(ExpectedConditions.visibilityOfElementLocated(By.className("ghx-column-header-group")))
    }
}
