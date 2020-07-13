package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.BrowseProjectsAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.UserMemory
import com.atlassian.performance.tools.jiraactions.api.scenario.JiraCoreScenario
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import jces1209.vu.action.BrowseProjectIssues
import jces1209.vu.action.CreateAnIssue
import jces1209.vu.action.WorkOnDashboard
import jces1209.vu.page.DcIssueNavigator
import jces1209.vu.page.DcIssuePage
import jces1209.vu.page.bars.side.DcSideBar
import jces1209.vu.page.bars.topBar.dc.DcTopBar
import jces1209.vu.page.boards.browse.dc.DcBrowseBoardsPage
import jces1209.vu.page.customizecolumns.DcColumnsEditor
import jces1209.vu.page.dashboard.dc.DcDashboardPage
import jces1209.vu.page.filters.ServerFiltersPage
import jces1209.vu.page.project.DcProjectNavigatorPage
import org.openqa.selenium.By
import org.openqa.selenium.TakesScreenshot

class JiraDcScenario : Scenario {

    override fun getLogInAction(
        jira: WebJira,
        meter: ActionMeter,
        userMemory: UserMemory
    ): Action {
        return JiraCoreScenario().getLogInAction(jira, meter, userMemory)
    }

    override fun getActions(
        jira: WebJira,
        seededRandom: SeededRandom,
        actionMeter: ActionMeter
    ): List<Action> {
        val meter = ActionMeter.Builder(actionMeter)
            .appendPostMetricHook(
                TakeScreenshotHook.Builder(
                    jira.driver as TakesScreenshot
                ).build())
            .build()
        val similarities = ScenarioSimilarities(jira, seededRandom, meter)
        return similarities.assembleScenario(
            issuePage = DcIssuePage(jira.driver),
            filtersPage = ServerFiltersPage(jira, jira.driver),
            browseBoardsPage = DcBrowseBoardsPage(jira),
            createIssue = CreateAnIssue(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory,
                createIssueButtons = listOf(By.id("create_link"))
            ),
            browseProjects = BrowseProjectsAction(
                jira = jira,
                meter = meter,
                projectMemory = similarities.projectMemory
            ),
            workOnDashboard = WorkOnDashboard(
                jira = jira,
                meter = meter,
                projectKeyMemory = similarities.projectMemory,
                dashboardPage = DcDashboardPage(jira)
            ),
            browseProjectIssues = BrowseProjectIssues(
                jira = jira,
                meter = meter,
                projectKeyMemory = similarities.projectMemory,
                browseProjectPage = DcProjectNavigatorPage(jira.driver)
            ),
            issueNavigator = DcIssueNavigator(jira.driver),
            columnsEditor = DcColumnsEditor(jira.driver),
            topBar = DcTopBar(jira.driver),
            sideBar = DcSideBar(jira)
        )
    }
}
