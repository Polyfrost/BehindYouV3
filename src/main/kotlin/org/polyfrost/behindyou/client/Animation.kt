package org.polyfrost.behindyou.client

class Animation(
    var durationNanos: Long,
    var from: Float,
    var to: Float,
) {
    private var passedTime = 0f

    var value = from
        private set

    val isFinished: Boolean
        get() = passedTime >= durationNanos.toFloat()

    fun update(deltaNanos: Long): Float {
        if (passedTime >= durationNanos.toFloat()) return to
        passedTime += deltaNanos.toFloat()
        val t = (passedTime / durationNanos.toFloat()).coerceIn(0f, 1f)
        value = easeOutQuart(t) * (to - from) + from
        return value
    }

    fun reset() {
        passedTime = 0f
        value = from
    }

    fun finishNow() {
        passedTime = durationNanos.toFloat()
        value = to
    }

    private fun easeOutQuart(t: Float): Float {
        val inv = 1.0 - t.toDouble()
        return (1.0 - inv * inv * inv * inv).toFloat()
    }
}

val Number.seconds: Long
    get() = (toDouble() * 1.0E9).toLong()
