package com.example.netcharge.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

class NetCharge (
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val description: String = "",
    val typeCharger: Int = 0,
    val mainImage: String = "",
    val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
)