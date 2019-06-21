package guru.baithak.starmark.ui.groups.Files


import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import de.hdodenhof.circleimageview.CircleImageView
import guru.baithak.starmark.Helpers.objectTypes

import guru.baithak.starmark.R
import guru.baithak.starmark.R.*
import kotlinx.android.synthetic.main.fragment_select_type.*
import java.util.ArrayList
import javax.sql.StatementEvent

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class SelectType : BottomSheetDialogFragment() {

    var listener:View.OnClickListener?=null
    var spinnerListener:AdapterView.OnItemSelectedListener?=null
    var topics:HashMap<String,String>?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(layout.fragment_select_type, container, false)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listen()
    }

    fun listen(){
        val count = selectRoot.childCount
        for(i in 0..1){
            val linear = selectRoot.getChildAt(i) as LinearLayout
            for(j in 0..2) {
                val inner = linear.getChildAt(j) as LinearLayout
//                val tag = (((selectRoot.getChildAt(i) as LinearLayout).getChildAt(j) as LinearLayout).getChildAt(1) as TextView).text
//                ((selectRoot.getChildAt(i) as LinearLayout).getChildAt(j) as LinearLayout).tag = tag
//                ((selectRoot.getChildAt(i) as LinearLayout).getChildAt(j) as LinearLayout).setOnClickListener(listen)
//                val tag  =(inner.getChildAt(1) as TextView).text as String
               (inner.getChildAt(1) as TextView).text  = objectTypes[i*3+j]
                Log.i("useless",tag)
                inner.tag  = i*3+j
                inner.setOnClickListener(listener)
            }
        }

    }




}
