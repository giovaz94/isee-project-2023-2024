package io.github.evasim.view

import io.github.evasim.controller.Domain
import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
import io.github.evasim.model.Food
import io.github.evasim.model.at
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
import javafx.scene.shape.Shape as JFxShape

/**
 * Interface representing drawable elements in a graphical user interface.
 *
 * This interface defines the contract for rendering simulation-related data onto a graphical canvas or pane.
 * Implementations of this interface are expected to handle the visualization of both the simulation domain as a whole
 * and individual entities within the simulation.
 */
interface DrawableInterface {

    /**
     * Renders the current state of the simulation domain.
     *
     * @param domain The domain representing the simulation state,
     *               which includes all entities and their respective properties
     *               to be visualized in the view.
     */
    fun render(domain: Domain)

    /**
     * Renders a graphical representation of the given entity.
     *
     * @param entity The entity to be rendered. Provides details such as its shape, position, and other properties
     *               used for creating its graphical representation.
     * @return A JavaFX shape object representing the visual depiction of the provided entity.
     */
    fun renderEntity(entity: Entity): JFxShape
}

@Suppress("detekt:all")
internal class FXSimulatorViewController : Initializable, DrawableInterface {
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

    override fun render(domain: Domain) {
        simulationGroup.children.clear()
        val elements = domain.blobs + domain.foods
        elements.forEach {
            println(it)
            simulationGroup.children.add(renderEntity(it))
        }
    }

    override fun renderEntity(entity: Entity): JFxShape {
        val shape = entity.shape
        val placed = shape.at(entity.position)

        return when (entity) {
            is Blob -> {
                val x = placed.position.x
                val y = placed.position.y
                val radius = 30.0
                val color = Color.web("#6C63FF", 0.8)
                val circle = Circle(x, y, radius, color)
                circle.stroke = Color.web("#222b45")
                circle.strokeWidth = 2.0
                circle
            }

            is Food -> {
                val x = placed.position.x
                val y = placed.position.y
                val width = 18.0
                val height = 18.0
                val color = Color.web("#00B894", 0.85)
                val rectangle = Rectangle(x, y, width, height)
                rectangle.arcWidth = 6.0
                rectangle.arcHeight = 6.0
                rectangle.fill = color
                rectangle.stroke = Color.web("#222b45")
                rectangle.strokeWidth = 1.5

                rectangle
            }

            else -> throw IllegalArgumentException("Unknown entity type: ${entity::class.simpleName}")
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
