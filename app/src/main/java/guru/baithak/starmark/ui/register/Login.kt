package guru.baithak.starmark.ui.register

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import guru.baithak.starmark.Helpers.sharedPref
import guru.baithak.starmark.Helpers.userExistsUrl
import guru.baithak.starmark.Helpers.userName
import guru.baithak.starmark.Helpers.userNameSharedPref
import guru.baithak.starmark.ui.mainScreen.MainActivity
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {

    var token:String?=null
    var progress:ProgressDialog? = null//ProgressDialog(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(FirebaseAuth.getInstance().currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()

        }
        setContentView(R.layout.activity_login)

        progress = ProgressDialog(this)
        signUp.setOnClickListener { view ->
            startActivity(Intent(this, SignUp::class.java))
        }
        loginButton.setOnClickListener(View.OnClickListener { v: View? ->
            if(loginButton.text.contains("OTP")){
                showProgress("Verifying OTP")
                verifyCode(loginOtp.text.toString())
                return@OnClickListener
            }
            var phoneEditext= findViewById<EditText>(R.id.loginPhone)
            if(!isValidPhoneNo(phoneEditext.text.toString())){
                phoneEditext.error="Enter a valid number"
                return@OnClickListener
            }else{
                phoneEditext.error=null
            }
            showProgress("Checking phone numbers with one at our servers...")
            getDetails(phoneEditext.text.toString())

        })

    }


    fun showProgress(text:String){
        progress!!.dismiss()
        progress!!.setTitle("Just a one time hustle... Please be patient")
        progress!!.setCancelable(false)
        progress!!.setMessage(text)
        progress!!.show()

    }


    fun getDetails(phone:String){
        val queue:RequestQueue = Volley.newRequestQueue(this)

        var params = HashMap<String,String>()
        params.put("phone","+91"+loginPhone.text.toString())

        val request:JsonObjectRequest = JsonObjectRequest(Request.Method.POST, userExistsUrl,JSONObject(params),Response.Listener {
            jsonResp->
            val response:JSONObject = jsonResp
            if(response.getString("status").equals("success")){
                try {
                    val username: String = response.getString("name")

                    usernameLogin.text = "Welcome back \n" + username
                    getSharedPreferences(sharedPref, Context.MODE_PRIVATE).edit().putString(userNameSharedPref,username).commit()
                    usernameLogin.visibility = View.VISIBLE
                }
                catch (e:Exception){

                }
                showProgress("Generating OTP....")

                requestOtp(phone)
                //user exists ....request otp
            }else{
                val i = Intent(this,SignUp::class.java)
                i.putExtra(userName,loginPhone.text.toString())
                startActivity(i)
                //user doesnt exists
                dismissProgress()
            }

        },Response.ErrorListener { err->

        })
        queue.add(request)


    }

    private fun requestOtp(phone: String) {
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(p0: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(p0, p1)
                token = p0
                dismissProgress()
                Snackbar.make(loginRoot,"OTP has been sent to number",Snackbar.LENGTH_LONG).show()
                loginOtp.visibility=View.VISIBLE
                findViewById<EditText>(R.id.loginPhone).isEnabled=false
                loginButton.text = "Verify OTP"

            }


            override fun onVerificationCompleted(p0: PhoneAuthCredential?) {
                signInAfterGettingPhoneCredential(p0)
                dismissProgress()
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                Log.d("Error Login",p0.toString())
                Toast.makeText(this@Login,"We are sorry...Some error occurred from our servers",Toast.LENGTH_LONG).show()
                dismissProgress()
            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91"+phone,1,TimeUnit.MINUTES,this,callback)
    }


    fun verifyCode(code:String){
        signInAfterGettingPhoneCredential(PhoneAuthProvider.getCredential(token!!,code))
    }

    fun signInAfterGettingPhoneCredential(credential: PhoneAuthCredential?){
        showProgress("Almost there.... Getting credentials...")
        FirebaseAuth.getInstance().signInWithCredential(credential!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        dismissProgress()
                        startActivity(Intent(this,MainActivity::class.java))

                    } else {
                        dismissProgress()
                        Snackbar.make(loginRoot,"Some error occurred",Snackbar.LENGTH_LONG).setAction("TRY AGAIN", View.OnClickListener{ v->
                            showProgress("Verifying OTP")
                            verifyCode(loginOtp.text.toString())
                        }).show()
                    }
                }
        dismissProgress()
    }

    fun dismissProgress(){
        progress!!.dismiss()
    }

    companion object {
        fun isValidPhoneNo(phone: String):Boolean{

            if(phone.length == 10){
                return true
            }

            return false
        }

    }
}
