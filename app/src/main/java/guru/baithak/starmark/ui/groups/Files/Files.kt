package guru.baithak.starmark.ui.groups.Files


import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import guru.baithak.starmark.Helpers.*
import guru.baithak.starmark.Models.Groups
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_files.*
import java.lang.Exception


class Files : Fragment() {

    var group:String?=null
    val topics=HashMap<String,String>()
    var topicSelected :String?=null
    var type:Int=0
    val files=ArrayList<HashMap<String,Any>>()
    var path:String?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(arguments!!.getBoolean("fromMain")){
            group = arguments!!.getParcelable<Groups>(groupName).groupKey
            path = "media/"+group
        }else{
            path = arguments!!.getString("path")
        }
        getData()
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectFileFAB.setOnClickListener{ v->
            add(v)
        }
    }

    fun getFiles(dataSnap :DataSnapshot,path:String){
        try {
            if(dataSnap.hasChild("fileName")){
                val data=dataSnap.value as HashMap<String, Any>
                data.put("topic",path)
                files.add(data)
            }else{
                for (child in dataSnap.children){
                    getFiles(child,path+"/"+child.key)
                }
            }
        }catch (e:Exception){

        }
    }

    fun getData(){
        val progress = ProgressDialog(context)
        progress.setMessage("Please wait while we load your files")
        progress.setTitle("Loading index")
        progress.show()

        FirebaseDatabase.getInstance().getReference(path!!).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                progress.dismiss()
                Toast.makeText(context,"Some Error occured while fetching list",Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                topics.clear()
                files.clear()
                Log.i("keyTopic",p0.toString())
//                for (t in p0.child("index").children){
//                    Log.i("keyTopic",t.getValue(String::class.java))
//                    topics.put(t.getValue(String::class.java)!!,t.key!!)
//                }
                progress.dismiss()
                getFiles(p0,"")
//                for(eachTopic in p0.child("files").children){
//                    for (upload in eachTopic.children) {
//                        val data=upload.value as HashMap<String, Any>
//                        data.put("topic",eachTopic.key!!)
//                        files.add(data)
//                    }
//                }
                if(files.size!=0){
                    emptyFiles?.let{it.visibility = View.INVISIBLE}
                }
                fileRecycler?.let{
                    it.adapter = Adapter(context!!,files)
                    val layoutManager = LinearLayoutManager(context)
                    layoutManager.reverseLayout = true
                    layoutManager.stackFromEnd = true
                    it.layoutManager = layoutManager

                }

            }
        })
    }

    fun add(v:View){
        val dialog = SelectType()
        dialog.listener = object :View.OnClickListener{
            override fun onClick(v: View?) {
                val response = v!!.tag as Int
                dialog.dismiss()
                type = response
                find()
            }
        }
        dialog.topics = topics
        dialog.spinnerListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                topicSelected = parent!!.getItemAtPosition(position)as String
            }
        }
        dialog.show(childFragmentManager,"get content")

   }

    fun find(){
        when(type){
            1,0->{////todo add proper types
                val mime= "image/**"
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.type = mime
                startActivityForResult(Intent.createChooser(i,"Choose app to select app"), FILE_PICKER_REQUEST)
            }
            else->{
                Toast.makeText(context,"Hold on... functionality coming soon",Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== FILE_PICKER_REQUEST){
            if(resultCode!=Activity.RESULT_OK || data == null){
                Toast.makeText(context,"Error getting file...Please Try Again",Toast.LENGTH_LONG).show()
            }else{
                upload(data.data!!)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun upload(uri:Uri){
//        var path = "media/"+ group+"/files/"+topicSelected
        val progress = ProgressDialog(context)
        val helper = UploadFile(path!!,object :UploadFile.ResultUpload{
            override fun results(successFull: Boolean, success: String?, error: String?) {
                   progress.dismiss()
                if(successFull){
                    Toast.makeText(context,"File Uploaded successfully",Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(context,"Error Uploading file",Toast.LENGTH_LONG).show()
                    Log.d("Error upload",error)
                }
            }
        },context!!)
        helper.type= objectTypes[type]
        progress.setTitle("Uploading file")
        progress.setMessage("Please wait while we upload your file")
        progress.setCancelable(false)
        progress.show()
        helper.file = uri
        helper.uploadFile()

    }
}
