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

    // 인증 상태 관리
    private val _isAuthenticated = MutableLiveData<Boolean>()
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    // 유저 정보
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    // 초기화, Firebase Auth 상태 변화 리스너 추가 & 로그인된 상태면 사용자 정보 가져오기& 로그아웃된 상태면 사용자 정보 클리어
    init {
        FirebaseAuth.getInstance().addAuthStateListener { auth ->
            val isLoggedIn = auth.currentUser != null
            _isAuthenticated.value = isLoggedIn
            if (isLoggedIn) {
                getCurrentUserInfo()
            } else {
                _currentUser.value = null
            }
        }
    }


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
            Log.d("에러에러에러", "${result}")
            if (result is Result.Error) {
                _errorMessage.value = result.exception.message
            } else {
                _errorMessage.value = null
            }
            Log.d("에러에러에러", "${_authResult.value}")
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                _errorMessage.value = null
                Log.d("로그아웃", "로그아웃 성공")
            } catch (e: Exception) {
                _errorMessage.value = "로그아웃 중 오류가 발생했습니다."
                Log.e("로그아웃", "로그아웃 실패: ${e.message}")
            }
        }
    }

    private fun getCurrentUserInfo() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUser()
            if (result is Result.Success) {
                _currentUser.value = result.data
            } else if (result is Result.Error) {
                Log.e("사용자정보", "사용자 정보 가져오기 실패: ${result.exception.message}")
            }
        }
    }

    fun checkAuthenticationStatus() {
        _isAuthenticated.value = FirebaseAuth.getInstance().currentUser != null
    }

    // 에러 메시지 클리어
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

}

