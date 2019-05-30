package guru.baithak.starmark.ui.groups.NewTopic

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.core.ServerValues
import guru.baithak.starmark.Helpers.groupName
import guru.baithak.starmark.Models.Course
import guru.baithak.starmark.Models.Groups

import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.fragment_add_topic.*
import kotlinx.android.synthetic.main.fragment_groups.*
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

        university.addAll(universitySet)

       val universityCallback = object :AdapterView.OnItemSelectedListener{
           override fun onNothingSelected(parent: AdapterView<*>?) {
               TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
           }

           override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
               universityString = university.getItem(position)
               degree.clear()
               degree.addAll(getDegree())
               degree.notifyDataSetChanged()
               spinnerDegree.onItemSelectedListener.onItemSelected(null,null,0,0)
           }
       }

        val degreeCallback=object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                degreeString = degree.getItem(position)
                course.clear()
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
            if(universityString == null || degreeString == null || courseString == null || semesterString == null || subjectString == null ){
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
        val topicDetails : HashMap<String,Any> = HashMap()
        topicDetails.put("createdAt", ServerValue.TIMESTAMP)
        topicDetails.put("detailsTopic", finalTopicString)
        topicDetails.put("lastModified", ServerValue.TIMESTAMP)
        topicDetails.put("createdBy",FirebaseAuth.getInstance().currentUser!!.uid)

        ref.child(key!!).setValue(topicDetails).addOnSuccessListener {
            Snackbar.make(view!!,"Topic Added Successfully",Snackbar.LENGTH_LONG).show()
        }.addOnFailureListener{
            Snackbar.make(view!!,"Error Adding Topic",Snackbar.LENGTH_LONG).show()
        }

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
