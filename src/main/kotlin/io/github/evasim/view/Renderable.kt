package io.github.evasim.view

/**
 * A functional type class that represents a renderable object.
 *
 * @param T the type of the object to be rendered.
 * @param R the type of the rendered result.
 */
fun interface Renderable<in T, out R> {

    /** Renders the given object of type [T] and returns a result of type [R]. */
    fun T.render(): R
}
