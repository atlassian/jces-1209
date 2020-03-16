import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.jiraactions.api.scenario.Scenario
import com.atlassian.performance.tools.jiraactions.api.ActionMetric
import com.atlassian.performance.tools.jiraactions.api.ActionResult
import com.atlassian.performance.tools.jiraactions.api.format.MetricCompactJsonFormat
import com.atlassian.performance.tools.jiraactions.api.format.MetricJsonFormat
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.Timeline
import com.atlassian.performance.tools.report.api.WaterfallHighlightReport
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import com.atlassian.performance.tools.workspace.api.TestWorkspace
import jces1209.BenchmarkQuality
import jces1209.SlowAndMeaningful
import jces1209.log.LogConfigurationFactory
import jces1209.vu.JiraCloudScenario
import jces1209.vu.JiraDcScenario
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.junit.Test
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.Executors

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build"))
    private val currentTaskWorkspace = workspace.currentTask

    init {
        ConfigurationFactory.setConfigurationFactory(LogConfigurationFactory(currentTaskWorkspace))
    }

    @Test
    fun shouldComparePerformance() {
        val pool = Executors.newCachedThreadPool()
        val baseline = pool.submitWithLogContext("baseline") {
            benchmark(File("jira-baseline.properties"), JiraDcScenario::class.java, SlowAndMeaningful())
        }
        val experiment = pool.submitWithLogContext("experiment") {
            benchmark(File("jira-experiment.properties"), JiraCloudScenario::class.java, SlowAndMeaningful())
        }
        val results = listOf(baseline, experiment).map { it.get().prepareForJudgement(FullTimeline()) }
        FullReport().dump(
            results = results,
            workspace = currentTaskWorkspace.isolateTest("Compare")
        )
        dumpMegaSlowWaterfalls(results)
    }

    @Test
    fun shouldOnlyProcessGatheredData() {
        val task = "2020-03-06T14-00-16.448"

        val baselineProperties = CohortProperties.load(File("jira-baseline.properties"))
        val experimentProperties = CohortProperties.load(File("jira-experiment.properties"))
        val metricFormat = MetricCompactJsonFormat()
        val timeline = FullTimeline()

        val baselineResult = processResults(
            baselineProperties.cohort,
            task,
            metricFormat,
            timeline
        )

        val experimentResult = processResults(
            experimentProperties.cohort,
            task,
            metricFormat,
            timeline
        )

        FullReport().dump(
            results = listOf(baselineResult, experimentResult),
            workspace = workspace.isolateTask(task).isolateTest("Compare")
        )

        reportByActionResult(task, baselineResult, experimentResult)
        dumpMegaSlowWaterfalls(listOf(baselineResult, experimentResult))
    }

    private fun benchmark(
        propertiesFile: File,
        scenario: Class<out Scenario>,
        benchmarkQuality: BenchmarkQuality
    ): RawCohortResult {
        val properties = CohortProperties.load(propertiesFile)
        val options = loadOptions(properties, scenario, benchmarkQuality)
        val cohort = properties.cohort
        val resultsTarget = currentTaskWorkspace.directory.resolve("vu-results").resolve(cohort)
        val provisioned = benchmarkQuality
            .provide()
            .obtainVus(resultsTarget, currentTaskWorkspace.directory)
        val virtualUsers = provisioned.virtualUsers
        return try {
            virtualUsers.applyLoad(options)
            virtualUsers.gatherResults()
            RawCohortResult.Factory().fullResult(cohort, resultsTarget)
        } catch (e: Exception) {
            virtualUsers.gatherResults()
            RawCohortResult.Factory().failedResult(cohort, resultsTarget, e)
        } finally {
            provisioned.resource.release().get()
        }
    }

    private fun loadOptions(
        properties: CohortProperties,
        scenario: Class<out Scenario>,
        benchmarkQuality: BenchmarkQuality
    ): VirtualUserOptions {
        val target = VirtualUserTarget(
            webApplication = properties.jira,
            userName = properties.userName,
            password = properties.userPassword
        )
        val behavior = benchmarkQuality.behave(scenario)
            .let { VirtualUserBehavior.Builder(it) }
            .avoidLeakingPersonalData(properties.jira)
            .build()
        return VirtualUserOptions(target, behavior)
    }

    private fun VirtualUserBehavior.Builder.avoidLeakingPersonalData(
        uri: URI
    ) = apply {
        if (uri.host.endsWith("atlassian.net")) {
            diagnosticsLimit(0)
        }
    }

    private fun dumpMegaSlowWaterfalls(
        results: List<EdibleResult>
    ) {
        results.forEach { result ->
            val megaSlow = result.actionMetrics.filter { it.duration > Duration.ofMinutes(1) }
            WaterfallHighlightReport().report(
                metrics = megaSlow,
                workspace = currentTaskWorkspace
                    .isolateTest("Mega slow")
                    .directory
                    .resolve(result.cohort)
                    .let { TestWorkspace(it) }
            )
        }
    }


    private fun reportByActionResult(task: String, baselineResult: EdibleResult, experimentResult: EdibleResult) {
        FullReport().dump(
            results = splitByActionResult(baselineResult) + splitByActionResult(experimentResult),
            workspace = workspace.isolateTask(task).isolateTest("By ActionResult")
        )
    }

    private fun splitByActionResult(edibleResult: EdibleResult): List<EdibleResult> {
        val result = ActionResult.values()
            .map { it to mutableListOf<ActionMetric>() }
            .toMap()
        edibleResult.actionMetrics.forEach { (result[it.result] ?: error("Invalid result")).add(it) }

        return result.entries.map { it.value.toEdibleResult("${edibleResult.cohort} ${it.key.name}") }
    }

    private fun processResults(
        cohort: String,
        task: String,
        metricJsonFormat: MetricJsonFormat,
        timeline: Timeline
    ): EdibleResult = RawCohortResult.Factory()
        .fullResult(
            cohort,
            workspace.directory
                .resolve(task)
                .resolve("vu-results")
                .resolve(cohort),
            metricJsonFormat
        )
        .prepareForJudgement(timeline)


    private fun List<ActionMetric>.toEdibleResult(name: String)
        : EdibleResult = EdibleResult.Builder(name).actionMetrics(this).build()

}
