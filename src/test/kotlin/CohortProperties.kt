import jces1209.vu.ConfigProperties
import java.io.File
import java.net.URI
import java.util.Properties


class CohortProperties(
    val jira: URI,
    val userName: String,
    val userPassword: String,
    val cohort: String,
    val jiraType: String?,
    val trafficConfigObj: Properties?
) {
    companion object {
        fun load(secretsName: String): CohortProperties {
            val secrets = File("cohort-secrets/").resolve(secretsName)
            val properties = Properties()
            secrets.bufferedReader().use { properties.load(it) }
            val jiraUri = URI(properties.getProperty("jira.uri")!!)
            return CohortProperties(
                jira = jiraUri,
                userName = properties.getProperty("user.name")!!,
                userPassword = properties.getProperty("user.password")!!,
                cohort = properties.getProperty("cohort")!!,
                jiraType = properties.getProperty("jira.type"),
                trafficConfigObj = ConfigProperties.load("${jiraUri.host}.properties")
            )
        }
    }
}
