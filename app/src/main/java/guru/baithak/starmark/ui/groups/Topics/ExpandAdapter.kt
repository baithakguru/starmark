package guru.baithak.starmark.ui.groups.Topics

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import guru.baithak.starmark.R

class ExpandAdapter(val c:Context,val details:ArrayList<TopicsCollection>,val isTop:Boolean): RecyclerView.Adapter<ExpandAdapter.EachView>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachView {
        return EachView(LayoutInflater.from(c).inflate(R.layout.expandable_child,p0,false))
    }

    override fun getItemCount(): Int {
        return details.size
    }

    override fun onBindViewHolder(p0: EachView, p1: Int) {
        if(isTop){
            p0.head.visibility = View.VISIBLE
            p0.text.visibility = View.GONE
        }
        p0.text.text = details[p1].name
        p0.head.text = details[p1].name
        if(details[p1].isLeaf()){
            p0.expand.visibility = View.GONE
            return
        }
        p0.recycler.layoutManager = LinearLayoutManager(c)
        p0.recycler.adapter = ExpandAdapter(c,details[p1].children,false)

        p0.expand.setOnClickListener {
            when(it.rotation%90){
                0f->{
                    it.animate().rotation(45f)
                    p0.recycler.visibility = View.VISIBLE
                }
                45f->{
                    it.animate().rotation(0f)
                    p0.recycler.visibility = View.GONE
                }


            }


        }

//        p0.recycler.visibility = View.VISIBLE
    }


    class EachView(val item: View) : RecyclerView.ViewHolder(item) {
        val recycler = item.findViewById<RecyclerView>(R.id.innerExpandable)
        val text = item.findViewById<TextView>(R.id.topicName)
        val head = item.findViewById<TextView>(R.id.topicNameHead)
        val expand = item.findViewById<ImageView>(R.id.expandTopic)

    }
}