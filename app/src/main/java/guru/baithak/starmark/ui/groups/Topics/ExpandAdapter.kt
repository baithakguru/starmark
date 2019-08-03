package guru.baithak.starmark.ui.groups.Topics

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import guru.baithak.starmark.R
import java.lang.Exception

class ExpandAdapter(val c:Context,var details:ArrayList<TopicsCollection>,val isTop:Boolean,val from:String,var stars:HashMap<String,Int>,
        val tillNow:String): RecyclerView.Adapter<ExpandAdapter.EachView>(),StarAction {
    override fun changesInStar(key: String, hasMarked: Boolean) {
        var newVal = 0
        stars[key]?.let {
            newVal+=it
        }
        if(hasMarked){
            newVal++
        }else{
            newVal--
        }
        stars[key] = newVal

        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachView {
        return EachView(LayoutInflater.from(c).inflate(R.layout.expandable_child,p0,false),c,this)
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
        p0.item.tag = (from+"/"+ details[p1].name)

        try {
            if(stars.containsKey(details[p1].name)){
                p0.star.visibility = View.VISIBLE
                when(stars[details[p1].name]){
                    0->{
                        p0.star.visibility = View.INVISIBLE
                    }
                    1->{
                        p0.star.setImageDrawable(c.getDrawable(R.drawable.star_1))
                    }

                    2->{
                        p0.star.setImageDrawable(c.getDrawable(R.drawable.star_2))
                    }

                    3->{
                        p0.star.setImageDrawable(c.getDrawable(R.drawable.star_3))
                    }
                    else->{
                        p0.star.setImageDrawable(c.getDrawable(R.drawable.star_many))
                    }


                }


            }else{
                p0.star.visibility = View.INVISIBLE
            }
        }catch (e:Exception){}
        if(details[p1].isLeaf()){
            p0.expand.visibility = View.GONE
            return
        }
        p0.recycler.layoutManager = LinearLayoutManager(c)
        p0.recycler.adapter = ExpandAdapter(c,details[p1].children,false,from,stars,tillNow+"/"+details[p1].name)
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


    class EachView(val item: View,val c:Context,val listener:StarAction) : RecyclerView.ViewHolder(item) {
        val recycler = item.findViewById<RecyclerView>(R.id.innerExpandable)
        val text = item.findViewById<TextView>(R.id.topicName)
        val head = item.findViewById<TextView>(R.id.topicNameHead)
        val expand = item.findViewById<ImageView>(R.id.expandTopic)
        val star = item.findViewById<ImageView>(R.id.starCount)

        init {
            val listener = View.OnClickListener {
                val b = Bundle()
                b.putString("path", it.tag as String)
                val dialog  = OptionsDialog()
                dialog.listener = listener
                dialog.arguments = b
                dialog.show((c as AppCompatActivity).supportFragmentManager,"Item clicked")

            }
            item.setOnClickListener(listener)
        }


    }



}