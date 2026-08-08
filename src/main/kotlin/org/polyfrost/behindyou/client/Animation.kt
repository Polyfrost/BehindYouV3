package org.polyfrost.behindyou.client

class Animation(
    var durationNanos: Long,
    var from: Float,
    var to: Float,
) {
    private var startNanos = System.nanoTime()

    var value = from
        private set

    var isFinished = false
        private set

    fun update(): Float {
        if (isFinished) return value

        val passedNanos = System.nanoTime() - startNanos
        val t = (passedNanos.toDouble() / durationNanos).coerceIn(0.0, 1.0).toFloat()
        value = easeOutQuart(t) * (to - from) + from
        if (t >= 1f) isFinished = true
        return value
    }

    fun reset() {
        startNanos = System.nanoTime()
        isFinished = false
        value = from
    }

    fun finishNow() {
        isFinished = true
        value = to
    }

    private fun easeOutQuart(t: Float): Float {
        val inv = 1.0 - t.toDouble()
        return (1.0 - inv * inv * inv * inv).toFloat()
    }
}

val Number.seconds: Long
    get() = (toDouble() * 1.0E9).toLong()
