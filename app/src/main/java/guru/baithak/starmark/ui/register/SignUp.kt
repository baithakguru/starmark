package guru.baithak.starmark.ui.register

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import guru.baithak.starmark.Helpers.userName
import guru.baithak.starmark.R
import guru.baithak.starmark.ui.mainScreen.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class SignUp : AppCompatActivity() {

    var token:String?=null
    var progress:ProgressDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        progress= ProgressDialog(this)
        loginPage.setOnClickListener{v->
            startActivity(Intent(this, Login::class.java))
        }
        try {
            signUpPhone.text.append(intent.getStringExtra(userName))
            signUpPhone.isEnabled=false

        }catch (e:Exception){

        }

        signUpButton.setOnClickListener{v->
            if(signUpButton.text.toString().contains("OTP")){
                showProgress("Verifying OTP")
                verifyCode(signUpOtp.text.toString())
                return@setOnClickListener
            }

            if(!Login.isValidPhoneNo(signUpPhone.text.toString())){
                signUpPhone.error= "Please enter a valid number"
                return@setOnClickListener
            }
            showProgress("Getting OTP")
            getOtp(signUpPhone.text.toString())

        }




    }

    fun dismissProgress(){
        progress!!.dismiss()
    }

    private fun getOtp(phone: String) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(p0, p1)
                token = p0
                Snackbar.make(signUpRoot,"OTP has been sent to number", Snackbar.LENGTH_LONG).show()
                signUpButton.text = "Verify OTP"
                signUpOtp.visibility = View.VISIBLE
                dismissProgress()

            }


            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                dismissProgress()
                signInAfterGettingPhoneCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                dismissProgress()
                Log.d("Error Login",p0.toString())
                Toast.makeText(this@SignUp,"We are sorry...Some error occurred from our servers", Toast.LENGTH_LONG).show()
            }
        }
            Toast.makeText(this,phone,Toast.LENGTH_LONG).show()
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+phone,1, TimeUnit.MINUTES,this,callback)
    }


    fun verifyCode(code:String){
        signInAfterGettingPhoneCredential(PhoneAuthProvider.getCredential(token!!,code))
    }

    fun signInAfterGettingPhoneCredential(credential: PhoneAuthCredential?){
        showProgress("Almost there... Getting your credentials")
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        showProgress("Introducing you to our databases...")
                        addUserToDatabase()
                    } else {
                        dismissProgress()
                        Snackbar.make(loginRoot,"Some error occurred", Snackbar.LENGTH_LONG).setAction("TRY AGAIN", View.OnClickListener{ v->
                            showProgress("Verifying OTP")
                            verifyCode(loginOtp.text.toString())
                        }).show()
                    }
                }
    }



    fun addUserToDatabase(){
        var ref =FirebaseDatabase.getInstance().getReference("users/"+FirebaseAuth.getInstance().currentUser!!.uid)
        var data = HashMap<String,Any>()
        data.put("name",signUpName.text.toString())
        data.put("phone",signUpPhone.text.toString())
        ref.setValue(data).addOnSuccessListener {
            dismissProgress()
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    fun getImage(){





    }
    fun showProgress(msg:String){
        progress!!.dismiss()
        progress!!.setTitle("Just a one time hustle... Please be patient")
        progress!!.setMessage(msg)
        progress!!.show()
    }

}
