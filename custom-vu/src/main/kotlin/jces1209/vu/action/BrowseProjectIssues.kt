package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import jces1209.vu.MeasureType.Companion.BROWSE_PROJECT_ISSUES
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class BrowseProjectIssues(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectKeyMemory: ProjectMemory
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        val projectKey = projectKeyMemory.recall()
        if (projectKey == null) {
            logger.debug("I don't recall any projects keys. Maybe next time I will.")
            return
        }
        meter.measure(BROWSE_PROJECT_ISSUES) {
            jira.driver.navigate().to("/projects/" + projectKey.key + "/issues")
        }
    }
}

