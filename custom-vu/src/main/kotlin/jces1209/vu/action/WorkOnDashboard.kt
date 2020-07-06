package jces1209.vu.action

import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.ProjectMemory
import jces1209.vu.MeasureType.Companion.CREATE_DASHBOARD
import jces1209.vu.MeasureType.Companion.CREATE_GADGET
import jces1209.vu.MeasureType.Companion.VIEW_DASHBOARD
import jces1209.vu.MeasureType.Companion.VIEW_DASHBOARDS
import jces1209.vu.page.dashboard.DashboardPage
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class WorkOnDashboard(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val projectKeyMemory: ProjectMemory,
    private val dashboardPage: DashboardPage

) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)
    override fun run() {
        viewDashboards()
        val dashboardName = createDashboard()
        createGadget(dashboardPage, dashboardName)
        openDashboard(dashboardPage)
    }

    private fun viewDashboards() {
        meter.measure(
            key = VIEW_DASHBOARDS,
            action = {
                openDashboardsPage()
                    .waitForDashboards()
            }
        )
    }

    private fun openDashboardsPage(): DashboardPage {
        jira.navigateTo("/secure/ConfigurePortalPages!default.jspa?name=")
        return dashboardPage
    }

    private fun createDashboard(): String {
        return meter.measure(
            key = CREATE_DASHBOARD,
            action = {
                dashboardPage.createDashboard()
            }
        )
    }

    private fun createGadget(dashboard: DashboardPage, dashboardName: String) {
        val projectKey = projectKeyMemory.recall()
        if (projectKey == null) {
            logger.debug("I don't recall any project keys. Maybe next time I will.")
            return
        }
        dashboard.selectDashboardIfPresent(dashboardName)
        meter.measure(
            key = CREATE_GADGET,
            action = {
                dashboard.createGadget(projectKey.name)
            }
        )
    }

    private fun openDashboard(dashboard: DashboardPage) {
        openDashboardsPage()
            .waitForDashboards()
            .clickPopularIfPresent()
        meter.measure(
            key = VIEW_DASHBOARD,
            action = {
                dashboard
                    .openDashboard()
                    .waitForGadgetsLoad()
            }
        )
    }
}
