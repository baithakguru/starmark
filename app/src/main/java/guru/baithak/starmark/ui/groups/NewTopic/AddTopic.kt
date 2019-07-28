package guru.baithak.starmark.ui.groups.NewTopic

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.core.ServerValues
import guru.baithak.starmark.Helpers.groupKey
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.Models.Course
import guru.baithak.starmark.Models.Groups

import guru.baithak.starmark.R
import guru.baithak.starmark.ui.groups.Notifications.Notifications
import guru.baithak.starmark.ui.groups.Topics.ExistingTopics
import kotlinx.android.synthetic.main.fragment_add_topic.*
import kotlinx.android.synthetic.main.fragment_groups.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

class AddTopic : Fragment() {

    var universityString:String?=null
    var degreeString:String?=null
    var courseString:String?=null
    var semesterString:String?=null
    var subjectString:String?=null
    var group:Groups?=null
    var canProced = false

    val topics :ArrayList<Course> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_topic, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!=null){
            group = arguments!!.getParcelable(groupName) as Groups
            Log.d("GROUP",group!!.groupName)
            Log.d("GROUP",group!!.groupKey)
        }else{
            Log.d("GROUP","error getting group")
        }

//        topics.add(Course("Select ","Select ","Select ","Select ","Select"))
        topics.add(Course("Mumbai","BE","COMPS","4th","COA"))
        topics.add(Course("Univesity","Degree","Course","sem","sub"))
        topics.add(Course("Mumbai","BE","COMPS","4th","DBMS"))
        topics.add(Course("Pune","BE","COMPS Pune","4th","COA"))

        val universitySet = LinkedHashSet<String>()
        for (c in topics){
            universitySet.add(c.university)
        }

        val university = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item)
        val degree = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item)
        val course = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item)
        val semester = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item)
        val subject = ArrayAdapter<String>(context!!,android.R.layout.simple_spinner_dropdown_item)

        spinnerUniversity.adapter = university
        spinnerDegree.adapter = degree
        spinnerCourse.adapter = course
        spinnerSemester.adapter = semester
        spinnerSubject.adapter = subject

        university.add("Select University")
        university.addAll(universitySet)

       val universityCallback = object :AdapterView.OnItemSelectedListener{
           override fun onNothingSelected(parent: AdapterView<*>?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

           override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               universityString = university.getItem(position)
               degree.clear()
               degree.add("Select Degree")
               degree.addAll(getDegree())
               degree.notifyDataSetChanged()
               spinnerDegree.onItemSelectedListener.onItemSelected(null,null,0,0)
           }
       }

        val degreeCallback=object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                degreeString = degree.getItem(position)
                course.clear()
                course.add("Select Course")
                course.addAll(getCourse())
                course.notifyDataSetChanged()
                spinnerCourse.onItemSelectedListener.onItemSelected(null,null,0,0)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val courseCallback=object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                courseString = course.getItem(position)
                semester.clear()
                semester.add("Select Semester")
                semester.addAll(getSemester())
                semester.notifyDataSetChanged()
                spinnerSemester.onItemSelectedListener.onItemSelected(null,null,0,0)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val semesterCallback=object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                semesterString = semester.getItem(position)
                subject.clear()
                subject.add("Select Subject")
                subject.addAll(getSubjects())
                subject.notifyDataSetChanged()
                spinnerSubject.onItemSelectedListener.onItemSelected(null,null,0,0)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        val subjectCallback = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                canProced = position != 0
                subjectString=subject.getItem(position)
            }
        }

        spinnerDegree.onItemSelectedListener=degreeCallback

        spinnerSubject.onItemSelectedListener=subjectCallback
        spinnerSemester.onItemSelectedListener=semesterCallback
        spinnerCourse.onItemSelectedListener=courseCallback
        spinnerUniversity.onItemSelectedListener=universityCallback
        addTopicFinal.setOnClickListener{ _->
            addTopicToDb()
        }

    }

    fun addTopicToDb(){
        var finalTopicString  =""
        if(addTopicNaiveTitle.text.isEmpty()){
            if(universityString == null || degreeString == null || courseString == null || semesterString == null || subjectString == null || !canProced){
                Snackbar.make(view!!,"Please Enter Valid Fields",Snackbar.LENGTH_LONG).show()
                return
            }
            else{
                finalTopicString = universityString+"/"+degreeString+"/"+courseString+"/"+semesterString+"/"+subjectString
            }
        }else{
            finalTopicString = addTopicNaiveTitle.text.toString()
        }

        val ref = FirebaseDatabase.getInstance().getReference("groups/"+group!!.groupKey+"/topics")
        val key = ref.push().key
        val topicDetails = HashMap<String,String>()// = HashMap()
        topicDetails.put("groupDetails", finalTopicString)
        topicDetails.put("groupId", group!!.groupKey!!)
        topicDetails.put("UID",FirebaseAuth.getInstance().currentUser!!.uid)

        val jsonRequest = JsonObjectRequest(Request.Method.POST,"https://us-central1-starmark-1-a2e6f.cloudfunctions.net/addTopic", JSONObject(topicDetails),
                Response.Listener<JSONObject> {
                    if(it["status"].equals("success")){
                        view?.let { it1 ->
                            Snackbar.make(it1, "Topic Added Successfully", Snackbar.LENGTH_LONG).show()
                            val arg = Bundle()
                            arg.putString(groupKey,group?.groupKey)
                            val topics = ExistingTopics()
                            topics.arguments = arg

                            fragmentManager!!.beginTransaction().replace(R.id.eachTopicFragment, topics).commit()
                        }
                    }else{
                        view?.let { it1 ->
                            Snackbar.make(it1, it["error"].toString(), Snackbar.LENGTH_LONG).show()
                        }
                    }
                },
                    Response.ErrorListener {
                        view?.let { it1 -> Snackbar.make(it1, "Error Adding Topic", Snackbar.LENGTH_LONG).show() }
                    })

        Volley.newRequestQueue(context!!).add(jsonRequest)
    }

    fun getDegree():LinkedHashSet<String>{
        val degree = LinkedHashSet<String>()

        for (c in topics){
            if(c.university.equals(universityString))
                degree.add(c.degree)
        }

        return degree
    }
    fun getCourse():LinkedHashSet<String>{
        val course = LinkedHashSet<String>()

        for (c in topics){
            Log.i("Course","hdsd" + universityString+"dsdd"+degreeString)
            if(c.university.equals(universityString) && c.degree.equals(degreeString))
                course.add(c.course)
        }

        return course
    }
    fun getSemester():LinkedHashSet<String>{
            val course = LinkedHashSet<String>()

            for (c in topics){
                if(c.university.equals(universityString) && c.degree.equals(degreeString) && c.course.equals(courseString))
                    course.add(c.semester)
            }

            return course
        }
    fun getSubjects():LinkedHashSet<String>{
                val course = LinkedHashSet<String>()

                for (c in topics){
                    if(c.university.equals(universityString) && c.degree.equals(degreeString) && c.course.equals(courseString) && c.semester.equals(semesterString))
                        course.add(c.subject)
                }

                return course
            }

}
