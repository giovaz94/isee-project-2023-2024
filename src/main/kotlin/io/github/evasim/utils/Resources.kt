package io.github.evasim.utils

import java.net.URL

/**
 * Access the resource at the given [path] in the classpath, possibly within the given [packageStructure].
 * @return the URL of the resource, throwing an [IllegalStateException] if the resource is not found.
 */
fun resource(path: String, packageStructure: String = ""): URL =
    checkNotNull(Thread.currentThread().contextClassLoader.getResource(packageStructure + path)) {
        "Unable to access resource $path"
    }
