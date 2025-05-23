package io.github.evasim.utils

import kotlin.reflect.KProperty

/**
 * A delegate that returns the name of the property it is used in.
 * Use this with `val propertyName: String by OwnName` to automatically assign the name of the property to the variable
 * and get automatic updates whenever the property name changes :)
 */
object OwnName {

    /** Returns the name of the property it is used in. */
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = property.name
}
