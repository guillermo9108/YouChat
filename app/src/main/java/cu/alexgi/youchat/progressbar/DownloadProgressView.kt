package cu.alexgi.youchat.progressbar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import cu.alexgi.youchat.R

class DownloadProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val circularProgress: ProgressBar
    private val progressImage: TextView

    init {
        View.inflate(context, R.layout.progress_layout, this)
        circularProgress = findViewById(R.id.circularProgress)
        progressImage = findViewById(R.id.progressImage)
    }

    fun showCircularProgress() {
        circularProgress.visibility = View.VISIBLE
        progressImage.visibility = View.GONE
    }

    fun hideCircularProgress() {
        circularProgress.visibility = View.GONE
    }

    fun showProgressImage() {
        progressImage.visibility = View.VISIBLE
        circularProgress.visibility = View.GONE
    }

    fun hideProgressImage() {
        progressImage.visibility = View.GONE
    }

    fun setProgressImage(resId: Int) {
        progressImage.setBackgroundResource(resId)
    }
}
