package guru.baithak.starmark.ui.groups.Notifications

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import guru.baithak.starmark.Models.Topic
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Files.Files

class Adapter(val c:Context,topics:ArrayList<Topic>,val isRoot:Boolean,var path:String) : RecyclerView.Adapter<Adapter.EachTopic>(){


    var list = HashMap<String,ArrayList<Topic>>()
    var finalDisplay = ArrayList<String>()
    var adapters = ArrayList<Adapter>()
    init {
        for(t in topics){
            val key = t.topicName.split("/")[0]
            if(!list.containsKey(key)){
                list.put(key, ArrayList<Topic>())
            }
            var left = t.topicName.replaceFirst(key,"")
            if(left.isNotEmpty() && left[0] == '/'){
                left =  left.replaceFirst("/","")
            }

            Log.i("left",left)
            if(left.isNotEmpty()){
                list.get(key)!!.add(Topic(left,t.keyTopic+"/left"))
            }
        }
        finalDisplay.clear()
        adapters.clear()
        for(keys in list.keys){
            finalDisplay.add(keys)
            adapters.add(Adapter(c,list[keys]!!,false,path+"/"+keys))
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachTopic {
        return EachTopic(LayoutInflater.from(c).inflate(R.layout.view_holder_topics,p0,false))
    }

    override fun getItemCount(): Int {
        return finalDisplay.size
    }

    override fun onBindViewHolder(p0: EachTopic, p1: Int) {
        if(!isRoot){
            p0.topicSubjects.visibility=View.VISIBLE
            p0.topicHead.visibility=View.GONE
            p0.topicSubjects.text = finalDisplay[p1]
//            p0.topicSubjects.text = finalDisplay[p1]
        }else{
            p0.topicHead.text = finalDisplay[p1]
            p0.end.visibility = View.VISIBLE

        }
        if(list.get(finalDisplay[p1])!!.size == 0){
            p0.expand.visibility = View.INVISIBLE
        }else{
            p0.recycler.adapter = adapters[p1]
            p0.recycler.layoutManager =LinearLayoutManager(c)
            p0.recycler.visibility = View.GONE
        }
        p0.expand.tag = p1
        p0.expand.setOnClickListener {v->
            if(v.rotation.toInt()%90 == 0){
                makeVisible(p0,v)
            }else if(v.rotation.toInt()%45 ==0){
                hideView(p0,v)
            }


        }

        p0.clickable.setOnClickListener { v->
            val dir = path+"/"+finalDisplay[p1]
            val f = Files()
            val b = Bundle()
            b.putBoolean("fromMain",false)
            b.putString("path",dir)
            f.arguments = b
            ((c)as AppCompatActivity).supportFragmentManager.beginTransaction()
                    .addToBackStack(null).replace(R.id.eachTopicFragment,f).commit()
//            Toast.makeText(c,dir,Toast.LENGTH_LONG).show()
        }


    }

    fun makeVisible(p0: EachTopic, v: View) {
//        p0.recycler.animate().alpha(0f)
        p0.recycler.visibility=View.VISIBLE
//        p0.recycler.animate().alpha(1f)
        v.animate().rotation(v.rotation+45)

    }

    fun hideView(p0: EachTopic, v: View) {
        p0.recycler.visibility = View.GONE
        v.animate().rotation(v.rotation+45)
    }






    class EachTopic(val view:View): RecyclerView.ViewHolder(view) {
        val topicHead:TextView = view.findViewById(R.id.topicHead)
        val topicSubjects:TextView = view.findViewById(R.id.topicSubHead)
        val recycler :RecyclerView = view.findViewById(R.id.innerRecycler)
        val expand :ImageView = view.findViewById(R.id.expandInner)
        val end = view.findViewById<View>(R.id.endLine)
        val clickable = view.findViewById<View>(R.id.clickable)
        }



}