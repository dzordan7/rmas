package com.example.netcharge.data

import com.example.netcharge.models.NetCharge
import com.example.netcharge.models.Rate

interface RateRepository {
    suspend fun getNetChargeRates(
        ncid: String
    ): Resource<List<Rate>>
    suspend fun getUserRates(): Resource<List<Rate>>
    suspend fun getUserAdForNetCharge(): Resource<List<Rate>>
    suspend fun addRate(
        ncid: String,
        rate: Int,
        netcharge: NetCharge
    ): Resource<String>

    suspend fun updateRate(
        rid: String,
        rate: Int,
    ): Resource<String>
}