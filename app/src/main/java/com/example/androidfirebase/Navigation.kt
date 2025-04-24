package com.example.androidfirebase

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.androidfirebase.ui.theme.LoginScreen
import com.example.androidfirebase.ui.theme.SignUpScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    context: Context,
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
                    Log.d("에러발생!!!!", "돌아감????????????????")
                    Toast.makeText(
                        context,
                        "로그인 인증 완료",
                        Toast.LENGTH_LONG
                    ).show()
                }
            ) {
                navController.navigate(Screen.ChatRoomsScreen.route)
            }
        }
    }
}