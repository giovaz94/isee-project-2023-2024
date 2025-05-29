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

//    @FXML private lateinit var blobSpeedSlider: Slider
//
//    @FXML private lateinit var foodAmountField: TextField

    private var isCollapsed = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        toggleButton.text = "⟩"
        toggleButton.setOnAction { toggleControlsPane() }
        startButton.setOnAction { onStart() }
        pauseButton.setOnAction { onPause() }
        stopButton.setOnAction { onStop() }
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
            shape = Circle(radius = 1_000.0),
            spawnZones = setOf(
                SpawnZone(HollowCircle(innerRadius = 900.0, outerRadius = 1_000.0), origin),
            ),
            blobsAmount = 120,
            hawkyBlobs = 60,
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
