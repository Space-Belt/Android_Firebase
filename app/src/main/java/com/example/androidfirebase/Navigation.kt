package com.example.androidfirebase

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route
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
            ChatRoomListScreen(roomViewModel = roomViewModel, authViewModel){
                navController.navigate("${Screen.ChatScreen.route}/${it.id}")
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
}