package com.example.semestralka_vamz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.semestralka_vamz.adapters.TimeAdapter
import com.example.semestralka_vamz.models.TimeTracked
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_statistics.*
import kotlin.NullPointerException as NullPointerException1

class StatisticsActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private var timeList: MutableList<TimeTracked> = mutableListOf()
    private lateinit var adapter : TimeAdapter

    private lateinit var linearLayoutManager: LinearLayoutManager

    private lateinit var gridLayoutManager: GridLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        initialise()
    }

    override fun onStart() {
        super.onStart()

        //check user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(
                Intent(this@StatisticsActivity,
                    LoginActivity::class.java)
            )
            finish()
        }

        //load data
        val mUser = mAuth!!.currentUser
        val userReference = mDatabaseReference!!.child(mUser!!.uid).child("time")

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {
                    snapshot.children.forEach{ child ->
                        val time = child.getValue(TimeTracked::class.java)
                        timeList.add(time!!)

                        adapter.times.list.add(time!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        adapter = TimeAdapter(this) {
            //
        }
        statisticsRecycler.adapter = adapter

        linearLayoutManager = LinearLayoutManager(this)
        statisticsRecycler.layoutManager = linearLayoutManager

        gridLayoutManager = GridLayoutManager(this, 2)
    }
}