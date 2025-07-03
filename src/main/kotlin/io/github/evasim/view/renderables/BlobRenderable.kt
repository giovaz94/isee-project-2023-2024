package io.github.evasim.view.renderables

import io.github.evasim.model.Blob
import io.github.evasim.model.Hawk
import io.github.evasim.utils.resource
import io.github.evasim.view.RenderableWithContext
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.paint.Color

private val doveIcon = Image(resource(path = "ui/images/dove.png").toExternalForm())
private val hawkIcon = Image(resource(path = "ui/images/hawk.png").toExternalForm())

internal data class BlobRenderableConfig(val showNames: Boolean = false, val iconDimension: Double = 48.0)

internal val blobRenderable = RenderableWithContext<Blob, BlobRenderableConfig, Node> { blob, conf ->
    // val circleNode = shapeRenderable(background = Color.TRANSPARENT).render(blob.place)
    val imageView = ImageView(if (blob.personality is Hawk) hawkIcon else doveIcon).apply {
        fitWidth = conf.iconDimension
        fitHeight = conf.iconDimension
        isPreserveRatio = true
        translateX = blob.position.x - conf.iconDimension / 2
        translateY = blob.position.y - conf.iconDimension / 2
    }
    val sightNode = shapeRenderable(background = Color.YELLOW)
        .render(blob.sight.visibilityArea)
        .apply { opacity = 0.5 }
    val group = Group(sightNode, imageView)
    if (conf.showNames) {
        val nameLabel = Label("${blob.health.current}").apply {
            translateX = blob.position.x - text.length * 2
            translateY = blob.position.y - conf.iconDimension / 2 - 10
        }
        group.children.add(nameLabel)
    }
    group.also { it.viewOrder = blob.position.y }
}
