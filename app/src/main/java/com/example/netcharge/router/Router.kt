package com.example.netcharge.router


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.netcharge.data.Resource
import com.example.netcharge.models.CustomUser
import com.example.netcharge.models.NetCharge
import com.example.netcharge.screens.IndexScreen
import com.example.netcharge.screens.LoginScreen
import com.example.netcharge.screens.NetChargeScreen
import com.example.netcharge.screens.RankingScreen
import com.example.netcharge.screens.RegisterScreen
import com.example.netcharge.screens.SettingScreen
import com.example.netcharge.screens.TableScreen
import com.example.netcharge.screens.UserProfileScreen
import com.example.netcharge.viewmodels.AuthViewModel
import com.example.netcharge.viewmodels.NetChargeViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(
    viewModel: AuthViewModel,
    netchargeViewModel: NetChargeViewModel
){
    val navController = rememberNavController();
    NavHost(navController = navController, startDestination = Routes.loginScreen) {
        composable(Routes.loginScreen){
           LoginScreen(
                viewModel=viewModel,
                navController=navController
           )
        }
        composable(Routes.indexScreen){
            val netchargeResource = netchargeViewModel.netcharges.collectAsState()
            val netchargeMarkers = remember {
                mutableListOf<NetCharge>()
            }
            netchargeResource.value.let {
                when(it){
                    is Resource.Success -> {
                        netchargeMarkers.clear()
                        netchargeMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                }
            }
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                netChargeMarkers = netchargeMarkers
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")

            val netchargeResource = netchargeViewModel.netcharges.collectAsState()
            val netChargeMarkers = remember {
                mutableListOf<NetCharge>()
            }
            netchargeResource.value.let {
                when(it){
                    is Resource.Success -> {
                        netChargeMarkers.clear()
                        netChargeMarkers.addAll(it.result)
                    }
                    is Resource.loading -> {

                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> {}
                }
            }

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                netChargeMarkers = netChargeMarkers
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{isCameraSet}/{latitude}/{longitude}/{netcharges}",
            arguments = listOf(
                navArgument("isCameraSet") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("netcharges") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val isCameraSet = backStackEntry.arguments?.getBoolean("isCameraSet")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")
            val netChargesJson = backStackEntry.arguments?.getString("netcharges")
            val netcharges = Gson().fromJson(netChargesJson, Array<NetCharge>::class.java).toList()

            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                isCameraSet = remember { mutableStateOf(isCameraSet!!) },
                cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                },
                netChargeMarkers = netcharges.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(
            route = Routes.indexScreenWithParams + "/{netcharges}",
            arguments = listOf(
                navArgument("netcharges") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val netChargesJson = backStackEntry.arguments?.getString("netcharges")
            val netcharges = Gson().fromJson(netChargesJson, Array<NetCharge>::class.java).toList()
            IndexScreen(
                viewModel = viewModel,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                netChargeMarkers = netcharges.toMutableList(),
                isFilteredParam = true
            )
        }
        composable(Routes.registerScreen){
            RegisterScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
        composable(
            route = Routes.netchargeScreen + "/{netcharge}",
            arguments = listOf(
                navArgument("netcharge"){ type = NavType.StringType }
            )
        ){backStackEntry ->
            val neteChargeJson = backStackEntry.arguments?.getString("netcharge")
            val netcharge = Gson().fromJson(neteChargeJson, NetCharge::class.java)
            netchargeViewModel.getNetChargeAllRates(netcharge.id)
            NetChargeScreen(
                netCharge = netcharge,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                viewModel = viewModel,
                netcharges = null
            )
        }
        composable(
            route = Routes.netchargeScreen + "/{netcharge}/{netcharges}",
            arguments = listOf(
                navArgument("netcharge"){ type = NavType.StringType },
                navArgument("netcharges"){ type = NavType.StringType },
            )
        ){backStackEntry ->
            val netChargesJson = backStackEntry.arguments?.getString("netcharges")
            val netchages = Gson().fromJson(netChargesJson, Array<NetCharge>::class.java).toList()
            val netChargeJson = backStackEntry.arguments?.getString("netcharge")
            val netcharge = Gson().fromJson(netChargeJson, NetCharge::class.java)
            netchargeViewModel.getNetChargeAllRates(netcharge.id)

            NetChargeScreen(
                netCharge = netcharge,
                navController = navController,
                netChargeViewModel = netchargeViewModel,
                viewModel = viewModel,
                netcharges = netchages.toMutableList()
            )
        }
        composable(
            route = Routes.userProfileScreen + "/{userData}",
            arguments = listOf(navArgument("userData"){
                type = NavType.StringType
            })
        ){backStackEntry ->
            val userDataJson = backStackEntry.arguments?.getString("userData")
            val userData = Gson().fromJson(userDataJson, CustomUser::class.java)
            val isMy = FirebaseAuth.getInstance().currentUser?.uid == userData.id
            UserProfileScreen(
                navController = navController,
                viewModel = viewModel,
                netChargeViewModel = netchargeViewModel,
                userData = userData,
                isMy = isMy
            )
        }
        composable(
            route = Routes.tableScreen + "/{netcharges}",
            arguments = listOf(navArgument("netcharges") { type = NavType.StringType })
        ){ backStackEntry ->
            val netchargesJson = backStackEntry.arguments?.getString("netcharges")
            val netcharges = Gson().fromJson(netchargesJson, Array<NetCharge>::class.java).toList()
            TableScreen(netcharges = netcharges, navController = navController, netChargeViewModel = netchargeViewModel)
        }

        composable(Routes.settingsScreen){
            SettingScreen(navController = navController)
        }
        composable(Routes.rankingScreen){
            RankingScreen(
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}