package dev.isxander.behindyou.config

import cc.polyfrost.oneconfig.gui.animations.Animation

class Linear(duration: Int, start: Float, end: Float, reverse: Boolean) : Animation(duration.toFloat(), start, end, reverse) {
    override fun animate(x: Float): Float {
        return x
    }
}