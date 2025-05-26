package com.example.androidfirebase.ui.theme

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidfirebase.AuthViewModel
import com.example.androidfirebase.Result
import com.google.firebase.auth.FirebaseAuthException
import java.util.regex.Pattern

@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit

){
    val firebaseAuthErrorMessages = mapOf(
        "ERROR_INVALID_EMAIL" to "유효하지 않은 이메일 형식입니다.",
        "ERROR_EMAIL_ALREADY_IN_USE" to "이미 사용 중인 이메일입니다.",
        "ERROR_USER_NOT_FOUND" to "존재하지 않는 계정입니다.",
        "ERROR_WRONG_PASSWORD" to "비밀번호가 일치하지 않습니다.",
        "ERROR_USER_DISABLED" to "비활성화된 계정입니다.",
        "ERROR_TOO_MANY_REQUESTS" to "잠시 후 다시 시도해주세요.",
        "ERROR_MISSING_EMAIL" to "이메일을 입력해주세요.",
        "ERROR_MISSING_PASSWORD" to "비밀번호를 입력해주세요."
        // 필요에 따라 추가
    )

    fun translateErrorMessage(errorCode: String?): String {
        if (errorCode == null) return ""
        return firebaseAuthErrorMessages[errorCode] ?: errorCode
    }

    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(true) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var isPasswordCheck by remember {  mutableStateOf(true) }

    var password by remember { mutableStateOf("") }
    var checkPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    val emailPattern = Pattern.compile(
        "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    )

    val passwordPattern = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#\$%^&*])[A-Za-z\\d!@#\$%^&*]{8,}$"
    )

    val authResult by authViewModel.authResult.observeAsState()
    val errorMessage by authViewModel.errorMessage.observeAsState()

    val isSignUpSuccess = (authResult as? Result.Success<Boolean>)?.data == true
    // 회원가입 성공 시 처리
    LaunchedEffect(isSignUpSuccess) {
        if (isSignUpSuccess) {
            email = ""
            password = ""
            checkPassword = ""
            firstName = ""
            lastName = ""
            onNavigateToLogin()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                isEmailValid = emailPattern.matcher(it).matches()
            },
            label = { Text("이메일") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        if (!isEmailValid && email.isNotBlank()) {
            Text(
                text = "올바른 이메일 주소를 입력하세요.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                isPasswordValid = passwordPattern.matcher(it).matches()
            },
            label = { Text("비밀번호") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        if (!isPasswordValid && password.isNotBlank()) {
            Text(
                text = "비밀번호는 8자이상 숫자,알파벳,특수기호를 포함해야합니다",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        OutlinedTextField(
            value = checkPassword,
            onValueChange = {
                checkPassword = it
                isPasswordCheck = (password == it)
            },
            label = { Text("비밀번호 확인") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            visualTransformation = PasswordVisualTransformation()
        )

        if (!isPasswordCheck && checkPassword.isNotBlank()) {
            Text(
                text = "비밀번호가 일치하지 않습니다.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("이름") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        if (firstName.isNotBlank()) {
            Text(
                text = "이름을 입력해주세요.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button (
            onClick = {
                authViewModel.signUp(email, password, firstName, lastName)
                email = ""
                password = ""
                checkPassword = ""
                firstName = ""
                lastName = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("회원가입")
        }
        if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = translateErrorMessage(errorMessage),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("이미 회원이신가요? 로그인하세요!",
            modifier = Modifier.clickable {
                onNavigateToLogin()
            }
        )


    }
}

@Preview
@Composable
fun SignupPreview() {
//    SignUpScreen()
}
