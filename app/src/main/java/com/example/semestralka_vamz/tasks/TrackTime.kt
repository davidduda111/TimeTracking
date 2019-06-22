package com.example.semestralka_vamz.tasks

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class TrackTime(activity: AppCompatActivity) {

    private val PREF_NAME = "data"
    private val KEY_START = "start"
    private val KEY_RUNNING = "running"

    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    init {
        sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }

    fun isTimeTrackingRunning(): Boolean {
        return sharedPreferences.getBoolean(KEY_RUNNING, false)
    }

    fun getCurrentTime(): String? {
        if(isTimeTrackingRunning()) {
            return hmsTimeFormatter(getTimeElapsed())
        }
        return null
    }

    fun getStartTime(): Long {
        return sharedPreferences.getLong(KEY_START, System.currentTimeMillis())
    }

    fun getTimeElapsed(): Long {
        return System.currentTimeMillis() - getStartTime()
    }

    fun stopTrackingTime() {
        editor.putBoolean(KEY_RUNNING, false)
        editor.apply()
    }

    fun startTrackingTime() {
        editor.putLong(KEY_START, System.currentTimeMillis())
        editor.putBoolean(KEY_RUNNING, true)
        editor.apply()
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
}