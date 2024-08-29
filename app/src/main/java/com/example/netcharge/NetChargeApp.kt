package com.example.netcharge

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class NetChargeApp:Application(){
    val db by lazy { Firebase.firestore }
}