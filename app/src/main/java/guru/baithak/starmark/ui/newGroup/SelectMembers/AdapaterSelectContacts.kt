package guru.baithak.starmark.ui.newGroup.SelectMembers

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.support.v4.content.res.ResourcesCompat.getColor
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.widget.Toast
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import guru.baithak.starmark.R
import java.util.*
import kotlin.collections.ArrayList

class AdapterSelectContacts(var C:Context, val contacts:ArrayList<Contact>, val callback: ContactSelectedCallback):
        RecyclerView.Adapter<AdapterSelectContacts.Views>(),
        SectionTitleProvider,
        Filterable{
    override fun getSectionTitle(position: Int): String {
        return contacts[position].name[0].toUpperCase().toString()
    }

    companion object {
        var selected:Int =0
    }
    val allContact = ArrayList<Contact>()
    init {
        allContact.addAll(contacts)
        Log.i("filer init",""+allContact.size+"  "+contacts.size)
    }


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Views {

        return Views(
            LayoutInflater.from(C).inflate(
                R.layout.view_holder_each_contact_long,
                p0,
                false
            ), C, callback
        )

    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(p0: Views, p1: Int) {
        p0.name.text=contacts[p1].name
        p0.number.text=contacts[p1].number
        p0.root.tag=contacts[p1]

        if(!contacts[p1].selected){
            p0.root.setBackgroundColor(getColor(C.resources,R.color.white,null))
        }else{
            p0.root.setBackgroundColor(getColor(C.resources,R.color.colorSelected,null))
        }

    }

    public fun dataUpdated(){
        allContact.clear()
        allContact.addAll(contacts)
    }


    override fun getFilter(): Filter {
        return filterObj
    }

    val filterObj = object : Filter() {
               override fun performFiltering(constraint: CharSequence?): FilterResults {
                   val newList = ArrayList<Contact>()
                   if(constraint==null || constraint.trim().length==0){
                       newList.addAll(allContact.toList())
                       Log.i("filer len all", ""+newList.size)

                   }
                   else{
                       for(contact in allContact){
                           if(contact.name.contains(constraint,true))
                               newList.add(contact)
                       }
                   }

                   val results = FilterResults()
                   results.values = newList
                   Log.i("filer", ""+constraint as String?)
                   Log.i("filer len", ""+newList.size)

                   return results
               }

               override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                   contacts.clear()
                   contacts.addAll(results.values as ArrayList<Contact>)
                   notifyDataSetChanged()
               }
           }






    class Views(item : View,C: Context,callback: ContactSelectedCallback):RecyclerView.ViewHolder(item){
        val name:TextView = item.findViewById(R.id.viewHolderNamePerson)
        val number:TextView = item.findViewById(R.id.viewHolderPhonePerson)
        val root =  item.findViewById(R.id.contactSelectRoot) as View

        init {


            root.setOnClickListener{v->
                if(!(root.tag as Contact).selected){
                    if(AdapterSelectContacts.selected >=5){
                        Toast.makeText(C,"Max users selected",Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    AdapterSelectContacts.selected++
                    v.setBackgroundColor(getColor(C.resources,R.color.colorSelected,null))
                    (root.tag as Contact).selected =true
                }else{
                    AdapterSelectContacts.selected--
                    v.setBackgroundColor(getColor(C.resources,R.color.white,null))
                    (root.tag as Contact).selected =false

                }

                callback.personSelected(root.tag as Contact)

//                if(v.background.alpha == 0){
//                    v.background.alpha =100
//                }else{
//                    v.background.alpha =0
//
//
//                }
            }
        }

    }

    class Contact(var name:String ,val number:String,var selected:Boolean = false):Parcelable{
        constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte()
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(number)
            parcel.writeByte(if (selected) 1 else 0)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Contact> {
            override fun createFromParcel(parcel: Parcel): Contact {
                return Contact(parcel)
            }

            override fun newArray(size: Int): Array<Contact?> {
                return arrayOfNulls(size)
            }
        }

    }
}