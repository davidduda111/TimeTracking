package com.example.semestralka_vamz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.NullPointerException as NullPointerException1

class WelcomeActivity : AppCompatActivity() {

    private var r = Random()
    private val random_time = r.nextInt(1500 - 1000) + 1000
    private val SPLASH_TIME_OUT = random_time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)

        Handler().postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                startActivity(
                    Intent(this@WelcomeActivity,
                        MainActivity::class.java))
            } else {
                startActivity(
                    Intent(this@WelcomeActivity,
                        LoginActivity::class.java))
            }
            finish()
        }, SPLASH_TIME_OUT.toLong())
    }
}