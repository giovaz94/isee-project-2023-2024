package io.github.evasim.view.renderables

import io.github.evasim.model.Circle
import io.github.evasim.model.Cone
import io.github.evasim.model.Placed
import io.github.evasim.model.Rectangle
import io.github.evasim.model.Shape
import io.github.evasim.view.Renderable
import javafx.scene.Node
import javafx.scene.paint.Color
import javafx.scene.shape.Circle as JFXCircle
import javafx.scene.shape.Rectangle as JFXRectangle

internal class ShapeRenderable<S : Shape> : Renderable<Placed<S>, Node> {
    override fun Placed<S>.render(): Node {
        return when (val s = shape) {
            is Circle -> {
                JFXCircle(s.radius).apply {
                    translateX = position.x
                    translateY = position.y
                    fill = Color.ORANGE
                    stroke = Color.DARKORANGE
                }
            }
            is Rectangle -> {
                JFXRectangle(s.width, s.height).apply {
                    translateX = position.x
                    translateY = position.y
                    fill = Color.CORNFLOWERBLUE
                    stroke = Color.DARKBLUE
                }
            }
            is Cone -> TODO("Cone rendering not implemented yet")
        }
    }
}

internal fun <S : Shape> Placed<S>.renderWith(renderer: Renderable<Placed<S>, Node>): Node = renderer.run { render() }
