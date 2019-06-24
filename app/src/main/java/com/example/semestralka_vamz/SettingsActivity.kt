package com.example.semestralka_vamz

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.SpannableStringBuilder
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
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

        setClickListeners()


    }

    private fun setClickListeners() {
        btn_save.setOnClickListener {
            var count = 0
            val mDatabase = FirebaseDatabase.getInstance()
            val mUser = mAuth!!.currentUser
            val database = mDatabase.reference.child("Users")
                .child(mUser!!.uid)

            database.child("currency").setValue(currency.text.toString())
                .addOnFailureListener {
                    count++
                }

            database.child("salary").setValue(salary.text.toString())
                .addOnFailureListener {
                    count++
                }

            database.child("firstName").setValue(firstName.text.toString())
                .addOnFailureListener {
                    count++
                }

            database.child("lastName").setValue(lastName.text.toString())
                .addOnFailureListener {
                    count++
                }

            if(count == 4) {
                Snackbar.make(settingsRoot, "Data saving failed.", Snackbar.LENGTH_SHORT).show()
            } else {
                Snackbar.make(settingsRoot, "Data saved successfully.", Snackbar.LENGTH_SHORT).show()
            }
        }
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