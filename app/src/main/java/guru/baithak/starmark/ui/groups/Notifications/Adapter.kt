package guru.baithak.starmark.ui.groups.Notifications

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import guru.baithak.starmark.Models.Topic
import guru.baithak.starmark.R

class Adapter(val c:Context,val topics:ArrayList<Topic>) : RecyclerView.Adapter<Adapter.EachTopic>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachTopic {
        return EachTopic(LayoutInflater.from(c).inflate(R.layout.view_holder_topics,p0,false))
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(p0: EachTopic, p1: Int) {
        var displayTopic = topics[p1].topicName
        var subText:String ?=null
        Log.i("topics adapter",topics[p1].topicName.split("/")[0])
        if(topics[p1].topicName.split("/").size==5){
            subText=topics[p1].topicName.split("/")[4]
            displayTopic=topics[p1].topicName.split("/")[2]
        }
        p0.topicHead.text = displayTopic
        if(subText==null){
            p0.topicSubjects.visibility=View.GONE
        }else{
            p0.topicSubjects.text=subText
        }

    }


    class EachTopic(val view:View): RecyclerView.ViewHolder(view) {
        val topicHead:TextView = view.findViewById(R.id.topicHead)
        val topicSubjects:TextView = view.findViewById(R.id.topicSubHead)
    }
}