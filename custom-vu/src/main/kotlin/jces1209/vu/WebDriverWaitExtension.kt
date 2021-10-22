package jces1209.vu

import com.atlassian.performance.tools.jiraactions.api.page.wait
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedCondition
import java.time.Duration

fun <T> WebDriver.wait(
    condition: ExpectedCondition<T>
): T {
    return this.wait(
        condition = condition,
        timeout = Duration.ofSeconds(10)
    )
}

fun <T> WebDriver.wait(
    condition: ExpectedCondition<T>,
    duration: Duration
): T {
    return this.wait(
        condition = condition,
        timeout = duration
    )
}
