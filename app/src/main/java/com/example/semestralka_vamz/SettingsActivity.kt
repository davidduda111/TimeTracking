package com.example.semestralka_vamz

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.NullPointerException as NullPointerException1

class SettingsActivity : AppCompatActivity() {

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initialise()


    }

    override fun onStart() {
        super.onStart()

        //check user
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(
                Intent(this@SettingsActivity,
                    LoginActivity::class.java)
            )
            finish()
        }

        //load data
        val mUser = mAuth!!.currentUser
        val mUserReference = mDatabaseReference!!.child(mUser!!.uid)
        email.text = SpannableStringBuilder(mUser.email)

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firstName.text = SpannableStringBuilder(snapshot.child("firstName").value as String)
                lastName.text = SpannableStringBuilder(snapshot.child("lastName").value as String)
                currency.text = SpannableStringBuilder(snapshot.child("currency").value as String)
                salary.text = SpannableStringBuilder(snapshot.child("salary").value as String)
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun initialise() {
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

    }
}