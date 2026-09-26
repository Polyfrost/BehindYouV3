plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "26.3" /* [SC] DO NOT EDIT */

stonecutter {
    tasks {
        order("publishModrinth")
    }

    parameters {
        replacements {
            string(eval(current.version, "= 1.8.9")) {
                replace(
                    "net.minecraft.server.Bootstrap",
                    "net.minecraft.Bootstrap"
                )
                replace(
                    "net.minecraft.client.CameraType",
                    "org.polyfrost.behindyou.client.compat.CameraType"
                )
            }
        }
    }
}
