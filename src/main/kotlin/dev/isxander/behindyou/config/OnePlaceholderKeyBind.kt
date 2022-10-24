package dev.isxander.behindyou.config

import cc.polyfrost.oneconfig.config.core.OneKeyBind

/**
 * does nothing but store the key
 */
class OnePlaceholderKeyBind(key: Int) : OneKeyBind(key) {
    override fun isActive(): Boolean {
        return false
    }

    override fun run() {

    }
}