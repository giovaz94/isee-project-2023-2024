package io.github.evasim.view.controllers

import com.google.common.eventbus.Subscribe
import io.github.evasim.controller.EventSubscriber
import io.github.evasim.controller.SimulatorController
import io.github.evasim.controller.UpdatedEntity
import io.github.evasim.controller.UpdatedWorld
import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
import io.github.evasim.model.Food
import io.github.evasim.view.renderables.blobRenderable
import io.github.evasim.view.renderables.foodRenderable
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*

@Suppress("detekt:style")
internal class FXSimulatorViewController : Initializable, EventSubscriber {
    @FXML private lateinit var simulationPane: AnchorPane

    @FXML private lateinit var startButton: Button

    @FXML private lateinit var pauseButton: Button

    @FXML private lateinit var stopButton: Button

    @FXML private lateinit var blobSpeedSlider: Slider

    @FXML private lateinit var foodAmountField: TextField

    private val simulationGroup = Pane()
    private var scale = 1.0
    private var lastMousePoint = Point2D(0.0, 0.0)
    private var translation = Point2D(0.0, 0.0)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        SimulatorController.register(this)
        simulationPane.children.add(simulationGroup)
        AnchorPane.setTopAnchor(simulationGroup, 0.0)
        AnchorPane.setLeftAnchor(simulationGroup, 0.0)
        AnchorPane.setRightAnchor(simulationGroup, 0.0)
        AnchorPane.setBottomAnchor(simulationGroup, 0.0)
        simulationPane.addEventFilter(ScrollEvent.SCROLL) { event ->
            scale *= if (event.deltaY > 0) 1.1 else 0.9
            simulationGroup.scaleX = scale
            simulationGroup.scaleY = scale
            event.consume()
        }
        simulationPane.setOnMousePressed {
            lastMousePoint = Point2D(it.sceneX, it.sceneY)
            simulationPane.cursor = Cursor.CLOSED_HAND
        }
        simulationPane.setOnMouseDragged { event ->
            translation = translation.add(Point2D(event.sceneX, event.sceneY).subtract(lastMousePoint))
            simulationGroup.translateX = translation.x
            simulationGroup.translateY = translation.y
            lastMousePoint = Point2D(event.sceneX, event.sceneY)
        }
        simulationPane.setOnMouseReleased {
            simulationPane.cursor = Cursor.DEFAULT
        }
        startButton.setOnAction { onStart() }
        pauseButton.setOnAction { onPause() }
        stopButton.setOnAction { onStop() }
    }

    @Subscribe
    fun update(updatedWorldEvent: UpdatedWorld) {
//        if (simulationGroup.children.isEmpty()) {
//            with(shapeRenderable(Color.web("cyan", .2))) {
//                updatedWorldEvent.world.spawnZones.map { zone ->
//                    zone.placedShape.render().also { it.userData = zone.hashCode() }
//                }
//            }.forEach { update(it) }
//            with(shapeRenderable()) {
//                update(
//                    (updatedWorldEvent.world.shape at updatedWorldEvent.world.position).render().also {
//                        it.userData = updatedWorldEvent.world.hashCode()
//                    },
//                )
//            }
//        }
        val updatedNode = updatedWorldEvent.world.foods.toSet().plus(updatedWorldEvent.world.blobs.toSet())
            .map { entity ->
                when (entity) {
                    is Food -> with(foodRenderable) { entity.render() }
                    is Blob -> with(blobRenderable) { entity.render() }
                    else -> error("Unsupported entity type: ${entity::class.simpleName}")
                }
            }.toSet()
        update(updatedNode)
    }

    @Subscribe
    fun <E : Entity> update(updatedEntity: UpdatedEntity<E>) {
        error("To not be called")
//        val newNode = when (val e = updatedEntity.entity) {
//            is Food -> with(foodRenderable) { e.render() }
//            is Blob -> with(blobRenderable) { e.render() }
//            else -> error("Unsupported entity type: ${e::class.simpleName}")
//        }
//        update(newNode)
    }

    private fun update(nodes: Set<Node>) = Platform.runLater {
        simulationGroup.children.clear()
        simulationGroup.children.addAll(nodes)
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
