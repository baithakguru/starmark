package guru.baithak.starmark.ui.register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import guru.baithak.starmark.ui.mainScreen.MainActivity
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        signUp.setOnClickListener { view ->
            startActivity(Intent(this, SignUp::class.java))
        }
        loginButton.setOnClickListener(View.OnClickListener { v: View? ->
            startActivity(Intent(this, MainActivity::class.java))

        })

    }
}
