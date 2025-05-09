package io.github.evasim.view.utils

import java.net.URL

internal fun resource(path: String, packageStructure: String = ""): URL =
    checkNotNull(Thread.currentThread().contextClassLoader.getResource(packageStructure + path)) {
        "Unable to access resource $path"
    }
