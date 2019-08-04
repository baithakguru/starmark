package guru.baithak.starmark.ui.groups.Topics

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Files.Files
import kotlinx.android.synthetic.main.fragment_options_dialog.*

class OptionsDialog : DialogFragment() {

    var path:String?=null
    var alreadyMarked:Boolean?=null
    var listener:StarAction?=null

    val mark = "Mark Subject as important"
    val unMark = "Remove subject as important"

    var ref:DatabaseReference?=null
    var dir=""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        arguments?.let {
            path = it.getString("path")
            val split = path!!.split("/")
            dir  = "media/"+split[0]+(it.getString("dir"))!!
            Log.i("finalDir",dir)
            getData()
        }

        return inflater.inflate(R.layout.fragment_options_dialog, container, false)
    }

    private fun getData() {
        ref = FirebaseDatabase.getInstance().getReference("stars/"+path!!).child(FirebaseAuth.getInstance().currentUser!!.uid)
                ref!!.addListenerForSingleValueEvent(
                object:ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        alreadyMarked = p0.exists()
                        viewSetter()
                    }
                }
        )
    }

    private fun viewSetter() {
        alreadyMarked?.let {
            if(progressBarIsMarked == null){
                return
            }
            progressBarIsMarked.visibility = View.GONE
            rootOptionsTopics.visibility = View.VISIBLE
            if(it){
                markSubjectImp.text = unMark
            }else{
                markSubjectImp.text = mark
            }
            markSubjectImp.setOnClickListener{
                if(markSubjectImp.text.equals(mark)){
                    ref!!.setValue(ServerValue.TIMESTAMP)
                }else{
                    ref!!.removeValue()
                }
                listener?.let {
                    val split = path!!.split("/")
                    it.changesInStar(split[split.size-1],markSubjectImp.text.equals(mark))
                }
                dismiss()
            }
        }
        viewFilesSub.setOnClickListener {
            val dir= dir
            val f = Files()
            val b = Bundle()
            b.putBoolean("fromMain",false)
            b.putString("path",dir)
            f.arguments = b
            ((context)as AppCompatActivity).supportFragmentManager.beginTransaction()
                     .addToBackStack(null).replace(R.id.eachTopicFragment,f).commit()
            dismiss()
        }
//        uploadFilesSub.setOnClickListener {
//
//        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewSetter()
    }
}
