package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.action.ProjectSummaryAction
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveIssueKeyMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveJqlMemory
import com.atlassian.performance.tools.jiraactions.api.memories.adaptive.AdaptiveProjectMemory
import jces1209.vu.action.*
import jces1209.vu.memory.BoardPagesMemory
import jces1209.vu.memory.SeededMemory
import jces1209.vu.page.AbstractIssuePage
import jces1209.vu.page.IssueNavigator
import jces1209.vu.page.JiraTips
import jces1209.vu.page.admin.fieldscreen.BrowseFieldScreensPage
import jces1209.vu.page.admin.workflow.BrowseWorkflowsPage
import jces1209.vu.page.admin.workflow.DcBrowseWorkflowsPage
import jces1209.vu.page.bars.side.SideBar
import jces1209.vu.page.bars.topBar.TopBar
import jces1209.vu.page.boards.browse.BrowseBoardsPage
import jces1209.vu.page.customizecolumns.ColumnsEditor
import jces1209.vu.page.dashboard.DashboardPage
import jces1209.vu.page.filters.FiltersPage
import jces1209.vu.page.project.CloudProjectNavigatorPage
import jces1209.vu.page.project.ProjectNavigatorPage
import java.net.URI
import java.util.*

class ScenarioSimilarities(
    private val jira: WebJira,
    private val seededRandom: SeededRandom,
    private val meter: ActionMeter
) {
    val measure = Measure(meter, seededRandom)
    val jqlMemory = AdaptiveJqlMemory(seededRandom)
        .also { it.remember(listOf("order by created DESC")) } // work around https://ecosystem.atlassian.net/browse/JPERF-573
    val issueKeyMemory = AdaptiveIssueKeyMemory(seededRandom)
    val projectMemory = AdaptiveProjectMemory(seededRandom)
    val filtersMemory = SeededMemory<URI>(seededRandom)
    val boardsMemory = BoardPagesMemory(seededRandom)

    fun assembleScenario(
        issuePage: AbstractIssuePage,
        filtersPage: FiltersPage,
        browseWorkflowsPage: BrowseWorkflowsPage,
        browseFieldScreensPage: BrowseFieldScreensPage,
        browseBoardsPage: BrowseBoardsPage,
        dashboardPage: DashboardPage,
        createIssue: Action,
        browseProjects: Action,
        issueNavigator: IssueNavigator,
        columnsEditor: ColumnsEditor,
        topBar: TopBar,
        sideBar: SideBar,
        projectNavigatorPage: ProjectNavigatorPage
    ): List<Action> = assembleScenario(
        createIssue = createIssue,
        workOnDashboard = WorkOnDashboard(
            jira = jira,
            measure = measure,
            projectKeyMemory = projectMemory,
            dashboardPage = dashboardPage,
            viewDashboardsProbability = 1.00f,
            viewDashboardProbability = 1.00f,
            createDashboardAndGadgetProbability = 0.00f // 0.10f if we can mutate data
        ),
        workAnIssue = WorkOnIssue(
            issuePage = issuePage,
            jira = jira,
            measure = measure,
            issueKeyMemory = issueKeyMemory,
            editProbability = 0.00f, // 0.10f if we can mutate data
            commentProbability = 0.00f, // 0.04f if we can mutate data
            linkIssueProbability = 0.00f, // 0.10f if we can mutate data
            attachScreenShotProbability = 0.00f, // 0.04f if we can mutate data
            changeAssigneeProbability = 0.00f, // 0.04f if we can mutate data
            mentionUserProbability = 0.00f, // 0.04f if we can mutate data
            transitionProbability = 0.00f, // 0.04f if we can mutate data
            contextOperationProbability = 0.05f
        ),
        projectSummary = ProjectSummaryAction(
            jira = jira,
            meter = meter,
            projectMemory = projectMemory
        ),
        browseProjects = browseProjects,
        browseFilters = BrowsePopularFilters(
            filters = filtersMemory,
            filtersPage = filtersPage,
            meter = meter
        ),
        browseBoards = BrowseBoards(
            jira = jira,
            browseBoardsPage = browseBoardsPage,
            meter = meter,
            boardsMemory = boardsMemory
        ),
        viewBoard = ViewBoard(
            driver = jira.driver,
            measure = measure,
            boardsMemory = boardsMemory.all,
            issueKeyMemory = issueKeyMemory,
            viewIssueProbability = 0.50f,
            configureBoardProbability = 0.05f,
            contextOperationProbability = 0.05f,
            changeIssueStatusProbability = 0.10f
        ),
        workOnSprint = WorkOnSprint(
            meter = meter,
            backlogsMemory = boardsMemory.backlog,
            sprintsMemory = boardsMemory.sprint,
            jiraTips = JiraTips(jira.driver)
        ),
        browseProjectIssues = BrowseProjectIssues(
            meter = meter,
            projectKeyMemory = projectMemory,
            browseProjectPage = projectNavigatorPage
        ),
        workOnTopBar = WorkOnTopBar(
            topBar = topBar,
            jira = jira,
            meter = meter
        ),
        workOnSearch = WorkOnSearch(
            issueNavigator = issueNavigator,
            jira = jira,
            measure = measure,
            columnsEditor = columnsEditor,
            filters = filtersMemory,
            jqlMemory = jqlMemory,
            issueKeyMemory = issueKeyMemory,
            searchFilterProbability = 0.50f,
            searchJclProbability = 0.05f,
            globalSearchProbability = 0.50f,
            customizeColumnsProbability = 0.05f,
            switchBetweenIssuesProbability = 0.15f
        ),
        workOnTransition = WorkOnTransition(
            measure = measure,
            boardsMemory = boardsMemory.sprint,
            sideBar = sideBar,
            issueNavigator = issueNavigator
        ),
        browseWorkflows = BrowseWorkflows(
            meter = meter,
            browseWorkflowsPage = browseWorkflowsPage
        ),
        browseFieldScreens = BrowseFieldScreens(
            measure = measure,
            browseFieldScreensPage = browseFieldScreensPage
        )
    )

    private fun assembleScenario(
        createIssue: Action,
        workAnIssue: Action,
        projectSummary: Action,
        browseProjects: Action,
        browseFilters: Action,
        browseBoards: Action,
        viewBoard: Action,
        workOnDashboard: Action,
        workOnSprint: WorkOnSprint,
        browseProjectIssues: Action,
        workOnSearch: Action,
        workOnTopBar: Action,
        workOnTransition: Action,
        browseWorkflows: Action,
        browseFieldScreens: Action
    ): List<Action> {
        val exploreData = listOf(browseProjects, browseFilters, browseBoards)
        val spreadOut = mapOf(
            createIssue to 0, // 5 if we can mutate data
            workAnIssue to 55,
            projectSummary to 5,
            browseProjects to 5,
            browseBoards to 5,
            viewBoard to 30,
            workOnDashboard to 5,
            workOnSprint to 0, // 3 if we can mutate data
            browseProjectIssues to 5,
            workOnSearch to 5,
            workOnTopBar to 5,
            workOnTransition to 5,
            browseWorkflows to 5,
            browseFieldScreens to 5
        )
            .map { (action, proportion) -> Collections.nCopies(proportion, action) }
            .flatten()
            .shuffled(seededRandom.random)
        return exploreData + spreadOut
    }
}
