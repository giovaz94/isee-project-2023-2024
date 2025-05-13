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

internal fun shapeRenderable(background: Color = Color.TRANSPARENT) = Renderable<Placed<Shape>, Node> {
    when (val s = shape) {
        is Circle -> JFXCircle(s.radius).apply {
            translateX = position.x
            translateY = position.y
            fill = background
            stroke = Color.DARKGRAY
        }
        is Rectangle -> JFXRectangle(s.width, s.height).apply {
            translateX = position.x - s.width / 2
            translateY = position.y - s.height / 2
            fill = background
            stroke = Color.DARKGRAY
        }
        is Cone -> Arc().apply {
            val degreeDirection = Math.toDegrees(atan2(-(direction?.y ?: 0.0), direction?.x ?: 0.0))
            centerX = position.x
            centerY = position.y
            radiusX = s.radius
            radiusY = s.radius
            startAngle = degreeDirection - s.fovDegrees.value / 2
            length = s.fovDegrees.value
            type = ArcType.ROUND
            fill = RadialGradient(
                0.0,
                0.0,
                position.x,
                position.y,
                s.radius,
                false,
                CycleMethod.NO_CYCLE,
                Stop(0.0, background),
                Stop(1.0, Color.TRANSPARENT),
            )
        }
        is HollowCircle -> {
            val outer = JFXCircle(s.outerRadius).apply {
                centerX = position.x
                centerY = position.y
            }
            val inner = JFXCircle(s.innerRadius).apply {
                centerX = position.x
                centerY = position.y
            }
            javafx.scene.shape.Shape.subtract(outer, inner).apply {
                fill = background
                stroke = Color.DARKGRAY
            }
        }
    }
}
