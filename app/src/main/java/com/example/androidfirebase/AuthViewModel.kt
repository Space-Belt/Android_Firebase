package com.example.androidfirebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val userRepository: UserRepository = UserRepository(
        FirebaseAuth.getInstance(),
        Injection.instance()
    )
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult: LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
//            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
            val result = userRepository.signUp(email, password, firstName, lastName)
            _authResult.value = result
            if (result is Result.Error) {
                _errorMessage.value = result.exception.message
            } else {
                _errorMessage.value = null
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            _authResult.value = result
            if (result is Result.Error) {
                _errorMessage.value = result.exception.message
            } else {
                _errorMessage.value = null
            }
            Log.d("에러에러에러", "${_authResult.value}")
        }
    }

}

