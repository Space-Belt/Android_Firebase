package com.example.androidfirebase

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidfirebase.ui.theme.ChatRoomListScreen
import com.example.androidfirebase.ui.theme.ChatScreen
import com.example.androidfirebase.ui.theme.LoginScreen
import com.example.androidfirebase.ui.theme.SignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    roomViewModel: RoomViewModel,
) {

    val isAuthenticated by authViewModel.isAuthenticated.observeAsState()

    val startDestination = when (isAuthenticated) {
        true -> Screen.ChatRoomsScreen.route
        false -> Screen.LoginScreen.route
        else -> null
    }

    Log.d("네비게이션그래프", "isAuthenticated: $isAuthenticated")

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.SignupScreen.route) {
                SignUpScreen(
                    authViewModel = authViewModel,
                    onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
                )
            }
            composable(Screen.LoginScreen.route) {
                LoginScreen(
                    authViewModel = authViewModel,
                    onNavigateToSignUp = { navController.navigate(Screen.SignupScreen.route) },
                    onSignInSuccess = {
                        navController.navigate(Screen.ChatRoomsScreen.route)
                    }
                ) {
                    navController.navigate(Screen.ChatRoomsScreen.route)
                }
            }
            composable(Screen.ChatRoomsScreen.route) {

                val isAuthenticate by authViewModel.isAuthenticated.observeAsState()

                LaunchedEffect(isAuthenticate) {
                    if (isAuthenticate == false) {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.ChatRoomsScreen.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                if (isAuthenticate == true) {
                    ChatRoomListScreen(
                        roomViewModel = roomViewModel,
                        authViewModel = authViewModel,
                        onJoinClicked = {
                            navController.navigate("${Screen.ChatScreen.route}/${it.id}")
                        },
                        onLogoutClicked = {
                            authViewModel.logout()
                        }
                    )
                }
            }
            composable("${Screen.ChatScreen.route}/{roomId}") {
                val roomId: String = it.arguments?.getString("roomId") ?: ""
                val roomName: String = it.arguments?.getString("roomName") ?: ""
                ChatScreen(
                    roomId = roomId,
                    roomName = "코틀린 스터디방",
                    onBackPressed = { navController.popBackStack() }
                )
            }
        }
    } else {
        androidx.compose.material.Text("로딩 중...")
    }

}