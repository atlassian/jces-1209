package jces1209

import com.atlassian.performance.tools.dockerinfrastructure.api.jira.Jira
import org.junit.Test
import org.openqa.selenium.PageLoadStrategy
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.logging.LoggingPreferences
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URI
import java.util.logging.Level

/**
 * This class is meant to be used in a local devloop, where Jira is already running.and Chrome is locally available.
 *
 */
class JiraCloudScenarioDevloop : AbstractJiraCloudScenario() {

    @Test
    fun shouldRunScenarioWithoutErrors() {
        val uri = TODO()//"http://tenantUri/"
        val user = TODO()//User("","")

        val jira = object : Jira {
            override fun close() {
            }

            override fun getUri(): URI {
                return URI.create(uri)
            }
        }
        val driver = createBrowser()
        try {
            shouldRunScenarioWithoutErrors(jira, user, driver)
        } finally {
            driver.close()
        }
    }

    private fun createBrowser(): RemoteWebDriver {
        System.setProperty("webdriver.chrome.driver", "chromedriver")

        val browserOptions: ChromeOptions = ChromeOptions()
            .addArguments("--start-maximized")

        val logPrefs = LoggingPreferences()
        logPrefs.enable(LogType.BROWSER, Level.ALL)
        browserOptions.setCapability(CapabilityType.LOGGING_PREFS, logPrefs)
        browserOptions.setCapability(CapabilityType.PAGE_LOAD_STRATEGY, PageLoadStrategy.EAGER)

        return ChromeDriver(browserOptions)
    }

}
