package io.github.evasim.view.controllers

import javafx.fxml.FXML
import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

@Suppress("detekt:all")
internal class MainController : Initializable {

    @FXML private lateinit var simulationViewController: SimulationPaneController

    @FXML private lateinit var controlsViewController: ControlsPaneController

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
    }
}
