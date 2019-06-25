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
import com.example.semestralka_vamz.models.TimeData
import com.example.semestralka_vamz.models.TimeTracked
import com.example.semestralka_vamz.tasks.TimeTrackingService


class MainActivity : AppCompatActivity() {
    //Time tracking
    private lateinit var timeTrackingService: TimeTrackingService

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

        timeTrackingService = TimeTrackingService(this)

        if(timeTrackingService.isTimeTrackingRunning()) {
            start_btn.setImageResource(R.drawable.ic_pause_white_100dp)
            startTimer()

            updateSalary()
        } else if(timeTrackingService.isTimeTrackingPaused()) {
            start_btn.setImageResource(R.drawable.ic_play_arrow_white_100dp)
            startTimer()

            updateSalary()
        }

        assignListeners()
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
                    stopTimer()

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
            if(timeTrackingService.isTimeTrackingRunning()) {
                start_btn.setImageResource(R.drawable.ic_play_arrow_white_100dp)
                timeTrackingService.pauseTrackingTime()
            } else {
                start_btn.setImageResource(R.drawable.ic_pause_white_100dp)
                startTimer()
                timeTrackingService.startTrackingTime()

            }
        }

        stop_btn.setOnClickListener {
            stopTimer()
        }
    }

    private fun stopTimer() {
        start_btn.setImageResource(R.drawable.ic_play_arrow_white_100dp)
        time.text = "00:00:00"
        val defaultSalary = "0.0" + salaryText.text.toString().takeLast(1)
        val salaryEarned = salaryText.text.toString()

        val stopData: TimeData? = timeTrackingService.stopTrackingTime(true)

        val mDatabase = FirebaseDatabase.getInstance()
        val mUser = mAuth!!.currentUser
        mDatabase.reference.child("Users").child(mUser!!.uid).child("time")
            .child(System.currentTimeMillis().toString())
            .setValue(TimeTracked(stopData!!.elapsed, salaryEarned, stopData.start, stopData.paused, timeTrackingService.getStopTime() - 40000))

        salaryText.text = defaultSalary
    }

    private fun startTimer() {
        val handler = Handler()
        val delay = 1000 //milliseconds

        handler.postDelayed(object : Runnable {
            override fun run() {

                //Update timer
                if (timeTrackingService.isTimeTrackingRunning()) {
                    time.text = timeTrackingService.getCurrentTime()

                    updateSalary()

                    handler.postDelayed(this, delay.toLong())
                } else if (timeTrackingService.isTimeTrackingPaused()) {
                    time.text = timeTrackingService.getCurrentTime()

                    updateSalary()

                    handler.postDelayed(this, delay.toLong())
                }


            }
        }, delay.toLong())
    }

    private fun updateSalary() {
        if (timeTrackingService.isTimeTrackingRunning() || timeTrackingService.isTimeTrackingPaused()) {
            val mUser = mAuth!!.currentUser
            val mUserReference = mDatabaseReference!!.child(mUser!!.uid)

            mUserReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    val timeElapsed = timeTrackingService.getTimeElapsed()
                    var salary = snapshot.child("salary").value as String
                    val salaryPerMillisecond = (salary.replace(",", ".").toDouble() / (60*60*1000))
                    val currency = snapshot.child("currency").value as String

                    val finalString = (Math.round((salaryPerMillisecond * timeElapsed) * 100.0) / 100.0).toString()+currency

                    if(timeTrackingService.isTimeTrackingRunning() || timeTrackingService.isTimeTrackingPaused()) {
                        salaryText.text = finalString

                        Log.e("TEST", timeElapsed.toString())
                        Log.e("TEST", finalString)
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }
    }
}

