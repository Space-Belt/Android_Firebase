package com.example.androidfirebase

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import com.google.firebase.auth.FirebaseAuthUserCollisionException

class UserRepository(private val auth: FirebaseAuth,
                     private val firestore: FirebaseFirestore
) {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> get() = _currentUser

    suspend fun getCurrentUser(): Result<User> = try {
        val firebaseUser = auth.currentUser ?: throw IllegalStateException("Not logged in")

        val uid = firebaseUser.uid
        val document = firestore.collection("users")
            .document(firebaseUser.uid) // ✅ email 대신 uid 사용
            .get()
            .await()

        if (!document.exists()) {
            throw IllegalStateException("Document not found for UID: $uid")
        }

        val user = document.toObject(User::class.java)
            ?: throw IllegalStateException("User data not found")

        _currentUser.postValue(user) // LiveData 업데이트
        Result.Success(user)
    } catch (e: Exception) {
        Result.Error(e)
    }

    private suspend fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    suspend fun signUp(email: String, password: String, firstName: String, lastName: String): Result<Boolean> =
        try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            //add user to firestore
            val user = User(firstName, lastName, email,
                uid = authResult.user?.uid ?: throw IllegalStateException("UID is null"))
            saveUserToFirestore(user)
            Result.Success(true)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.e("유저레포지토리!!!!", "이미 등록된 이메일: ${e.message}")
            Result.Error(Exception("이미 등록된 이메일입니다."))
        } catch (e: Exception) {
            Log.e("유저레포지토리!!!!", "getCurrentUser failed: ${e.stackTraceToString()}")
            Result.Error(e)
        }


    suspend fun login(email: String, password: String): Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d("UserRepository", "로그인 성공")
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "로그인 실패: ${e.message}")
            Result.Error(e)
        }

    fun logout(): Result<Boolean> =
        try {
            auth.signOut()
            _currentUser.postValue(null) // 현재 사용자 정보 클리어
            Log.d("UserRepository", "로그아웃 성공")
            Result.Success(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "로그아웃 실패: ${e.message}")
            Result.Error(e)
        }

    // 현재 로그인 상태 확인
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // 현재 사용자 UID 가져오기
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // 현재 사용자 이메일 가져오기
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}

object Injection {
    private val instance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    fun instance(): FirebaseFirestore {
        return instance
    }
}