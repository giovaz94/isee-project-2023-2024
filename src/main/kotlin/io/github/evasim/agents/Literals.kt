package io.github.evasim.agents

import kotlin.reflect.KProperty

/** Literals used in agents knowledge base. */
internal object Literals {
    val print: String by OwnName
    val my_position: String by OwnName
    val personality: String by OwnName
    val find_food: String by OwnName
    val food: String by OwnName
    val move_towards: String by OwnName
    val update_velocity: String by OwnName
    val update_direction: String by OwnName
    val reached_food: String by OwnName
}

private object OwnName {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = property.name
}
