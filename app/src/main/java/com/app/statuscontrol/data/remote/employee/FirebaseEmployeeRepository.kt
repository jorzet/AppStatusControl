package com.app.statuscontrol.data.remote.employee

import com.app.statuscontrol.data.util.FirebaseConstants
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.repository.EmployeeRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseEmployeeRepository @Inject constructor(

): EmployeeRepository {

    override suspend fun getAllEmployees(): List<User>? {
        return try {
            val collectionReference = FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS_COLLECTION)
            val employees = collectionReference.whereEqualTo("userType", "employee")
                .get()
                .await()
                .toObjects(User::class.java)

            employees
        } catch (e: Exception) {
            null
        }
    }
}