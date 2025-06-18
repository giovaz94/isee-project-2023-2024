package io.github.evasim.view.controllers

import com.google.common.eventbus.Subscribe
import io.github.evasim.controller.EventSubscriber
import io.github.evasim.controller.RemoveFood
import io.github.evasim.controller.SimulatorController
import io.github.evasim.controller.UpdatedBlob
import io.github.evasim.controller.UpdatedFood
import io.github.evasim.controller.UpdatedWorld
import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
import io.github.evasim.model.Food
import io.github.evasim.view.renderables.BlobRenderableConfig
import io.github.evasim.view.renderables.blobRenderable
import io.github.evasim.view.renderables.foodRenderable
import io.github.evasim.view.renderables.worldRenderable
import javafx.animation.Interpolator
import javafx.animation.ParallelTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*
import kotlin.math.abs

@Suppress("detekt:VarCouldBeVal")
internal class SimulationPaneController : Initializable, EventSubscriber {
    @FXML private lateinit var simulationPane: AnchorPane

    @FXML private lateinit var centerViewButton: Button

    private val simulationGroup = Pane()
    private var scale = DEFAULT_SCALE
    private var lastMousePoint = Point2D.ZERO
    private var defaultTranslation = Point2D.ZERO
    private var translation = Point2D.ZERO
    private var showBlobNames = false

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        SimulatorController.register(this)
        setupPane()
        setupZoomAndPan()
    }

    private fun setupPane() {
        simulationPane.children.add(simulationGroup)
        AnchorPane.setTopAnchor(simulationGroup, 0.0)
        AnchorPane.setLeftAnchor(simulationGroup, 0.0)
        AnchorPane.setRightAnchor(simulationGroup, 0.0)
        AnchorPane.setBottomAnchor(simulationGroup, 0.0)
        simulationPane.widthProperty().addListener { centerCoordinates() }
        simulationPane.heightProperty().addListener { centerCoordinates() }
        Platform.runLater { centerCoordinates() }
    }

    private fun setupZoomAndPan() = simulationPane.apply {
        addEventFilter(ScrollEvent.SCROLL) { event ->
            if (event.deltaY > 0) scale *= SCALE_DELTA else scale /= SCALE_DELTA
            simulationGroup.scaleX = scale
            simulationGroup.scaleY = scale
            event.consume()
        }
        setOnMousePressed {
            lastMousePoint = Point2D(it.sceneX, it.sceneY)
            cursor = Cursor.CLOSED_HAND
        }
        setOnMouseDragged {
            translation = translation.add(Point2D(it.sceneX, it.sceneY).subtract(lastMousePoint))
            simulationGroup.translateX = translation.x
            simulationGroup.translateY = translation.y
            lastMousePoint = Point2D(it.sceneX, it.sceneY)
        }
        setOnMouseReleased { cursor = Cursor.DEFAULT }
        centerViewButton.setOnAction { centerView() }
    }

    private fun centerView() {
        scale = DEFAULT_SCALE
        translation = defaultTranslation
        ParallelTransition(
            ScaleTransition(Duration.millis(TRANSITION_DURATION_MS), simulationGroup).apply {
                toX = DEFAULT_SCALE
                toY = DEFAULT_SCALE
                interpolator = Interpolator.EASE_OUT
            },
            TranslateTransition(Duration.millis(TRANSITION_DURATION_MS), simulationGroup).apply {
                toX = defaultTranslation.x
                toY = defaultTranslation.y
                interpolator = Interpolator.EASE_OUT
            },
        ).play()
    }

    private fun centerCoordinates() {
        val centerX = simulationPane.width / 2.0
        val centerY = simulationPane.height / 2.0
        defaultTranslation = Point2D(centerX, centerY)
        if (translation == Point2D(0.0, 0.0) || isDefaultPosition()) {
            translation = defaultTranslation
            simulationGroup.translateX = translation.x
            simulationGroup.translateY = translation.y
        }
    }

    private fun isDefaultPosition(): Boolean {
        val tolerance = 1.0
        return abs(translation.x - defaultTranslation.x) < tolerance &&
            abs(translation.y - defaultTranslation.y) < tolerance
    }

    internal fun toggleShowBlobNames() {
        showBlobNames = !showBlobNames
    }

    @Subscribe
    fun update(updatedWorldEvent: UpdatedWorld) {
        val worldNode = worldRenderable.render(updatedWorldEvent.world)
        val updatedNodes = updatedWorldEvent.world.foods.toSet().plus(updatedWorldEvent.world.blobs.toSet())
            .map { it.render() }
            .toSet()
        update(updatedNodes + worldNode)
    }

    private fun update(nodes: Set<Node>) = Platform.runLater {
        simulationGroup.children.clear()
        simulationGroup.children.addAll(nodes)
    }

    @Subscribe
    fun update(updatedBlob: UpdatedBlob) = updateEntity(updatedBlob.blob)

    @Subscribe
    fun update(updatedFood: UpdatedFood) = updateEntity(updatedFood.food)

    @Subscribe
    fun update(removedFood: RemoveFood) = removeEntity(removedFood.food)

    private fun updateEntity(entity: Entity) = Platform.runLater {
        simulationGroup.children.find { it.userData == entity.id }?.let { toBeRemoved ->
            simulationGroup.children.remove(toBeRemoved)
            simulationGroup.children.add(entity.render())
        }
    }

    private fun removeEntity(entity: Entity) = Platform.runLater {
        simulationGroup.children.find { it.userData == entity.id }?.let { simulationGroup.children.remove(it) }
    }

    private fun Entity.render(): Node = when (this) {
        is Food -> foodRenderable.render(this)
        is Blob -> blobRenderable.render(this, BlobRenderableConfig(showBlobNames))
        else -> error("Unsupported entity type: ${this::class.simpleName}")
    }.also { it.userData = id }

    private companion object {
        private const val SCALE_DELTA = 0.8
        private const val DEFAULT_SCALE = 1.0
        private const val TRANSITION_DURATION_MS = 300.0
    }
}
