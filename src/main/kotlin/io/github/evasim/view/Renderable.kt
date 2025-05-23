package io.github.evasim.view

/**
 * A functional type class that represents a renderable object.
 * @param T the type of the object to be rendered.
 * @param R the type of the rendered result.
 */
fun interface Renderable<in T, out R> {

    /** An alias of `render` with named parameter. */
    fun render(t: T): R
}
