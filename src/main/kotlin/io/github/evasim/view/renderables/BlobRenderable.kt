package io.github.evasim.view.renderables

import io.github.evasim.model.Blob
import io.github.evasim.model.Dove
import io.github.evasim.model.Hawk
import io.github.evasim.view.Renderable
import io.github.evasim.view.utils.resource
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView

internal object BlobRenderable : Renderable<Blob, Node> {
    private const val ICON_DIMENSION = 48.0
    private val doveIcon = resource("ui/images/dove.png")
    private val hawkIcon = resource("ui/images/hawk.png")

    override fun Blob.render(): Node {
        val image = when (personality) {
            is Hawk -> Image(hawkIcon.toExternalForm())
            is Dove -> Image(doveIcon.toExternalForm())
        }
        return ImageView(image).apply {
            fitWidth = ICON_DIMENSION
            fitHeight = ICON_DIMENSION
            isPreserveRatio = true
            translateX = position.x
            translateY = position.y
        }
    }
}
