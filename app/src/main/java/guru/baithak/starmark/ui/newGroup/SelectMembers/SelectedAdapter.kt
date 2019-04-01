package guru.baithak.starmark.ui.newGroup.SelectMembers

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import guru.baithak.starmark.R
import de.hdodenhof.circleimageview.CircleImageView

class SelectedAdapter(val c : Context , val selected : ArrayList<AdapterSelectContacts.Contact>) : RecyclerView.Adapter<SelectedAdapter.ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(c).inflate(
                R.layout.view_holder_selected,
                p0,
                false
            )
        )
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return selected.size
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
//        p0.image.background = c.getDrawable(R.color.white)
        p0.name.text = selected[p1].name
    }



    fun updateData(newData : AdapterSelectContacts.Contact){
        if(newData.selected){
            selected.add(newData)
            notifyItemChanged(selected.size-1)
        }else{
            selected.remove(newData)
            notifyDataSetChanged()
        }

    }


    class ViewHolder(item: View): RecyclerView.ViewHolder(item){
        val image :  CircleImageView = item.findViewById(R.id.circleSelected)
        val name :  TextView = item.findViewById(R.id.selectedName)as TextView
    }
}