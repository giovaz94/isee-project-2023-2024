package io.github.evasim.view.renderables

import io.github.evasim.model.Blob
import io.github.evasim.model.Hawk
import io.github.evasim.utils.resource
import io.github.evasim.view.Renderable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color

private const val ICON_DIMENSION = 48.0
private val doveIcon = Image(resource("ui/images/dove.png").toExternalForm())
private val hawkIcon = Image(resource("ui/images/hawk.png").toExternalForm())

internal val blobRenderable = Renderable<Blob, Node> { blob ->
    // val circleNode = shapeRenderable(background = Color.TRANSPARENT).render(blob.place)
    val imageView = ImageView(if (blob.personality is Hawk) hawkIcon else doveIcon).apply {
        fitWidth = ICON_DIMENSION
        fitHeight = ICON_DIMENSION
        isPreserveRatio = true
        translateX = blob.position.x - ICON_DIMENSION / 2
        translateY = blob.position.y - ICON_DIMENSION / 2
    }
    val sightNode = shapeRenderable(background = Color.YELLOW).render(blob.sight.visibilityArea)
    Group(sightNode, /*circleNode,*/ imageView)
}
