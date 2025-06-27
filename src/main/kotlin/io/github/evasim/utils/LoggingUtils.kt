package io.github.evasim.utils

import io.github.evasim.model.Round
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.logging.Logger

/** The logger instance. */
val Any.logger: Logger
    get() = Logger.getLogger(this::class.java.name)

/**
 * A file logger that calls hooks before and after each round and streams logs to a file.
 */
class RoundLogger(
    logFile: File = File("rounds.log"),
    private val logContentFunction: (Round) -> String,
) {
    private val writer: BufferedWriter

    init {
        try {
            writer = BufferedWriter(FileWriter(logFile, true))
        } catch (e: IOException) {
            throw IOException("Unable to create log file writer", e)
        }
    }

    /** Manually trigger the start of a round. */
    fun write(round: Round) {
        writeLog(logContentFunction(round))
    }

    /** Writes a log line to the file. */
    private fun writeLog(message: String) {
        try {
            writer.write("[${System.currentTimeMillis()}] $message\n")
            writer.flush()
        } catch (e: IOException) {
            logger.severe("Failed to write log: ${e.message}")
        }
    }

    /** Properly close the stream when done. */
    fun close() {
        try {
            writer.close()
        } catch (e: IOException) {
            logger.severe("Failed to close log writer: ${e.message}")
        }
    }
}
