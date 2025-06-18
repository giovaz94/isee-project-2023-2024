package io.github.evasim.view.controllers

import io.github.evasim.controller.SimulatorController
import io.github.evasim.model.Circle
import io.github.evasim.model.HollowCircle
import io.github.evasim.model.SpawnZone
import io.github.evasim.model.World
import io.github.evasim.model.origin
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

@Suppress("detekt:VarCouldBeVal")
internal class ControlsPaneController : Initializable {
    @FXML private lateinit var controlsPanel: VBox

    @FXML private lateinit var toggleButton: Button

    @FXML private lateinit var startButton: Button

    @FXML private lateinit var pauseButton: Button

    @FXML private lateinit var stopButton: Button

    @FXML private lateinit var showBlobNamesCheckBox: CheckBox

    internal var simulationPaneController: SimulationPaneController? = null
    private var isCollapsed = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        toggleButton.text = "⟩"
        toggleButton.setOnAction { toggleControlsPane() }
        startButton.setOnAction { onStart() }
        pauseButton.setOnAction { onPause() }
        stopButton.setOnAction { onStop() }
        showBlobNamesCheckBox.setOnAction { simulationPaneController?.toggleShowBlobNames() }
    }

    private fun toggleControlsPane() {
        isCollapsed = !isCollapsed
        if (isCollapsed) {
            controlsPanel.styleClass.add("collapsed")
            toggleButton.text = "⟨"
        } else {
            controlsPanel.styleClass.remove("collapsed")
            toggleButton.text = "⟩"
        }
    }

    private fun onStart() {
        val config = World.Companion.Configuration(
            shape = Circle(radius = 300.0),
            spawnZones = setOf(
                SpawnZone(HollowCircle(innerRadius = 100.0, outerRadius = 150.0), origin),
            ),
            blobsAmount = 2,
            hawkyBlobs = 0,
        )
        SimulatorController.start(config)
    }

    private fun onPause() {
        // TODO: Pause simulation logic
    }

    private fun onStop() {
        SimulatorController.stop()
    }
}
