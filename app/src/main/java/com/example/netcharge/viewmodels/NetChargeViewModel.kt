package com.example.netcharge.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.netcharge.data.NetChargeImpl
import com.example.netcharge.data.RateRepositoryImpl
import com.example.netcharge.data.Resource
import com.example.netcharge.models.NetCharge
import com.example.netcharge.models.Rate
//import com.google.type.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.android.gms.maps.model.LatLng

class NetChargeViewModel: ViewModel() {
    val repository = NetChargeImpl()
    val rateRepository = RateRepositoryImpl()

    private val _netchargeFlow = MutableStateFlow<Resource<String>?>(null)
    val netchargeFlow: StateFlow<Resource<String>?> = _netchargeFlow

    private val _newRate = MutableStateFlow<Resource<String>?>(null)
    val newRate: StateFlow<Resource<String>?> = _newRate

    private val _netcharges = MutableStateFlow<Resource<List<NetCharge>>>(Resource.Success(emptyList()))
    val netcharges: StateFlow<Resource<List<NetCharge>>> get() = _netcharges

    private val _rates = MutableStateFlow<Resource<List<Rate>>>(Resource.Success(emptyList()))
    val rates: StateFlow<Resource<List<Rate>>> get() = _rates


    private val _userNetCharges = MutableStateFlow<Resource<List<NetCharge>>>(Resource.Success(emptyList()))
    val userNetCharges: StateFlow<Resource<List<NetCharge>>> get() = _userNetCharges

    init {
        getAllNetCharges()
    }

    fun getAllNetCharges() = viewModelScope.launch {
        _netcharges.value = repository.getAllNetCharges()
    }

    fun saveNetChargeData(
        description: String,
        typeCharger: Int,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: MutableState<LatLng?>
    ) = viewModelScope.launch{
        _netchargeFlow.value = Resource.loading
        repository.saveNetChargeData(
            description = description,
            typeCharger = typeCharger,
            mainImage = mainImage,
            galleryImages = galleryImages,
            location = location.value!!
        )
        _netchargeFlow.value = Resource.Success("Uspešno dodato mesto")
    }


    fun getNetChargeAllRates(
        ncid: String
    ) = viewModelScope.launch {
        _rates.value = Resource.loading
        val result = rateRepository.getNetChargeRates(ncid)
        _rates.value = result
    }

    fun addRate(
        ncid: String,
        rate: Int,
        netcharge: NetCharge
    ) = viewModelScope.launch {
        _newRate.value = rateRepository.addRate(ncid, rate, netcharge)
    }

    fun updateRate(
        rid: String,
        rate: Int
    ) = viewModelScope.launch{
        _newRate.value = rateRepository.updateRate(rid, rate)
    }

    fun getUserNetCharges(
        uid: String
    ) = viewModelScope.launch {
        _userNetCharges.value = repository.getUserNetCharges(uid)
    }
}

class NetChargeViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NetChargeViewModel::class.java)){
            return NetChargeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}