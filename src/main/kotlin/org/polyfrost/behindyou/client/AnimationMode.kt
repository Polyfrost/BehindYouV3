package org.polyfrost.behindyou.client

enum class AnimationMode(val animateEnter: Boolean, val animateReturn: Boolean) {
    Off(false, false),
    Enter(true, false),
    Return(false, true),
    Both(true, true),
}
