package com.example.semestralka_vamz.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.semestralka_vamz.R
import com.example.semestralka_vamz.data.TimeTracked
import com.example.semestralka_vamz.data.Times
import kotlinx.android.synthetic.main.time_layout.view.*
import java.util.*
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat


@SuppressLint("SetTextI18n")

class TimeAdapter(val context: Context, val times: Times = Times(mutableListOf()),
                  val onItemClick: (TimeTracked) -> Unit) : RecyclerView.Adapter<TimeAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parrent: ViewGroup, p1: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.time_layout, parrent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return times.list.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, possition: Int) {
        val user = times.list[possition]
        holder.setData(user)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SimpleDateFormat")
        fun setData(time: TimeTracked) {
            val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
            val timeFormatter = SimpleDateFormat("hh:mm:ss")

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = time.start

            val calendarStop = Calendar.getInstance()
            calendarStop.timeInMillis = time.stop

            itemView.date.text = dateFormatter.format(calendar.time)
            itemView.startTime.text = timeFormatter.format(calendar.time)
            itemView.stopTime.text = timeFormatter.format(calendarStop.time)
            itemView.pauseTime.text = hmsTimeFormatter(time.pause)
            itemView.durationTime.text = hmsTimeFormatter(time.duration)
            itemView.salary.text = time.salary

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
}