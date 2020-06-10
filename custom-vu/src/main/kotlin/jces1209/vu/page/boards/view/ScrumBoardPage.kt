package jces1209.vu.page.boards.view

import jces1209.vu.page.boards.sprint.SprintPage
import org.openqa.selenium.WebDriver
import java.net.URI

abstract class ScrumBoardPage(
    driver: WebDriver,
    uri: URI
) : BoardPage(
    driver = driver,
    uri = uri
) {
    abstract fun goToBacklog(): SprintBoardComponent
    abstract fun goToActiveSprint(): SprintPage

    override fun getTypeLabel(): String {
        return "Scrum"
    }
}
