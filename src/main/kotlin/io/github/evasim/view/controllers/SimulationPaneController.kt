package io.github.evasim.view.controllers

import com.google.common.eventbus.Subscribe
import io.github.evasim.controller.SimulatorController
import io.github.evasim.model.Blob
import io.github.evasim.model.Dove
import io.github.evasim.model.Entity
import io.github.evasim.model.EventSubscriber
import io.github.evasim.model.Food
import io.github.evasim.model.Hawk
import io.github.evasim.model.RemoveFood
import io.github.evasim.model.SimulationEnded
import io.github.evasim.model.SimulationStarted
import io.github.evasim.model.UpdatedBlob
import io.github.evasim.model.UpdatedFood
import io.github.evasim.model.UpdatedWorld
import io.github.evasim.model.World
import io.github.evasim.view.renderables.BlobRenderableConfig
import io.github.evasim.view.renderables.blobRenderable
import io.github.evasim.view.renderables.foodRenderable
import io.github.evasim.view.renderables.worldRenderable
import javafx.animation.AnimationTimer
import javafx.animation.Interpolator
import javafx.animation.ParallelTransition
import javafx.animation.ScaleTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.util.Duration
import java.net.URL
import java.util.*
import kotlin.math.abs
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Suppress("detekt:VarCouldBeVal", "detekt:UnusedParameter")
@OptIn(ExperimentalTime::class)
internal class SimulationPaneController : Initializable, EventSubscriber {

    internal var statisticsPaneController: StatisticsPaneController? = null

    @FXML private lateinit var simulationPane: AnchorPane

    @FXML private lateinit var centerViewButton: Button

    @FXML private lateinit var roundLabel: Label

    @FXML private lateinit var simulationStateLabel: Label

    @FXML private lateinit var elapsedTimeLabel: Label

    private val simulationGroup = Pane()
    private var scale = DEFAULT_SCALE
    private var lastMousePoint = Point2D.ZERO
    private var defaultTranslation = Point2D.ZERO
    private var translation = Point2D.ZERO
    private var showBlobNames = false
    private val roundNumber = SimpleIntegerProperty(0)
    private var startedRoundInstant: ObjectProperty<Instant?> = SimpleObjectProperty(null)
    private var activeAnimationTimer: AnimationTimer? = null

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        SimulatorController.register(this)
        setupInfo()
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
        Platform.runLater { centerView() }
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
        if (translation == Point2D.ZERO || isDefaultPosition()) {
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

    private fun setupInfo() {
        roundNumber.addListener { _, _, newValue ->
            onFx { roundLabel.text = newValue.toString() }
        }
        fun startTimer(initialInstant: Instant) = object : AnimationTimer() {
            override fun handle(now: Long) {
                val millis = (Clock.System.now() - initialInstant).inWholeMilliseconds
                elapsedTimeLabel.text = "%02d.%02ds".format(millis / 1_000, (millis % 1_000) / 10)
            }
        }
        startedRoundInstant.addListener { _, oldValue, newValue ->
            activeAnimationTimer?.stop()
            when {
                newValue == null -> onFx { elapsedTimeLabel.text = "00.00s" }
                else -> activeAnimationTimer = startTimer(newValue).also { it.start() }
            }
        }
    }

    internal fun toggleShowBlobNames() {
        showBlobNames = !showBlobNames
    }

    @Subscribe
    fun update(simulationStartedEvent: SimulationStarted) = onFx {
        simulationStateLabel.text = "Running"
        statisticsPaneController?.clearData()
    }

    @Subscribe
    fun update(simulationEndedEvent: SimulationEnded) {
        roundNumber.set(0)
        startedRoundInstant.set(null)
        onFx { simulationStateLabel.text = "Ready" }
    }

    @Subscribe
    fun update(updatedWorldEvent: UpdatedWorld) {
        clearView()
        startedRoundInstant.set(Clock.System.now())
        roundNumber.set(roundNumber.get() + 1)
        val hawky = updatedWorldEvent.world.blobs.count { it.personality is Hawk }
        val doves = updatedWorldEvent.world.blobs.count { it.personality is Dove }
        statisticsPaneController?.updateData(roundNumber.get(), doveBlobs = doves, hawkyBlobs = hawky)
        updatedWorldEvent.world.foods.toSet()
            .plus(updatedWorldEvent.world.blobs.toSet())
            .plus(updatedWorldEvent.world)
            .forEach { updateView(it) }
    }

    @Subscribe
    fun update(updatedBlob: UpdatedBlob) = updateView(updatedBlob.blob)

    @Subscribe
    fun update(updatedFood: UpdatedFood) = updateView(updatedFood.food)

    @Subscribe
    fun update(removedFood: RemoveFood) = removeFromView(removedFood.food)

    private fun clearView() = onFx { simulationGroup.children.clear() }

    private fun updateView(any: Any) = onFx {
        if (any is Entity) simulationGroup.children.removeIf { it.userData == any.id }
        simulationGroup.children.add(any.render())
    }

    private fun removeFromView(entity: Entity) = onFx {
        simulationGroup.children.removeIf { it.userData == entity.id }
    }

    private fun Any.render(): Node = when (this) {
        is Food -> foodRenderable.render(this)
        is Blob -> blobRenderable.render(this, BlobRenderableConfig(showBlobNames))
        is World -> worldRenderable.render(this)
        else -> error("Unsupported type: ${this::class.simpleName}")
    }.also { if (this@render is Entity) it.userData = id }

    private fun onFx(action: () -> Unit) = if (Platform.isFxApplicationThread()) action() else Platform.runLater(action)

    private companion object {
        private const val SCALE_DELTA = 0.8
        private const val DEFAULT_SCALE = 1.0
        private const val TRANSITION_DURATION_MS = 300.0
    }
}
