package io.github.evasim.view.controllers

import javafx.fxml.FXML
import javafx.fxml.Initializable
import java.net.URL
import java.util.ResourceBundle

@Suppress("detekt:VarCouldBeVal")
internal class MainController : Initializable {

    @FXML private lateinit var simulationPaneController: SimulationPaneController

    @FXML private lateinit var controlsPaneController: ControlsPaneController

    @FXML private lateinit var statisticsPaneController: StatisticsPaneController

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        controlsPaneController.simulationPaneController = simulationPaneController
        simulationPaneController.statisticsPaneController = statisticsPaneController
    }
}
