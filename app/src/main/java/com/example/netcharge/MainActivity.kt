package com.example.netcharge

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.netcharge.models.NetCharge
import com.example.netcharge.ui.theme.NetChargeTheme
import com.example.netcharge.viewmodels.AuthViewModel
import com.example.netcharge.viewmodels.AuthViewModelFactory
import com.example.netcharge.viewmodels.NetChargeViewModel
import com.example.netcharge.viewmodels.NetChargeViewModelFactory

class MainActivity : ComponentActivity() {
    private val userViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory()
    }
    private val netChargeViewModel: NetChargeViewModel by viewModels{
        NetChargeViewModelFactory()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                NetCharge(userViewModel,netChargeViewModel)
//            AquaSpot(viewModel = userViewModel)
        }
    }
}