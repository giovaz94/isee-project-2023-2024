package io.github.evasim.view

import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import java.net.URL
import java.util.ResourceBundle

@Suppress("detekt:all")
internal class FXSimulatorViewController : Initializable {
    @FXML private lateinit var simulationPane: AnchorPane

    @FXML private lateinit var startButton: Button

    @FXML private lateinit var pauseButton: Button

    @FXML private lateinit var stopButton: Button

    @FXML private lateinit var blobSpeedSlider: Slider

    @FXML private lateinit var foodAmountField: TextField

    private val simulationGroup = Group()
    private var scale = 1.0

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        simulationPane.children.add(simulationGroup)
        AnchorPane.setTopAnchor(simulationGroup, 0.0)
        AnchorPane.setLeftAnchor(simulationGroup, 0.0)
        AnchorPane.setRightAnchor(simulationGroup, 0.0)
        AnchorPane.setBottomAnchor(simulationGroup, 0.0)
        drawStubObjects()
        simulationPane.addEventFilter(ScrollEvent.SCROLL) { event ->
            val zoomFactor = if (event.deltaY > 0) 1.1 else 0.9
            scale *= zoomFactor
            simulationGroup.scaleX = scale
            simulationGroup.scaleY = scale
            event.consume()
        }
        startButton.setOnAction { onStart() }
        pauseButton.setOnAction { onPause() }
        stopButton.setOnAction { onStop() }
    }

    private fun drawStubObjects() {
        simulationGroup.children.clear()
        // Stub blobs (circles)
        for (i in 0 until 5) {
            val blob = Circle(60.0 + i * 80, 200.0, 30.0, Color.web("#6C63FF", 0.8))
            blob.stroke = Color.web("#222b45")
            blob.strokeWidth = 2.0
            simulationGroup.children.add(blob)
        }
        // Stub food (rectangles)
        for (i in 0 until 8) {
            val food = Rectangle(80.0 + i * 60, 400.0, 18.0, 18.0)
            food.arcWidth = 6.0
            food.arcHeight = 6.0
            food.fill = Color.web("#00B894", 0.85)
            food.stroke = Color.web("#222b45")
            food.strokeWidth = 1.5
            simulationGroup.children.add(food)
        }
    }

    private fun onStart() {
        // TODO: Start simulation logic
    }
    private fun onPause() {
        // TODO: Pause simulation logic
    }
    private fun onStop() {
        // TODO: Stop simulation logic
    }
}
