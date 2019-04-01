package guru.baithak.starmark.ui.newGroup.FinalizeGroup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import guru.baithak.starmark.ui.mainScreen.MainActivity
import guru.baithak.starmark.Helpers.selectedIntent
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.newGroup.SelectMembers.AdapterSelectContacts
import guru.baithak.starmark.ui.newGroup.SelectMembers.SelectedAdapter
import kotlinx.android.synthetic.main.activity_new_group.*

class NewGroup : AppCompatActivity() {
    var data : ArrayList<AdapterSelectContacts.Contact>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_group)
        setSupportActionBar(finalizeActionbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        data = intent.getParcelableArrayListExtra(selectedIntent) //as ArrayList<AdapterSelectContacts.Contact>
        data = data as ArrayList<AdapterSelectContacts.Contact>
        if(data!=null){
            viewSetter()
        }else{
            Toast.makeText(this,"Error getting data",Toast.LENGTH_LONG).show()
        }
    }


    fun viewSetter(){

        Toast.makeText(this,"Got  data "+data!!.size,Toast.LENGTH_LONG).show()
        selectedFinalizeRecycler.adapter = SelectedAdapter(this, data!!)
        selectedFinalizeRecycler.layoutManager = GridLayoutManager(this,3)
        finalSelectedCount.text = String.format("%d participants selected",data!!.size)
        fabAddGroupFinal.setOnClickListener{v->
            startActivity(Intent(this, MainActivity::class.java))
        }

    }
}
