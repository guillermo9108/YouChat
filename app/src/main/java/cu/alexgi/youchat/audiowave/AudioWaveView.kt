package cu.alexgi.youchat.audiowave

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.annotation.IntRange
import kotlin.math.roundToInt

internal class AudioWave(
    private val view: AudioWaveView
) {
    var waveColor: Int = 0
        set(value) {
            field = value
            if (field != 0) {
                wavePaint.color = field
                waveBackgroundPaint.color = field
                waveBackgroundPaint.alpha = 50
            }
        }
    var waveBackgroundColor: Int = 0
        set(value) {
            field = value
            if (field != 0) {
                waveBackgroundPaint.color = field
            }
        }
    var waveGap: Float = 0f
    private val wavePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val waveBackgroundPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var waveData: ByteArray? = null
    var waveBitmap: Bitmap? = null
        private set
    var waveBackgroundBitmap: Bitmap? = null
        private set
    private var waveWidth = 0
    private var waveHeight = 0
    private var waveDirty = true

    fun onAttached() {
        wavePaint.color = waveColor
        waveBackgroundPaint.color = waveBackgroundColor
    }

    fun onDetached() {
        waveBitmap?.safeRecycle()
        waveBackgroundBitmap?.safeRecycle()
    }

    fun isDirty(): Boolean {
        return waveDirty
    }

    fun setBitmapSize(width: Int, height: Int) {
        if (waveBitmap.fits(width, height)) {
            return
        }
        waveBitmap?.safeRecycle()
        waveBackgroundBitmap?.safeRecycle()
        waveWidth = width
        waveHeight = height
        waveBitmap = Bitmap.createBitmap(waveWidth, waveHeight, Bitmap.Config.ARGB_8888)
        waveBackgroundBitmap = Bitmap.createBitmap(waveWidth, waveHeight, Bitmap.Config.ARGB_8888)
        waveDirty = true
    }

    fun setWaveData(@IntRange(from = 0) waveData: ByteArray) {
        this.waveData = waveData
        waveDirty = true
    }

    fun drawWave(width: Int, height: Int) {
        if (waveData == null) return
        val halfHeight = (height / 2f).roundToInt()

        waveBackgroundBitmap?.flush()
        val waveBackgroundCanvas = waveBackgroundBitmap?.inCanvas()
        for (i in waveData!!.indices) {
            val h = (waveData!![i].toFloat() * halfHeight).roundToInt()
            val left = (i * waveGap)
            val right = left + wavePaint.strokeWidth
            waveBackgroundCanvas?.drawRect(left, (halfHeight - h), right, (halfHeight + h), waveBackgroundPaint)
        }

        waveBitmap?.flush()
        val waveCanvas = waveBitmap?.inCanvas()
        for (i in waveData!!.indices) {
            val h = (waveData!![i].toFloat() * halfHeight).roundToInt()
            val left = (i * waveGap)
            val right = left + wavePaint.strokeWidth
            waveCanvas?.drawRect(left, (halfHeight - h), right, (halfHeight + h), wavePaint)
        }
        waveDirty = false
    }
}
