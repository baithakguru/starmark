package guru.baithak.starmark.ui.groups.Chats

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import guru.baithak.starmark.Models.Topic
import guru.baithak.starmark.R

class TopicListAdapter(val c : Context, val topics:ArrayList<Topic>): RecyclerView.Adapter<TopicListAdapter.EachGroupViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachGroupViewHolder {
        return EachGroupViewHolder(
            LayoutInflater.from(c).inflate(
                R.layout.view_holder_each_topic,
                p0,
                false
            ), c
        )
    }

    override fun getItemCount(): Int {
//        Toast.makeText(c,"Size is "+topics.size,Toast.LENGTH_SHORT).show()
        return topics.size
    }

    override fun onBindViewHolder(p0: EachGroupViewHolder, p1: Int) {
        p0.name.text = topics[p1].topicName
        p0.topics.text = topics[p1].topics
    }


    class EachGroupViewHolder(val item:View,val c:Context):RecyclerView.ViewHolder(item){
        val name : TextView = item.findViewById(R.id.subjectName)
        val topics : TextView = item.findViewById(R.id.eachSubjectTopics)
    }
}
