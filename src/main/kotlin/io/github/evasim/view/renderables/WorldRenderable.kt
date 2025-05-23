package io.github.evasim.view.renderables

import io.github.evasim.model.World
import io.github.evasim.model.at
import io.github.evasim.model.origin
import io.github.evasim.view.Renderable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color

private const val COLOR = "cyan"
private const val OPACITY = 0.2

internal val worldRenderable = Renderable<World, Node> { world ->
    val spawningZonesNodes = world.spawnZones
        .map { shapeRenderable(background = Color.web(COLOR, OPACITY)).render(it.place) }
        .toSet()
    val boundaryNode = shapeRenderable().render(world.shape at origin)
    Group(boundaryNode, *spawningZonesNodes.toTypedArray())
}
