package com.example.netcharge

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class NetChargeApp:Application(){
    val db by lazy { Firebase.firestore }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

    }
}