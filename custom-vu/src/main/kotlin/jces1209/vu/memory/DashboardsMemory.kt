package jces1209.vu.memory

import com.atlassian.performance.tools.jiraactions.api.SeededRandom
import com.atlassian.performance.tools.jiraactions.api.memories.Memory

class DashboardsMemory(
    private val random: SeededRandom
) : Memory<Dashboard> {
    private val dashboards = mutableSetOf<Dashboard>()

    override fun recall(): Dashboard? {
        if (dashboards.isEmpty()) {
            return null
        }
        return random.pick(dashboards.toList())
    }

    override fun remember(memories: Collection<Dashboard>) {
        dashboards.addAll(memories)
    }
}
