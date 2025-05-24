package io.github.evasim.view.controllers

import com.google.common.eventbus.Subscribe
import io.github.evasim.controller.EventSubscriber
import io.github.evasim.controller.SimulatorController
import io.github.evasim.controller.UpdatedWorld
import io.github.evasim.model.Blob
import io.github.evasim.model.Food
import io.github.evasim.view.renderables.blobRenderable
import io.github.evasim.view.renderables.foodRenderable
import io.github.evasim.view.renderables.worldRenderable
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import java.net.URL
import java.util.ResourceBundle

@Suppress("detekt:VarCouldBeVal")
internal class SimulationPaneController : Initializable, EventSubscriber {
    @FXML private lateinit var simulationPane: AnchorPane

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
            scale *= if (event.deltaY > 0) scale + SCALE_DELTA else scale - SCALE_DELTA
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
    }

    @Subscribe
    fun update(updatedWorldEvent: UpdatedWorld) {
        val worldNode = worldRenderable.render(updatedWorldEvent.world)
        val updatedNodes = updatedWorldEvent.world.foods.toSet().plus(updatedWorldEvent.world.blobs.toSet())
            .map { entity ->
                when (entity) {
                    is Food -> foodRenderable.render(entity)
                    is Blob -> blobRenderable.render(entity)
                    else -> error("Unsupported entity type: ${entity::class.simpleName}")
                }
            }.toSet()
        update(updatedNodes + worldNode)
    }

    private fun update(nodes: Set<Node>) = Platform.runLater {
        simulationGroup.children.clear()
        simulationGroup.children.addAll(nodes)
    }

    private companion object {
        private const val SCALE_DELTA = 0.1
    }
}
