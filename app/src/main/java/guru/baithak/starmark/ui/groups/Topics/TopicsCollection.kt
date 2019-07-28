package guru.baithak.starmark.ui.groups.Topics

import com.google.firebase.database.DataSnapshot

class TopicsCollection(val name:String,var stars:Int=0,val children:ArrayList<TopicsCollection>) {

    fun isLeaf():Boolean{
        if(children.size ==0)
            return true
        return false
    }

    fun getChildrenCount():Int{
        if(isLeaf())
            return 0
        return children.size

    }

}