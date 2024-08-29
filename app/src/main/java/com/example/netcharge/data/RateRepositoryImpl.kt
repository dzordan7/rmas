package com.example.netcharge.data

import com.example.netcharge.models.NetCharge
import com.example.netcharge.models.Rate
import com.example.netcharge.models.service.DataBaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RateRepositoryImpl : RateRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val databaseService = DataBaseService(firestoreInstance)
    override suspend fun getNetChargeRates(
        ncid: String
    ): Resource<List<Rate>> {
        return try {
            val rateDocRef = firestoreInstance.collection("rates")
            val querySnapshot = rateDocRef.get().await()
            val ratesList = mutableListOf<Rate>()
            for (document in querySnapshot.documents) {
                val netchargeId = document.getString("netchargeId") ?: ""
                if (netchargeId == ncid) {
                    ratesList.add(
                        Rate(
                            id = document.id,
                            userId = document.getString("userId") ?: "",
                            netchargeId = ncid,
                            rate = document.getLong("rate")?.toInt() ?: 0
                        )
                    )
                }
            }
            Resource.Success(ratesList)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserAdForNetCharge(): Resource<List<Rate>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserRates(): Resource<List<Rate>> {
        return try{
            val rateDocRef = firestoreInstance.collection("rates")
            val querySnapshot = rateDocRef.get().await()
            val ratesList = mutableListOf<Rate>()
            for(document in querySnapshot.documents){
                val userId = document.getString("userId") ?: ""
                if(userId == firebaseAuth.currentUser?.uid){
                    ratesList.add(Rate(
                        id = document.id,
                        netchargeId = document.getString("netchargeId") ?: "",
                        userId = userId,
                        rate = document.getLong("rate")?.toInt() ?: 0
                    ))
                }
            }
            Resource.Success(ratesList)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }


    override suspend fun addRate(
        ncid: String,
        rate: Int,
        netchage: NetCharge
    ): Resource<String> {
        return try{
            val myRate = Rate(
                userId = firebaseAuth.currentUser!!.uid,
                netchargeId = ncid,
                rate = rate
            )
            databaseService.addPoints(netchage.userId, rate * 3)
            val result = databaseService.saveRateData(myRate)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun updateRate(rid: String, rate: Int): Resource<String> {
        return try{
            val result = databaseService.updateRate(rid, rate)
            result
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}