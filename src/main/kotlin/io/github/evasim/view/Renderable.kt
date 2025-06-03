package io.github.evasim.view

/**
 * A functional type class representing a renderable object where no context is needed.
 * @param T the type of the object to be rendered.
 * @param R the type of the rendered result.
 */
fun interface Renderable<in T, out R> : RenderableWithContext<T, Unit, R> {

    /** Render the given [element]. */
    fun render(element: T): R

    override fun render(element: T, context: Unit): R = render(element)
}

/**
 * A general functional type class representing a renderable object.
 * @param T the type of the object to be rendered.
 * @param R the type of the rendered result.
 * @param C the context used for the rendering.
 */
fun interface RenderableWithContext<in T, in C, out R> {

    /** Render the given [element] with the given [content]. */
    fun render(element: T, context: C): R
}
