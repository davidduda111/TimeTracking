package com.example.semestralka_vamz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.PopupMenu
import com.example.semestralka_vamz.models.User
import com.example.semestralka_vamz.tasks.TrackTime


class MainActivity : AppCompatActivity() {
    //Time tracking
    private lateinit var timeTracking: TrackTime

    //User
    private lateinit var user: User

    //Firebase references
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()

    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        timeTracking = TrackTime(this)

        user = User.create()

        if(timeTracking.isTimeTrackingRunning()) {
            start_btn.setImageResource(R.drawable.ic_stop_white_100dp)
            startTimer()
        }

        assignListeners()

        loadUserData()
    }

    private fun assignListeners() {
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
                    timeTracking.stopTrackingTime()

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
            if(timeTracking.isTimeTrackingRunning()) {
                start_btn.setImageResource(R.drawable.ic_play_arrow_white_100dp)
                timeTracking.stopTrackingTime()
            } else {
                start_btn.setImageResource(R.drawable.ic_stop_white_100dp)
                startTimer()
                timeTracking.startTrackingTime()

            }
        }
    }

    private fun loadUserData() {
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                user.salary = snapshot.child("salary").value as String
                user.currency = snapshot.child("currency").value as String
                user.firstName = snapshot.child("firstName").value as String
                user.lastName = snapshot.child("lastName").value as String
                //user.time = ...
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun startTimer() {
        val handler = Handler()
        val delay = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {

                //Update timer
                if (timeTracking.isTimeTrackingRunning()) {
                    time.text = timeTracking.getCurrentTime()
                }

                //Update salary
                updateSalary()

                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun updateSalary() {
        if (timeTracking.isTimeTrackingRunning()) {
            val mUser = mAuth!!.currentUser
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

            mUserReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val timeElapsed = System.currentTimeMillis() - timeTracking.getStartTime()
                    var salary = snapshot.child("salary").value as String
                    val salaryPerMillisecond = (salary.replace(",", ".").toDouble() / (60*60*1000))
                    val currency = snapshot.child("currency").value as String

                    val finalString = (Math.round((salaryPerMillisecond * timeElapsed) * 100.0) / 100.0).toString()+currency

                    salaryText.text = finalString
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}
