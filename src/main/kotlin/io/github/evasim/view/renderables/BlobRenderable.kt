package io.github.evasim.view.renderables

import io.github.evasim.model.Blob
import io.github.evasim.model.Dove
import io.github.evasim.model.Hawk
import io.github.evasim.view.Renderable
import io.github.evasim.view.utils.resource
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color

private const val ICON_DIMENSION = 48.0
private val doveIcon = resource("ui/images/dove.png")
private val hawkIcon = resource("ui/images/hawk.png")

internal val blobRenderable = Renderable<Blob, Node> {
    val image = when (personality) {
        is Hawk -> Image(hawkIcon.toExternalForm())
        is Dove -> Image(doveIcon.toExternalForm())
    }
    val imageView = ImageView(image).apply {
        fitWidth = ICON_DIMENSION
        fitHeight = ICON_DIMENSION
        isPreserveRatio = true
        translateX = position.x - ICON_DIMENSION / 2
        translateY = position.y - ICON_DIMENSION / 2
    }
    val visionNode = with(shapeRenderable(background = Color.YELLOW)) {
        sight.visibilityArea.render()
    }
    Group(imageView, visionNode)
}
