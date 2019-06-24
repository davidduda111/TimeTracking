package com.example.semestralka_vamz.models

data class TimeTracked(
    val duration    : Long,
    val sallary     : Double,
    val start       : Long,
    val Stop        : Long
)

data class TimeData(val elapsed: Long, val start: Long)