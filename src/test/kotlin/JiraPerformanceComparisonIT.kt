import com.atlassian.performance.tools.concurrency.api.submitWithLogContext
import com.atlassian.performance.tools.report.api.FullReport
import com.atlassian.performance.tools.report.api.FullTimeline
import com.atlassian.performance.tools.report.api.WaterfallHighlightReport
import com.atlassian.performance.tools.report.api.result.EdibleResult
import com.atlassian.performance.tools.report.api.result.RawCohortResult
import com.atlassian.performance.tools.virtualusers.api.VirtualUserOptions
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserBehavior
import com.atlassian.performance.tools.virtualusers.api.config.VirtualUserTarget
import com.atlassian.performance.tools.workspace.api.RootWorkspace
import com.atlassian.performance.tools.workspace.api.TestWorkspace
import jces1209.BenchmarkQuality
import jces1209.EagerSlowAndMeaningful
import jces1209.log.LogConfigurationFactory
import jces1209.vu.JiraCloudScenario
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.junit.Test
import java.io.File
import java.net.URI
import java.nio.file.Paths
import java.time.Duration
import java.util.concurrent.Executors

class JiraPerformanceComparisonIT {

    private val workspace = RootWorkspace(Paths.get("build")).currentTask
    private val benchmarkQuality: BenchmarkQuality = EagerSlowAndMeaningful()

    init {
        ConfigurationFactory.setConfigurationFactory(LogConfigurationFactory(workspace))
    }

    @Test
    fun shouldComparePerformance() {
        val pool = Executors.newCachedThreadPool()
        val experiment = pool.submitWithLogContext("experiment") {
            benchmark(File("jira-experiment.properties"))
        }
        val results = listOf(experiment).map { it.get().prepareForJudgement(FullTimeline()) }
        FullReport().dump(
            results = results,
            workspace = workspace.isolateTest("Compare")
        )
        dumpMegaSlowWaterfalls(results)
    }

    private fun benchmark(
        propertiesFile: File
    ): RawCohortResult {
        val properties = CohortProperties.load(propertiesFile)
        val options = loadOptions(properties)
        val cohort = properties.cohort
        val resultsTarget = workspace.directory.resolve("vu-results").resolve(cohort)
        val provisioned = benchmarkQuality
            .provide()
            .obtainVus(resultsTarget, workspace.directory)
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

    private fun loadOptions(properties: CohortProperties): VirtualUserOptions {
        val target = VirtualUserTarget(
            webApplication = properties.jira,
            userName = properties.userName,
            password = properties.userPassword
        )
        val behavior = benchmarkQuality.behave(JiraCloudScenario::class.java)
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
                workspace = workspace
                    .isolateTest("Mega slow")
                    .directory
                    .resolve(result.cohort)
                    .let { TestWorkspace(it) }
            )
        }
    }
}
