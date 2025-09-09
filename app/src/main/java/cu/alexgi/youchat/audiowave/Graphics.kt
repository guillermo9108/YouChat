package cu.alexgi.youchat.audiowave

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import kotlin.math.abs
import kotlin.math.roundToInt

internal val MAIN_THREAD = Handler(Looper.getMainLooper())

internal fun smoothPaint(@ColorInt color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    this.color = color
}

internal fun filterPaint(@ColorInt color: Int): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
}

internal inline fun Canvas.transform(crossinline init: Canvas.() -> Unit) {
    save()
    init()
    restore()
}

@Px
internal fun View.dip(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

@ColorInt
internal fun @receiver:ColorInt Int.withAlpha(@Px alpha: Int): Int = Color.argb(alpha, Color.red(this), Color.green(this), Color.blue(this))

internal fun Float.clamp(min: Float, max: Float): Float = Math.max(min, Math.min(this, max))

internal fun rectFOf(left: Int, top: Int, right: Int, bottom: Int): RectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())

internal fun Bitmap?.fits(neededW: Int, neededH: Int): Boolean = this != null && width == neededW && height == neededH

internal fun Bitmap?.safeRecycle() {
    this?.run {
        if (!isRecycled) {
            recycle()
        }
    }
}

internal fun Bitmap?.flush() {
    this?.run {
        eraseColor(Color.TRANSPARENT)
    }
}

internal fun Bitmap.inCanvas(f: Canvas.() -> Unit = {}): Canvas {
    val canvas = Canvas(this)
    canvas.f()
    return canvas
}

internal fun ByteArray.paste(value: ByteArray): ByteArray {
    for (i in value.indices) {
        this[i] = value[i]
    }
    return this
}

internal val Byte.abs: Byte
    get() = abs(this.toInt()).toByte()

internal val Int.abs: Int
    get() = abs(this)
