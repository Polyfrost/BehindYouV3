package c

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModOrigin
import java.io.File
import kotlin.io.path.name

class Test : ClientModInitializer {
    override fun onInitializeClient() {
    }

    fun getFileOfMod(id: String): File? {
        FabricLoader.getInstance().getModContainer(id).let {
            if (it.isPresent) {
                val container = it.get()
                return when (container.origin.kind) {
                    ModOrigin.Kind.PATH -> {
                        container.origin.paths.firstOrNull { file -> file.name.endsWith(".jar") }?.toFile()
                    }
                    else -> {
                        null
                    }
                }
            }
        }
        return null
    }
}