package com.example.semestralka_vamz.tasks

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import com.example.semestralka_vamz.models.TimeData
import java.util.concurrent.TimeUnit

class TimeTrackingService(activity: AppCompatActivity) {

    private val PREF_NAME = "data"
    private val KEY_START = "start"
    private val KEY_STOP = "stop"
    private val KEY_PAUSE_TIME = "pause_time"
    private val KEY_RUNNING = "running"
    private val KEY_PAUSE = "pause"

    private var sharedPreferences: SharedPreferences
    private var editor: SharedPreferences.Editor

    private var pausedTime: Long

    init {
        sharedPreferences = activity.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
        pausedTime = 0
    }

    fun isTimeTrackingRunning(): Boolean {
        return sharedPreferences.getBoolean(KEY_RUNNING, false)
    }

    fun isTimeTrackingPaused(): Boolean {
        return sharedPreferences.getBoolean(KEY_PAUSE, false)
    }

    fun getCurrentTime(): String? {
        if(isTimeTrackingRunning() || isTimeTrackingPaused()) {
            return hmsTimeFormatter(getTimeElapsed())
        }
        return null
    }

    fun getStartTime(): Long {
        return sharedPreferences.getLong(KEY_START, System.currentTimeMillis())
    }

    fun getTimeElapsed(): Long {
        return (System.currentTimeMillis() - getStartTime()) - getTimePaused()
    }

    fun getTimePaused (): Long {
        if(sharedPreferences.getBoolean(KEY_PAUSE, false)) {
            return System.currentTimeMillis() - sharedPreferences.getLong(KEY_PAUSE_TIME, System.currentTimeMillis())
        }
        return pausedTime
    }

    fun getStopTime(): Long {
        return sharedPreferences.getLong(KEY_STOP, System.currentTimeMillis())
    }

    fun stopTrackingTime(result:Boolean = false): TimeData? {
        if(sharedPreferences.getBoolean(KEY_PAUSE, false)) {
            pausedTime += System.currentTimeMillis() - sharedPreferences.getLong(KEY_PAUSE_TIME, System.currentTimeMillis())
        }

        val elapsed: Long = getTimeElapsed()
        val start: Long = getStartTime() - 40000
        val paused: Long = pausedTime

        editor.putBoolean(KEY_RUNNING, false)
        editor.putBoolean(KEY_PAUSE, false)
        editor.putLong(KEY_START, 0)
        editor.putLong(KEY_PAUSE_TIME, 0)
        editor.putLong(KEY_STOP, System.currentTimeMillis())
        editor.apply()

        pausedTime = 0

        if(result) {
            return TimeData(elapsed, start, paused)
        }
        return null
    }

    fun startTrackingTime() {
        if(!sharedPreferences.getBoolean(KEY_PAUSE, false)) {
            editor.putLong(KEY_START, System.currentTimeMillis())
        } else {
            pausedTime += System.currentTimeMillis() - sharedPreferences.getLong(KEY_PAUSE_TIME, System.currentTimeMillis())
        }
        editor.putBoolean(KEY_RUNNING, true)
        editor.putBoolean(KEY_PAUSE, false)
        editor.putLong(KEY_STOP, 0)
        editor.apply()
    }

    fun pauseTrackingTime() {
        editor.putLong(KEY_PAUSE_TIME, System.currentTimeMillis())
        editor.putBoolean(KEY_PAUSE, true)
        editor.putBoolean(KEY_RUNNING, false)
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