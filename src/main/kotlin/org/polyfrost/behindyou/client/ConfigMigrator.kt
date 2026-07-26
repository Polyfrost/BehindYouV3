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
 * Brings stored configs up to date with the schema [BehindYouConfig] expects.
 *
 * The schema a config was written against is stored in it as [BehindYouConfig.SCHEMA_VERSION]. A
 * config with no such entry predates versioning and counts as version `0`. A config that does not
 * exist yet is left alone, so it is created from [BehindYouConfig]'s defaults at
 * [CURRENT_SCHEMA_VERSION] and never migrated.
 *
 * [migrate] rewrites config files directly and must therefore run before [BehindYouConfig] is
 * loaded. Every profile is migrated, not just the active one, so switching profiles later cannot
 * land on a config that never got migrated.
 *
 * A migration that changes behaviour the player would not otherwise notice can add a line to
 * [NOTICES]; [notifyPending] then delivers it once the player is actually in a world, and only if
 * the migration really did change one of their options.
 */
object ConfigMigrator {
    const val CURRENT_SCHEMA_VERSION = 1

    /**
     * Messages to show once for the migration that produced each schema version. Versions absent
     * from this map migrate silently, which is the norm.
     */
    private val NOTICES = mapOf(
        1 to "The mod has been automatically disabled to prevent unwanted behavior. " +
            "Open the config to re-enable it if you want to use it.",
    )

    private const val SCHEMA_VERSION_KEY = "SCHEMA_VERSION"
    private const val NOTIFIED_SCHEMA_VERSION_KEY = "NOTIFIED_SCHEMA_VERSION"
    private const val IS_ENABLED_KEY = "isEnabled"

    // Matches the indentation OneConfig itself writes, so a migration does not reformat the file.
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

    /**
     * Shows the notice for every migration applied since the one the player was last told about,
     * then records that they have been told. Safe to call repeatedly; it does nothing once the
     * player is caught up.
     */
    fun notifyPending() {
        val notified = BehindYouConfig.NOTIFIED_SCHEMA_VERSION
        if (notified >= CURRENT_SCHEMA_VERSION) return

        for ((version, notice) in NOTICES) {
            if (version <= notified || version > CURRENT_SCHEMA_VERSION) continue
            Notifications.send(
                BehindYouConstants.NAME,
                notice,
                NotificationType.ERROR,
                duration = 10_000f,
                // Clicking the body also dismisses the toast, so opening the config is all this has to do.
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

            // 0 -> 1: force the master switch off, so players carrying a config over from an
            // earlier version opt back in manually.
            if (stored < 1) config.addProperty(IS_ENABLED_KEY, false)

            val changedOptions = config != original

            // Pin the notice progress to where it was, so migrating does not swallow a notice the
            // player has not seen yet. A migration that left every option as it already was has
            // nothing worth interrupting the player over, so its notice starts out delivered.
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
