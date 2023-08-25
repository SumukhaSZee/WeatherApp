package com.example.openmapweatherapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Date {
    fun dateFormatConverter(date: Long): String {

        return SimpleDateFormat(
            "hh:mm a",
            Locale.ENGLISH
        ).format(Date(date * 1000))
    }
}