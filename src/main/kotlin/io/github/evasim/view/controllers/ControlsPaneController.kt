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
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("detekt:VarCouldBeVal")
internal class ControlsPaneController : Initializable {

    internal var simulationPaneController: SimulationPaneController? = null

    @FXML private lateinit var controlsPanel: VBox

    @FXML private lateinit var toggleButton: Button

    @FXML private lateinit var startButton: Button

    @FXML private lateinit var stopButton: Button

    @FXML private lateinit var showBlobInfoCheckBox: CheckBox

    @FXML private lateinit var doveCountField: TextField

    @FXML private lateinit var hawkCountField: TextField

    @FXML private lateinit var foodPiecesField: TextField

    @FXML private lateinit var roundMaxDuration: TextField

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        ToggleController(toggleButton, controlsPanel)
        doveCountField.textProperty().addListener { recomputeFoodField() }
        hawkCountField.textProperty().addListener { recomputeFoodField() }
        startButton.setOnAction { onStart() }
        stopButton.setOnAction { onStop() }
        showBlobInfoCheckBox.setOnAction { simulationPaneController?.toggleShowBlobNames() }
    }

    private fun recomputeFoodField() {
        val doves = doveCountField.text.toIntOrNull() ?: 0
        val hawks = hawkCountField.text.toIntOrNull() ?: 0
        foodPiecesField.text = (doves + hawks).toString()
    }

    private fun onStart() {
        val hawkyBlobs = hawkCountField.text.toInt()
        val doveBlobs = doveCountField.text.toInt()
        val maxRoundDuration = roundMaxDuration.text.toIntOrNull()?.toDuration(DurationUnit.SECONDS)
        val shape = Circle.scaleFromInnerElements(hawkyBlobs + doveBlobs)
        val config = World.Companion.Configuration(
            shape = shape,
            spawnZones = setOf(
                SpawnZone(HollowCircle.fromCircle(shape), origin),
            ),
            hawkyBlobs = hawkyBlobs,
            doveBlobs = doveBlobs,
            foodsAmount = foodPiecesField.text.toInt(),
        )
        SimulatorController.start(config, maxRoundDuration)
        simulationPaneController?.newSimulationState(SimulationState.RUNNING)
    }

    private fun onStop() {
        SimulatorController.stop()
        simulationPaneController?.newSimulationState(SimulationState.READY)
    }
}
