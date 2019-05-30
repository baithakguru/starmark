package guru.baithak.starmark.ui.groups.groupDetails

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import guru.baithak.starmark.Models.Person
import guru.baithak.starmark.R

class GroupAdapter(val c:Context,val members:ArrayList<Person>): RecyclerView.Adapter<GroupAdapter.EachView>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): EachView {
        return EachView(LayoutInflater.from(c).inflate(R.layout.viewholder_members,p0,false))
    }

    override fun getItemCount(): Int {
       return members.size
    }

    override fun onBindViewHolder(p0: EachView, p1: Int) {
        p0.name.text= members[p1].name
        if(members[p1].isAdmin!!){
            p0.lastSeen.text="Admin"
        }else{
            p0.lastSeen.visibility = View.VISIBLE
        }
    }

    class EachView(val view: View): RecyclerView.ViewHolder(view){
        val name:TextView= view.findViewById(R.id.memberName)
        val lastSeen:TextView= view.findViewById(R.id.memberLastSeen)
    }
}