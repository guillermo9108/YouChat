package cu.alexgi.youchat.audiowave

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import kotlin.math.abs
import kotlin.math.roundToInt

val MAIN_THREAD = Handler(Looper.getMainLooper())

fun dip(value: Int): Int {
    return (value * Resources.getSystem().displayMetrics.density).roundToInt()
}

fun smoothPaint(color: Int): Paint {
    return Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
    }
}

fun filterPaint(color: Int): Paint {
    return Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        this.color = color
    }
}

fun Int.withAlpha(alpha: Int): Int {
    return Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))
}

inline fun Canvas.transform(block: Canvas.() -> Unit) {
    save()
    block()
    restore()
}

fun Bitmap?.safeRecycle() {
    if (this != null && !this.isRecycled) {
        this.recycle()
    }
}

fun Bitmap?.flush() {
    if (this != null && !this.isRecycled) {
        this.eraseColor(Color.TRANSPARENT)
    }
}

fun Bitmap?.fits(w: Int, h: Int): Boolean {
    return this != null && width == w && height == h
}

fun Bitmap.inCanvas(): Canvas {
    return Canvas(this)
}

fun rectFOf(left: Int, top: Int, right: Int, bottom: Int): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun <T : Number> T.clamp(min: T, max: T): T {
    return when {
        this.toFloat() < min.toFloat() -> min
        this.toFloat() > max.toFloat() -> max
        else -> this
    }
}

val Byte.abs: Byte
    get() = abs(this.toInt()).toByte()

fun ByteArray.paste(other: ByteArray): ByteArray {
    val result = ByteArray(this.size)
    System.arraycopy(other, 0, result, 0, other.size)
    return result
}
