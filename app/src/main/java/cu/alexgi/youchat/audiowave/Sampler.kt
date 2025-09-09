package cu.alexgi.youchat.audiowave

import android.os.AsyncTask

internal object Sampler {

    @JvmStatic
    fun downSampleAsync(raw: ByteArray, chunksCount: Int, callback: (ByteArray) -> Unit) {
        DownSampleTask(raw, chunksCount, callback).execute()
    }

    private class DownSampleTask(
        private val raw: ByteArray,
        private val chunksCount: Int,
        private val callback: (ByteArray) -> Unit
    ) : AsyncTask<Void, Void, ByteArray>() {

        override fun doInBackground(vararg params: Void): ByteArray {
            val downSampled = ByteArray(chunksCount)
            val chunkLength = raw.size / chunksCount
            var chunkMax: Int
            var chunkValue: Int
            for (i in 0 until chunksCount) {
                chunkMax = 0
                for (j in 0 until chunkLength) {
                    chunkValue = raw[i * chunkLength + j].abs.toInt()
                    if (chunkMax < chunkValue) {
                        chunkMax = chunkValue
                    }
                }
                downSampled[i] = chunkMax.toByte()
            }
            return downSampled
        }

        override fun onPostExecute(downSampled: ByteArray) {
            callback(downSampled)
        }
    }
}
