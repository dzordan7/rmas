package com.example.netcharge.models

import com.google.firebase.firestore.DocumentId

class Rate (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val netchargeId: String = "",
    var rate: Int = 0
)