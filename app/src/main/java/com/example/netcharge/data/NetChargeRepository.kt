package com.example.netcharge.data

import android.net.Uri
import com.example.netcharge.models.NetCharge
//import com.google.type.LatLng
import com.google.android.gms.maps.model.LatLng

interface NetChargeRepository {

    suspend fun getAllNetCharges(): Resource<List<NetCharge>>
    suspend fun saveNetChargeData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String>

    suspend fun getUserNetCharges(
        uid: String
    ): Resource<List<NetCharge>>
}