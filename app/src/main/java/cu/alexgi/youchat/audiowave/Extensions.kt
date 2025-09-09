package cu.alexgi.youchat.audiowave

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import kotlin.math.abs

val MAIN_THREAD = Handler(Looper.getMainLooper())

fun dip(value: Int) = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP,
    value.toFloat(),
    App.context.resources.displayMetrics
).toInt()

fun Int.withAlpha(alpha: Int): Int {
    return Color.argb(
        alpha,
        Color.red(this),
        Color.green(this),
        Color.blue(this)
    )
}

fun smoothPaint(color: Int): Paint {
    return Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
}

fun filterPaint(color: Int): Paint {
    return Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        style = Paint.Style.FILL
    }
}

inline fun Canvas.transform(block: Canvas.() -> Unit) {
    val checkpoint = save()
    try {
        block()
    } finally {
        restoreToCount(checkpoint)
    }
}

fun Float.clamp(min: Float, max: Float): Float {
    return Math.max(min, Math.min(this, max))
}

fun rectFOf(left: Int, top: Int, right: Int, bottom: Int): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun Bitmap?.fits(width: Int, height: Int): Boolean {
    if (this == null) {
        return false
    }

    return this.width == width && this.height == height
}

fun Bitmap?.safeRecycle() {
    this?.recycle()
}

fun Bitmap?.inCanvas(): Canvas? {
    if (this == null) {
        return null
    }

    return Canvas(this)
}

fun Bitmap?.flush() {
    this?.eraseColor(Color.TRANSPARENT)
}

fun ByteArray.paste(data: ByteArray): ByteArray {
    val arr = this
    data.forEachIndexed { i, value ->
        if (i > arr.size - 1) {
            return@forEachIndexed
        }
        arr[i] = value
    }
    return arr
}

val Byte.abs: Byte
    get() = abs(this.toInt()).toByte()

val Short.abs: Short
    get() = abs(this.toInt()).toShort()
