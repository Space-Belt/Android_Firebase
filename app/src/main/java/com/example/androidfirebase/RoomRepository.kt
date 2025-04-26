package com.example.androidfirebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RoomRepository(private val firestore: FirebaseFirestore) {

    suspend fun createRoom(name: String): Result<Unit> = try {
//        val document = firestore.collection("rooms").document()
        val room = Room(name = name)
//        document.set(room).await()
        firestore.collection("rooms").add(room).await()
        Log.d("도냐?", "성공")
        Result.Success(Unit)
    } catch (e: Exception) {
        Log.d("도냐?", "${e.message}")
        Result.Error(e)
    }

    suspend fun getRooms(): Result<List<Room>> = try {
        val querySnapshot = firestore.collection("rooms").get().await()
        val rooms = querySnapshot.documents.map { document ->
            document.toObject(Room::class.java)!!.copy(id = document.id)
        }
        Result.Success(rooms)
    } catch (e: Exception) {
        Result.Error(e)
    }


}