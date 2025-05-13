package io.github.evasim.view.renderables

import io.github.evasim.model.Blob
import io.github.evasim.model.Hawk
import io.github.evasim.model.at
import io.github.evasim.view.Renderable
import io.github.evasim.view.utils.resource
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color

private const val ICON_DIMENSION = 48.0
private val doveIcon = Image(resource("ui/images/dove.png").toExternalForm())
private val hawkIcon = Image(resource("ui/images/hawk.png").toExternalForm())

internal val blobRenderable = Renderable<Blob, Node> {
    val circle = with(shapeRenderable(background = Color.TRANSPARENT)) {
        (shape at position).render()
    }
    val imageView = ImageView(if (personality is Hawk) hawkIcon else doveIcon).apply {
        fitWidth = ICON_DIMENSION
        fitHeight = ICON_DIMENSION
        isPreserveRatio = true
        translateX = position.x - ICON_DIMENSION / 2
        translateY = position.y - ICON_DIMENSION / 2
    }
    val sightNode = with(shapeRenderable(background = Color.YELLOW)) {
        sight.visibilityArea.render()
    }
    Group(sightNode, circle, imageView)
}
