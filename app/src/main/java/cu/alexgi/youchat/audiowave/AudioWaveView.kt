package cu.alexgi.youchat.audiowave

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.annotation.IntRange
import androidx.annotation.WorkerThread
import kotlin.math.abs

class AudioWaveView(
    context: Context,
    attrs: AttributeSet
) : View(context, attrs) {

    private val audioWave = AudioWave(context, attrs)

    private var onProgressChanged: ((Float) -> Unit)? = null

    private var duration: Long = 0
    private var progress: Long = 0

    private val samples = Sampler()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AudioWaveView, 0, 0)
        audioWave.waveColor = typedArray.getColor(R.styleable.AudioWaveView_wave_color, 0)
        audioWave.waveBackgroundColor = typedArray.getColor(R.styleable.AudioWaveView_wave_background_color, 0)
        audioWave.waveGap = typedArray.getDimension(R.styleable.AudioWaveView_wave_gap, dip(2f).toFloat())
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        audioWave.onAttached()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        audioWave.onDetached()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.TRANSPARENT)

        if (audioWave.isDirty()) {
            audioWave.drawWave(width, height)
        }

        if (audioWave.waveBackgroundBitmap != null) {
            canvas.drawBitmap(audioWave.waveBackgroundBitmap!!, 0f, 0f, audioWave.waveBackgroundPaint)
        }

        if (audioWave.waveBitmap != null) {
            val progressWidth = if (duration == 0L) 0f else audioWave.waveBitmap!!.width * progress / duration
            canvas.transform {
                clipRect(rectFOf(0, 0, progressWidth.roundToInt(), height))
                drawBitmap(audioWave.waveBitmap!!, 0f, 0f, audioWave.wavePaint)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        audioWave.setBitmapSize(w, h)
        if (samples.isReady()) {
            val waveData = samples.getWaveData(w, h.toFloat())
            if (waveData != null) {
                audioWave.setWaveData(waveData)
            }
        }
    }

    private var downX = 0f
    private var isDragging = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || !isClickable || duration == 0L) {
            return false
        }
        val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                isDragging = false
                downX = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - downX
                if (abs(dx) > touchSlop) {
                    isDragging = true
                }
                val rawProgress = event.x / width
                progress = (rawProgress * duration).toLong().clamp(0, duration)
                onProgressChanged?.invoke(progress.toFloat() / duration.toFloat())
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                if (isDragging) {
                    onProgressChanged?.invoke(progress.toFloat() / duration.toFloat())
                }
                isDragging = false
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    fun setRawData(@WorkerThread rawData: ByteArray) {
        MAIN_THREAD.post {
            samples.setRawData(rawData)
            if (samples.isReady()) {
                val waveData = samples.getWaveData(width, height.toFloat())
                if (waveData != null) {
                    audioWave.setWaveData(waveData)
                }
            }
        }
    }

    fun setProgress(@IntRange(from = 0) progress: Long) {
        if (progress != this.progress) {
            this.progress = progress.clamp(0, duration)
            invalidate()
        }
    }

    fun setDuration(@IntRange(from = 0) duration: Long) {
        this.duration = duration.clamp(0, Long.MAX_VALUE)
    }

    fun setOnProgressChanged(onProgressChanged: (Float) -> Unit) {
        this.onProgressChanged = onProgressChanged
    }
}
