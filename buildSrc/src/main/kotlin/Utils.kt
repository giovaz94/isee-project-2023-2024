import org.gradle.api.provider.Provider
import org.gradle.internal.cc.base.logger
import org.gradle.plugin.use.PluginDependency
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.os.OperatingSystem.LINUX
import org.gradle.internal.os.OperatingSystem.MAC_OS
import org.gradle.internal.os.OperatingSystem.WINDOWS

object Utils {

    fun Provider<PluginDependency>.id(): String = this.get().pluginId

    val inCI: Boolean
        get() = System.getenv("CI") == true.toString()

    val onLinux: Boolean
        get() = os().isLinux

    val onMac: Boolean
        get() = os().isMacOsX

    val onWindows: Boolean
        get() = os().isWindows

    private fun os(): OperatingSystem = OperatingSystem.current()

    fun platform() = with(os()) {
        when {
            isLinux -> "linux"
            isMacOsX -> if (System.getProperty("os.arch") == "aarch64") "mac-aarch64" else "mac"
            isWindows -> "win"
            else -> error("Unsupported operating system: $name")
        }
    }

    fun normally(todo: () -> Unit): Normally = Normally(todo)

    class Normally(private val normallyBlock: () -> Unit) {
        infix fun except(condition: () -> Boolean): Conditionally = Conditionally(normallyBlock, condition)
    }

    class Conditionally(private val normallyBlock: () -> Unit, private val condition: () -> Boolean) {
        infix fun where(exceptionalBlock: () -> Unit): ConditionallyWithCause = with(condition()) {
            if (this) exceptionalBlock() else normallyBlock()
            ConditionallyWithCause(this)
        }
    }

    class ConditionallyWithCause(private val condition: Boolean) {
        infix fun cause(reason: String) = if (condition) logger.quiet(reason) else Unit
    }
}
