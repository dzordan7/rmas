package com.example.netcharge.screens.netcharges

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.netcharge.R
import com.example.netcharge.data.Resource
import com.example.netcharge.screens.components.CustomCrowd
import com.example.netcharge.screens.components.CustomGalleryForAddNewNetCharge
import com.example.netcharge.screens.components.CustomImageForNewNetCharge
import com.example.netcharge.screens.components.customRichTextInput
import com.example.netcharge.screens.components.headingText
import com.example.netcharge.screens.components.inputTextIndicator
import com.example.netcharge.screens.components.loginRegisterCustomButton
import com.example.netcharge.viewmodels.NetChargeViewModel
import com.google.android.gms.maps.model.LatLng
//import com.google.type.LatLng

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddNewNetChargeBottomSheet(
    netChargeViewModel: NetChargeViewModel?,
    location: MutableState<LatLng?>,
    sheetState: ModalBottomSheetState
) {
    val netChargeFlow = netChargeViewModel?.netchargeFlow?.collectAsState()
    val inputDescription = remember {
        mutableStateOf("")
    }
    val isDescriptionError = remember {
        mutableStateOf(false)
    }
    val descriptionError = remember {
        mutableStateOf("Ovo polje je obavezno")
    }
    val selectedOption = remember {
        mutableStateOf(0)
    }
    val buttonIsEnabled = remember {
        mutableStateOf(true)
    }
    val buttonIsLoading = remember {
        mutableStateOf(false)
    }

    val selectedImage = remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }
    val selectedGallery = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    val showedAlert = remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 20.dp)
    ) {
        item{ headingText(textValue = stringResource(id = R.string.add_new_netcharge_heading)) }
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{ CustomImageForNewNetCharge(selectedImageUri = selectedImage) }
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{ inputTextIndicator(textValue = "Opis") }
        item{ Spacer(modifier = Modifier.height(5.dp)) }
        item{ customRichTextInput(inputValue = inputDescription, inputText = "Unesite opis", isError = isDescriptionError, errorText = descriptionError) }
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{inputTextIndicator(textValue = "Gužva")}
        item{ Spacer(modifier = Modifier.height(5.dp)) }
        item{ CustomCrowd(selectedOption) }
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{inputTextIndicator(textValue = "Galerija")}
        item{ Spacer(modifier = Modifier.height(5.dp)) }
        item{ CustomGalleryForAddNewNetCharge(selectedImages = selectedGallery) }
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{
            loginRegisterCustomButton(buttonText = "Dodaj NetCharge mesto", isEnabled = buttonIsEnabled, isLoading = buttonIsLoading) {
            showedAlert.value = false;
            buttonIsLoading.value = true
            netChargeViewModel?.saveNetChargeData(
                description = inputDescription.value,
                crowd = selectedOption.value,
                mainImage = selectedImage.value!!,
                galleryImages = selectedGallery.value,
                location = location
            )
        }
        }
        item{ Spacer(modifier = Modifier.height(5.dp)) }
//        Spacer(modifier = Modifier.height(100.dp))
    }
    netChargeFlow?.value.let {
        when (it){
            is Resource.Failure -> {
                Log.d("Stanje flowa", it.toString());
                buttonIsLoading.value = false
                val context = LocalContext.current
                if(!showedAlert.value) {
//                    Toast.makeText(context, it.exception.message, Toast.LENGTH_LONG).show()
                    showedAlert.value = true
                    netChargeViewModel?.getAllNetCharges()
                }else{}
            }
            is Resource.loading -> {

            }
            is Resource.Success -> {
                Log.d("Stanje flowa", it.toString());
                buttonIsLoading.value = false
                val context = LocalContext.current
                if(!showedAlert.value) {
//                    Toast.makeText(context, "Uspesno dodato", Toast.LENGTH_LONG).show()
                    showedAlert.value = true
                    netChargeViewModel?.getAllNetCharges()
                }else{}
            }
            null -> {}
        }
    }
}
