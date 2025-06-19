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
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

@Suppress("detekt:VarCouldBeVal")
internal class ControlsPaneController : Initializable {
    @FXML private lateinit var controlsPanel: VBox

    @FXML private lateinit var toggleButton: Button

    @FXML private lateinit var startButton: Button

    @FXML private lateinit var stopButton: Button

    @FXML private lateinit var showBlobInfoCheckBox: CheckBox

    @FXML private lateinit var doveCountField: TextField

    @FXML private lateinit var hawkCountField: TextField

    internal var simulationPaneController: SimulationPaneController? = null
    private var isCollapsed = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        toggleButton.text = "⟩"
        toggleButton.setOnAction { toggleControlsPane() }
        startButton.setOnAction { onStart() }
        stopButton.setOnAction { onStop() }
        showBlobInfoCheckBox.setOnAction { simulationPaneController?.toggleShowBlobNames() }
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
        // TODO: validate input
        val hawkyBlobs = hawkCountField.text.toInt()
        val doveBlobs = doveCountField.text.toInt()
        val radius = BASE_RADIUS + (hawkyBlobs + doveBlobs) * SCALE_FACTOR
        val config = World.Companion.Configuration(
            shape = Circle(radius),
            spawnZones = setOf(
                SpawnZone(HollowCircle(innerRadius = radius * 0.80, outerRadius = radius), origin),
            ),
            hawkyBlobs = hawkyBlobs,
            doveBlobs = doveBlobs,
        )
        SimulatorController.start(config)
        simulationPaneController?.newSimulationState(SimulationState.RUNNING)
    }

    private fun onStop() {
        SimulatorController.stop()
        simulationPaneController?.newSimulationState(SimulationState.READY)
    }

    private companion object {
        private const val BASE_RADIUS = 200.0
        private const val SCALE_FACTOR = 5.0
    }
}
