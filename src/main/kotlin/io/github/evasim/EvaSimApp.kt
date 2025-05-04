package io.github.evasim

import io.github.evasim.controller.SimulatorController
import io.github.evasim.model.Blob
import io.github.evasim.model.Entity
import io.github.evasim.model.Position2D
import io.github.evasim.model.Rectangle
import io.github.evasim.model.Vector2D
import io.github.evasim.model.World
import kotlinx.coroutines.runBlocking

/** EvaSim Application Entry Point object. */
object EvaSimApp {

    /** Main method to start the EvaSim application. */
    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        println("EvaSim Application started.")

        var domain = World.empty(shape = Rectangle(width = 50.0, height = 50.0))
        val blob = Blob(
            id = Entity.Id("PROVA1"),
            shape = Rectangle(width = 10.0, height = 10.0),
            position = Position2D(x = 10.0, y = 210.0),
            velocity = Vector2D(0.0, 0.0),
        )

        domain = domain.addBlob(blob)

        val controller = SimulatorController(domain = domain)
        controller.launchUIAsync()
        controller.render()
    }
}
