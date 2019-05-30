package guru.baithak.starmark.ui.groups.Files

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import guru.baithak.starmark.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class Adapter(val c: Context, val files: ArrayList<HashMap<String, Any>>): RecyclerView.Adapter<Adapter.ViewHold>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHold {
        return ViewHold(LayoutInflater.from(c).inflate(R.layout.view_holder_files,p0,false))
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(p0: ViewHold, p1: Int) {
        p0.head.text=files[p1].get("createdByName").toString()+" uploaded "+files[p1].get("fileName").toString()
        p0.topic.text = files[p1].get("topic") as String
        val date =Date(files[p1].get("createdAt")as Long)
        val format = SimpleDateFormat("dd-M-yy")
        p0.time.text = format.format(date)
    }


    class ViewHold(val v:View):RecyclerView.ViewHolder(v){
        val image = v.findViewById<ImageView>(R.id.fileTypeIcon)
        val head = v.findViewById<TextView>(R.id.fileMainHead)
        val topic = v.findViewById<TextView>(R.id.fileTopic)
        val time = v.findViewById<TextView>(R.id.fileUploadedAt)
    }
}