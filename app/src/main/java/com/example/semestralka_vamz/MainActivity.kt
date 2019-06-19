package com.example.semestralka_vamz

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.PopupMenu
import java.util.concurrent.TimeUnit
import android.support.v4.os.HandlerCompat.postDelayed




class MainActivity : AppCompatActivity() {

    //SharedPreferences
    private val PREF_NAME = "data"
    private val KEY_START = "start"
    private val KEY_RUNNING = "running"

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor


    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
        initTimer()
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        burger.setOnClickListener{
            //Creating the instance of PopupMenu
            val popup = PopupMenu(this@MainActivity, burger)
            //Inflating the Popup using xml file
            popup.menuInflater
                .inflate(R.menu.popup_menu, popup.menu)

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener { item ->
                //Logout
                if(item.itemId == R.id.logout_btn) {
                    mAuth?.signOut()
                    editor.putBoolean(KEY_RUNNING, false)
                    editor.putLong(KEY_START, 0)
                    editor.apply()
                    
                    startActivity(
                        Intent(this@MainActivity,
                            LoginActivity::class.java)
                    )
                }

                if(item.itemId == R.id.settings) {
                    startActivity(
                        Intent(this@MainActivity,
                            SettingsActivity::class.java)
                    )
                }

                if(item.itemId == R.id.statistics) {
                    startActivity(
                        Intent(this@MainActivity,
                            StatisticsActivity::class.java)
                    )
                }

                //...
                true
            }

            popup.show() //showing popup menu
        }

        start_btn.setOnClickListener{
            if(sharedPreferences.getBoolean(KEY_RUNNING, false)) {
                start_btn.setImageResource(R.drawable.ic_play_arrow_white_100dp)
                editor.putBoolean(KEY_RUNNING, false)
            } else {
                start_btn.setImageResource(R.drawable.ic_stop_white_100dp)
                startTimer()
                editor.putLong(KEY_START, System.currentTimeMillis())
                editor.putBoolean(KEY_RUNNING, true)

            }
            editor.apply()
        }
    }

    private fun initTimer() {
        if(sharedPreferences.getBoolean(KEY_RUNNING, false)) {
            start_btn.setImageResource(R.drawable.ic_stop_white_100dp)
            startTimer()

        }
    }


    private fun hmsTimeFormatter(milliSeconds: Long): String {

        return String.format(
            "%02d:%02d:%02d",
            TimeUnit.MILLISECONDS.toHours(milliSeconds),
            TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(
                    milliSeconds
                )
            ),
            TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    milliSeconds
                )
            )
        )
    }

    private fun startTimer() {
        val handler = Handler()
        val delay = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {

                updateTimer()

                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun updateTimer() {
        if (sharedPreferences.getBoolean(KEY_RUNNING, false)) {
            val timeElapsed = System.currentTimeMillis() - sharedPreferences.getLong(KEY_START, System.currentTimeMillis())
            val timeString = hmsTimeFormatter(timeElapsed)

            time.text = timeString
        }
    }
}
