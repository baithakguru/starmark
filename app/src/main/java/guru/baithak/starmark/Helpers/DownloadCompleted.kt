package guru.baithak.starmark.Helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class DownloadCompleted : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Toast.makeText(context,"Your File has been downloaded",Toast.LENGTH_SHORT).show()
//        Log.i("download",intent.dataString!!.toString())
        Log.i("download",intent.toString())
        Log.i("download",intent.extras!!.toString())
    }
}
