package org.polyfrost.behindyou.client

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.stream.JsonWriter
import org.polyfrost.behindyou.BehindYouConstants
import org.polyfrost.oneconfig.api.config.v1.ConfigManager
import org.polyfrost.oneconfig.api.notifications.v1.NotificationType
import org.polyfrost.oneconfig.api.notifications.v1.Notifications
import org.polyfrost.oneconfig.utils.v1.dsl.openUI
import java.nio.file.Files
import java.nio.file.Path

/**
 * Brings stored configs up to date with the schema [BehindYouConfig] expects
 *
 * A config with no stored version predates versioning and counts as `0`
 * A missing config is left alone so it gets created at [CURRENT_SCHEMA_VERSION]
 *
 * [migrate] rewrites files directly so it must run before [BehindYouConfig] loads
 * All profiles are migrated so switching profiles later stays safe
 */
object ConfigMigrator {
    const val CURRENT_SCHEMA_VERSION = 3

    // one time message per schema version and versions absent here migrate silently
    private val NOTICES = mapOf(
        1 to (NotificationType.ERROR to
            "The mod has been automatically disabled to prevent unwanted behavior. " +
            "Open the config to re-enable it if you want to use it."),
        2 to (NotificationType.INFO to
            "Camera animations now last exactly as long as configured, where they used to finish " +
            "early depending on your framerate. Your animation time has been reset to the default, " +
            "so open the config if you want to change it."),
        3 to (NotificationType.INFO to
            "Minecraft's perspective key (F5) is no longer changed by the mod, so it cycles views " +
            "the way it does without it. Open the config and turn it back on under Keybinds if you " +
            "want the old behavior."),
    )

    private const val SCHEMA_VERSION_KEY = "SCHEMA_VERSION"
    private const val NOTIFIED_SCHEMA_VERSION_KEY = "NOTIFIED_SCHEMA_VERSION"
    private const val IS_ENABLED_KEY = "isEnabled"
    private const val ANIMATION_KEY = "Animation"
    private const val ANIMATION_SPEED_KEY = "speed"

    private const val DEFAULT_ANIMATION_SPEED = 0.4f

    // matches what OneConfig writes so a migration does not reformat the file
    private const val INDENT = "\t"

    private val gson = Gson()

    fun migrate() {
        val profiles = try {
            ConfigManager.profiles()
        } catch (_: Throwable) {
            return
        }

        for (profile in profiles) migrateProfile(profile)
    }

    fun notifyPending() {
        val notified = BehindYouConfig.NOTIFIED_SCHEMA_VERSION
        if (notified >= CURRENT_SCHEMA_VERSION) return

        for ((version, notice) in NOTICES) {
            if (version <= notified || version > CURRENT_SCHEMA_VERSION) continue
            val (type, message) = notice
            Notifications.send(
                BehindYouConstants.DISPLAY_NAME,
                message,
                type,
                duration = 10_000f,
                onClick = Runnable { BehindYouConfig.openUI() },
            )
        }

        BehindYouConfig.NOTIFIED_SCHEMA_VERSION = CURRENT_SCHEMA_VERSION
        BehindYouConfig.save()
    }

    private fun migrateProfile(profile: String) {
        try {
            val file = ConfigManager.profileDir(profile).resolve(BehindYouConstants.CONFIG_ID)
            if (!Files.isRegularFile(file)) return

            val parsed = Files.newBufferedReader(file).use { JsonParser.parseReader(it) }
            if (!parsed.isJsonObject) return
            val config = parsed.asJsonObject

            val stored = storedVersion(config)
            if (stored >= CURRENT_SCHEMA_VERSION) return

            val original = config.deepCopy()

            // version 0 to 1 forces the master switch off so carried over configs opt back in manually
            if (stored < 1) config.addProperty(IS_ENABLED_KEY, false)

            if (stored < 2) {
                config.getAsJsonObject(ANIMATION_KEY)?.addProperty(ANIMATION_SPEED_KEY, DEFAULT_ANIMATION_SPEED)
            }

            val changedOptions = config != original

            // pin notice progress so unseen notices survive but mark delivered when no option changed
            if (!config.has(NOTIFIED_SCHEMA_VERSION_KEY)) {
                val notified = if (changedOptions) stored else CURRENT_SCHEMA_VERSION
                config.addProperty(NOTIFIED_SCHEMA_VERSION_KEY, notified)
            }

            config.addProperty(SCHEMA_VERSION_KEY, CURRENT_SCHEMA_VERSION)
            write(file, config)
        } catch (_: Throwable) {
        }
    }

    private fun write(file: Path, config: JsonObject) {
        Files.newBufferedWriter(file).use { out ->
            JsonWriter(out).use { json ->
                json.setIndent(INDENT)
                gson.toJson(config, json)
            }
        }
    }

    private fun storedVersion(config: JsonObject): Int = try {
        config.get(SCHEMA_VERSION_KEY)?.asInt ?: 0
    } catch (_: Throwable) {
        0
    }
}
