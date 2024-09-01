package com.example.netcharge.data

import android.net.Uri
import com.example.netcharge.models.NetCharge
import com.example.netcharge.models.service.DataBaseService
import com.example.netcharge.models.service.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
//import com.google.type.LatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

class NetChargeImpl: NetChargeRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    private val databaseService = DataBaseService(firestoreInstance)
    private val storageService = StorageService(storageInstance)


    override suspend fun getAllNetCharges(): Resource<List<NetCharge>> {
        return try{
            val snapshot = firestoreInstance.collection("netcharges").get().await()
            val netchages = snapshot.toObjects(NetCharge::class.java)
            Resource.Success(netchages)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun saveNetChargeData(
        description: String,
        crowd: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
                val mainImageUrl = storageService.uploadNetChargeMainImage(mainImage)
                val galleryImagesUrls = storageService.uploadNetChargeGalleryImages(galleryImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                val netcharge = NetCharge(
                    userId = currentUser.uid,
                    description = description,
                    crowd = crowd,
                    mainImage = mainImageUrl,
                    galleryImages = galleryImagesUrls,
                    location = geoLocation
                )
                databaseService.saveNetChargeData(netcharge)
                databaseService.addPoints(currentUser.uid, 5)
            }
            Resource.Success("Uspesno sačuvani svi podaci o mestu")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserNetCharges(uid: String): Resource<List<NetCharge>> {
        return try {
            val snapshot = firestoreInstance.collection("netcharges")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val netcharges = snapshot.toObjects(NetCharge::class.java)
            Resource.Success(netcharges)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}