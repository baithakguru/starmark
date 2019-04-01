package guru.baithak.starmark.ui.mainScreen.Groups

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.R

class GroupsAdapter(c : Context , groups : ArrayList<Groups>) : RecyclerView.Adapter<ViewHolderGroups>() {
    val c:Context =c
    val groups:ArrayList<Groups> = groups

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolderGroups {
       return ViewHolderGroups(
           LayoutInflater.from(c).inflate(
               R.layout.viewholder_groups,
               p0,
               false
           ), c
       )
    }

    override fun getItemCount(): Int {
        return groups.size
    }

    override fun onBindViewHolder(p0: ViewHolderGroups, p1: Int) {
        p0.name.text=groups[p1].name+groups[p1].topics.size
        p0.lastActive.text= groups[p1].lastActive
        p0.members.text= groups[p1].member
        p0.item.tag = groups[p1]

        val isMuted : Boolean = groups[p1].isMuted
        val isNoti : Boolean = groups[p1].notify

        if(isMuted){
            p0.isMuted.visibility = View.VISIBLE
            p0.isNoti.background=c.getDrawable(R.color.colorAccent)
        }

        if(!isNoti){
            p0.isNoti.visibility = View.GONE
        }


    }

}


class ViewHolderGroups(val item : View,val c:Context) : RecyclerView.ViewHolder(item){
    val name : TextView = item.findViewById(R.id.groupName) as TextView
    val members : TextView = item.findViewById(R.id.groupMembers) as TextView
    val lastActive : TextView = item.findViewById(R.id.groupActive) as TextView
    val isMuted : ImageView = item.findViewById(R.id.groupIsMuted) as ImageView
    val isNoti : View = item.findViewById(R.id.groupIsNotiAvail)

    init {
        item.setOnClickListener{v->
            val i = Intent(c, EachGroup::class.java)
            i.putExtra(groupName,item.tag as Groups)
            c.startActivity(i)
        }
    }

}