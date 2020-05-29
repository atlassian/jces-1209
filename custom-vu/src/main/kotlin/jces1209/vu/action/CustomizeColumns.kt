package jces1209.vu.action;

import com.atlassian.performance.tools.jiraactions.api.ActionType
import com.atlassian.performance.tools.jiraactions.api.WebJira
import com.atlassian.performance.tools.jiraactions.api.action.Action
import com.atlassian.performance.tools.jiraactions.api.measure.ActionMeter
import jces1209.vu.MeasureType.Companion.CUSTOMIZE_COLUMNS
import jces1209.vu.page.customizecolumns.CustomizeColumns
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class CustomizeColumns(
    private val jira: WebJira,
    private val meter: ActionMeter,
    private val customizeColumns: CustomizeColumns
) : Action {
    private val logger: Logger = LogManager.getLogger(this::class.java)

    override fun run() {
        jira.navigateTo("/issues/?jql=resolution = Unresolved ORDER BY priority DESC")
        meter.measure(
            key = CUSTOMIZE_COLUMNS,
            action = {
                meter.measure(
                    key = ActionType("Customize columns)") { Unit },
                    action = {
                        customizeColumns.openEditor()
                        customizeColumns.selectItems(2)
                        customizeColumns.submitSelection()
                    }
                )
            }
        )
    }
}
