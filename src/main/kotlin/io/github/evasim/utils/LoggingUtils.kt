package io.github.evasim.utils

import java.util.logging.Logger

/** The logger instance. */
val Any.logger: Logger
    get() = Logger.getLogger(this::class.java.name)
