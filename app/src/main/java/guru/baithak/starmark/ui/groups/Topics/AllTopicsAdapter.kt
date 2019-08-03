package guru.baithak.starmark.ui.groups.Topics

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.R

class AllTopicsAdapter(val c:Context,val groupKey:String,val topics:HashMap<String,String>): RecyclerView.Adapter<AllTopicsAdapter.ViewHolder>() {

    val topicNames = ArrayList<String>()

    init{
        topicNames.addAll(topics.keys)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(c).inflate(R.layout.all_topics_view_holder,p0,false))
    }

    override fun getItemCount(): Int {
        return topicNames.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.topic.text = topicNames[p1]
        p0.item.setOnClickListener {
            val frag = ExistingTopics()
            val b = Bundle()
            b.putString("subjectKey",topics[topicNames[p1]])
            b.putString("groupKey",groupKey)
            b.putString("groupName",topicNames[p1])
            frag.arguments = b
            (c as AppCompatActivity).supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.eachTopicFragment,frag).commit()
        }
    }


    class ViewHolder(val item:View): RecyclerView.ViewHolder(item) {
        val topic = item.findViewById<TextView>(R.id.allTopicName)
    }

}