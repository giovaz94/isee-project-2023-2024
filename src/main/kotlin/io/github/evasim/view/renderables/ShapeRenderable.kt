package io.github.evasim.view.renderables

import io.github.evasim.model.Circle
import io.github.evasim.model.Cone
import io.github.evasim.model.HollowCircle
import io.github.evasim.model.Placed
import io.github.evasim.model.Rectangle
import io.github.evasim.model.Shape
import io.github.evasim.view.Renderable
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.RadialGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import kotlin.math.atan2
import javafx.scene.shape.Circle as JFXCircle
import javafx.scene.shape.Rectangle as JFXRectangle

internal fun shapeRenderable(background: Color = Color.TRANSPARENT) = Renderable<Placed<Shape>, Node> { place ->
    when (val s = place.shape) {
        is Circle -> JFXCircle(s.radius).apply {
            translateX = place.position.x
            translateY = place.position.y
            fill = background
            stroke = Color.DARKGRAY
        }
        is Rectangle -> JFXRectangle(s.width, s.height).apply {
            translateX = place.position.x - s.width / 2
            translateY = place.position.y - s.height / 2
            fill = background
            stroke = Color.DARKGRAY
        }
        is Cone -> Arc().apply {
            val degreeDirection = Math.toDegrees(atan2(-(place.direction?.y ?: 0.0), place.direction?.x ?: 0.0))
            centerX = place.position.x
            centerY = place.position.y
            radiusX = s.radius
            radiusY = s.radius
            startAngle = degreeDirection - s.fovDegrees.value / 2
            length = s.fovDegrees.value
            type = ArcType.ROUND
            fill = RadialGradient(
                0.0,
                0.0,
                place.position.x,
                place.position.y,
                s.radius,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0, background),
                Stop(1.0, Color.TRANSPARENT),
            )
        }
        is HollowCircle -> {
            val outer = JFXCircle(s.outerRadius).apply {
                centerX = place.position.x
                centerY = place.position.y
            }
            val inner = JFXCircle(s.innerRadius).apply {
                centerX = place.position.x
                centerY = place.position.y
            }
            javafx.scene.shape.Shape.subtract(outer, inner).apply {
                fill = background
                stroke = Color.DARKGRAY
            }
        }
    }
}
