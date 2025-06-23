package io.github.evasim.view.renderables

import io.github.evasim.model.Circle
import io.github.evasim.model.Food
import io.github.evasim.view.Renderable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Arc
import javafx.scene.shape.ArcType
import kotlin.math.max

internal val foodRenderable = Renderable<Food, Node> { food ->
    val root = Group()
    when (val s = food.shape) {
        is Circle -> {
            val angleStep = 360.0 / max(1, food.pieces.size)
            food.pieces.forEachIndexed { i, piece ->
                val startAngle = i * angleStep
                val arc = Arc(food.position.x, food.position.y, s.radius, s.radius, startAngle, angleStep).apply {
                    type = ArcType.ROUND
                    fill = piece.collectedBy()?.let { Color.LIGHTSALMON } ?: Color.LIGHTGREEN
                    stroke = Color.DARKGRAY
                    strokeWidth = 1.0
                }
                root.children.add(arc)
            }
        }
        else -> error(
            """
            |Expected a Circle shape for Food, but got $s.
            |This is raised because of laziness other shapes rendering are not implemented yet.
            """.trimIndent(),
        )
    }
    root.also { it.viewOrder = 0.0 }
}
