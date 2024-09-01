package com.example.netcharge.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.netcharge.data.Resource
import com.example.netcharge.models.NetCharge
import com.example.netcharge.models.Rate
import com.example.netcharge.router.Routes
import com.example.netcharge.screens.components.CustomBackButton
import com.example.netcharge.screens.components.CustomCrowdIndicator
import com.example.netcharge.screens.components.CustomNetChargeGallery
import com.example.netcharge.screens.components.CustomNetChargeLocation
import com.example.netcharge.screens.components.CustomNetChargeRate
import com.example.netcharge.screens.components.CustomRateButton
import com.example.netcharge.screens.components.NetChargeMainImage
import com.example.netcharge.screens.components.greyTextBigger
import com.example.netcharge.screens.components.headingText
import com.example.netcharge.screens.dialogs.RateNetChargeDialog
import com.example.netcharge.viewmodels.AuthViewModel
import com.example.netcharge.viewmodels.NetChargeViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.math.RoundingMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun NetChargeScreen(
    navController: NavController,
    netChargeViewModel: NetChargeViewModel,
    netCharge: NetCharge,
    viewModel: AuthViewModel,
    netcharges: MutableList<NetCharge>?
){
    val ratesResources = netChargeViewModel.rates.collectAsState()
    val newRateResource = netChargeViewModel.newRate.collectAsState()

    val rates = remember {
        mutableListOf<Rate>()
    }
    val averageRate = remember {
        mutableStateOf(0.0)
    }
    val showRateDialog = remember {
        mutableStateOf(false)
    }

    val isLoading = remember {
        mutableStateOf(false)
    }

    val myPrice = remember {
        mutableStateOf(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NetChargeMainImage(netCharge.mainImage)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            item{ CustomBackButton {
                if(netcharges == null) {
                    navController.popBackStack()
                }else{
                    val isCameraSet = true
                    val latitude = netCharge.location.latitude
                    val longitude = netCharge.location.longitude

                    val netChargesJson = Gson().toJson(netcharges)
                    val encodedNetChargeJson = URLEncoder.encode(netChargesJson, StandardCharsets.UTF_8.toString())
                    navController.navigate(Routes.indexScreenWithParams + "/$isCameraSet/$latitude/$longitude/$encodedNetChargeJson")
                }
            }
            }
            item{ Spacer(modifier = Modifier.height(220.dp)) }
            item{ CustomCrowdIndicator(crowd = netCharge.crowd) }
            item{ Spacer(modifier = Modifier.height(20.dp)) }
            item{ headingText(textValue = "NetCharge mesto u blizini") }
            item{ Spacer(modifier = Modifier.height(10.dp)) }
            item{ CustomNetChargeLocation(location = LatLng(netCharge.location.latitude, netCharge.location.longitude)) }
            item{ Spacer(modifier = Modifier.height(10.dp)) }
            item{ CustomNetChargeRate(average = averageRate.value) }
            item{ Spacer(modifier = Modifier.height(10.dp)) }
            item{ greyTextBigger(textValue = netCharge.description.replace('+', ' ')) }
            item{ Spacer(modifier = Modifier.height(20.dp)) }
            item{ Text(text = "Galerija NetCharge mesta", style= TextStyle(fontSize = 20.sp)) };
//            item{ CustomCrowdIndicator(crowd = 1)}
            item{ Spacer(modifier = Modifier.height(10.dp)) }
            item { CustomNetChargeGallery(images = netCharge.galleryImages) }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 15.dp, vertical = 20.dp)
        ) {
            CustomRateButton(
                enabled = if(netCharge.userId == viewModel.currentUser?.uid) false else true,
                onClick = {
                    val rateExist = rates.firstOrNull{
                        it.netchargeId == netCharge.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null)
                        myPrice.value = rateExist.rate
                    showRateDialog.value = true
                })
        }


        if(showRateDialog.value){
            RateNetChargeDialog(
                showRateDialog = showRateDialog,
                rate = myPrice,
                rateNetCharge = {

                    val rateExist = rates.firstOrNull{
                        it.netchargeId == netCharge.id && it.userId == viewModel.currentUser!!.uid
                    }
                    if(rateExist != null){
                        isLoading.value = true
                        netChargeViewModel.updateRate(
                            rid = rateExist.id,
                            rate = myPrice.value
                        )
                    }else {
                        isLoading.value = true
                        netChargeViewModel.addRate(
                            ncid = netCharge.id,
                            rate = myPrice.value,
                            netcharge = netCharge
                        )
                    }
                },
                isLoading = isLoading
            )
        }
    }

    ratesResources.value.let {
        when(it){
            is Resource.Success -> {
                rates.addAll(it.result)
                var sum = 0.0
                for (rate in it.result){
                    sum += rate.rate.toDouble()
                }
                if(sum != 0.0) {
                    val rawPositive = sum / it.result.count()
                    val rounded = rawPositive.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                    averageRate.value = rounded
                }  else {}
            }
            is Resource.loading -> {

            }
            is Resource.Failure -> {
                Log.e("Podaci", it.toString())
            }
        }
    }
    newRateResource.value.let {
        when(it){
            is Resource.Success -> {
                isLoading.value = false

                val rateExist = rates.firstOrNull{rate ->
                    rate.id == it.result
                }
                if(rateExist != null){
                    rateExist.rate = myPrice.value
                }
            }
            is Resource.loading -> {
//                isLoading.value = false
            }
            is Resource.Failure -> {
                val context = LocalContext.current
                Toast.makeText(context, "Došlo je do greške prilikom ocenjivanja", Toast.LENGTH_LONG).show()
                isLoading.value = false
            }
            null -> {
                isLoading.value = false
            }
        }
    }
}