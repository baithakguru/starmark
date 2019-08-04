package guru.baithak.starmark.ui.groups.Files

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import guru.baithak.starmark.Helpers.DownloadCompleted
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Topics.OptionsDialog
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Adapter(val c: Context, val files: ArrayList<HashMap<String, Any>>): RecyclerView.Adapter<Adapter.ViewHold>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHold {
        return ViewHold(LayoutInflater.from(c).inflate(R.layout.view_holder_files,p0,false),c)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(p0: ViewHold, p1: Int) {
        val names= files[p1]["topic"].toString().split("/")
        var name = names[0]

        try {
            name = names[names.size - 2]
        }
        catch (e:Exception){
        }
        if(name.trim().isEmpty()){
            name ="root"
        }

        p0.head.text=files[p1]["fileName"].toString()
        p0.topic.text = String.format("#%s by %s",name,files[p1]["createdByName"] as String)
        val date =Date(files[p1].get("createdAt")as Long)
        val format = SimpleDateFormat("dd-M-yy")
        p0.time.text = format.format(date)
        p0.v.setOnClickListener {
//            val request = DownloadManager.Request(Uri.parse(files[p1]["url"] as String))
//            Log.i("downloadUrl",(Uri.parse(files[p1]["url"] as String)).toString())
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,files[p1]["fileName"] as String)
//            (c.getSystemService(DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
//            Toast.makeText(c,"Your download is started",Toast.LENGTH_SHORT).show()
//            c.registerReceiver(DownloadCompleted(), IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            val b = Bundle()
            b.putSerializable("data",files[p1])
            val frag = FileOptions()
            frag.arguments = b
            frag.show((c as AppCompatActivity).supportFragmentManager,"FILES_FRAG")

        }
        try {
            p0.stars.visibility = View.VISIBLE
            when(Integer.parseInt(files[p1]["stars"].toString())){
                0->{
                    p0.stars.visibility = View.INVISIBLE
                }
                1->{
                    p0.stars.setImageDrawable(c.getDrawable(R.drawable.star_1))
                }

                2->{
                        p0.stars.setImageDrawable(c.getDrawable(R.drawable.star_2))
                }

                3->{
                        p0.stars.setImageDrawable(c.getDrawable(R.drawable.star_3))
                }

                else->{
                        p0.stars.setImageDrawable(c.getDrawable(R.drawable.star_many))
                }
            }

        }catch (e:Exception)
        {
            p0.stars.visibility = View.INVISIBLE
        }
    }


    class ViewHold(val v:View,val c:Context):RecyclerView.ViewHolder(v){
        val image = v.findViewById<ImageView>(R.id.fileTypeIcon)
        val head = v.findViewById<TextView>(R.id.fileMainHead)
        val topic = v.findViewById<TextView>(R.id.fileTopic)
        val time = v.findViewById<TextView>(R.id.fileUploadedAt)
        val stars = v.findViewById<ImageView>(R.id.fileStars)

    }
}