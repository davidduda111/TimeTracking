package com.example.semestralka_vamz.models

data class TimeTracked(
    val duration    : Long = 0,
    val salary      : String = "",
    val start       : Long = 0,
    val pause       : Long = 0,
    val stop        : Long = 0
)

data class TimeData(val elapsed: Long, val start: Long, val paused: Long)

class Times (var list : MutableList<TimeTracked>)