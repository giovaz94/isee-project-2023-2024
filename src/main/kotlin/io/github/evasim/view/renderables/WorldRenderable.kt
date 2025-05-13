package io.github.evasim.view.renderables

import io.github.evasim.model.World
import io.github.evasim.model.at
import io.github.evasim.view.Renderable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.paint.Color

private const val SPAWNING_ZONE_COLOR = "cyan"
private const val SPAWNING_ZONE_OPACITY = 0.2

internal val worldRenderable = Renderable<World, Node> {
    val spawningZones = with(shapeRenderable(Color.web(SPAWNING_ZONE_COLOR, SPAWNING_ZONE_OPACITY))) {
        spawnZones.map { it.placedShape.render() }
    }.toSet()
    val boundary = with(shapeRenderable()) {
        (shape at position).render()
    }
    Group(boundary, *spawningZones.toTypedArray())
}
