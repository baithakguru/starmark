package guru.baithak.starmark.ui.register

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import guru.baithak.starmark.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        loginPage.setOnClickListener{v->
            startActivity(Intent(this, Login::class.java))
        }
    }
}
