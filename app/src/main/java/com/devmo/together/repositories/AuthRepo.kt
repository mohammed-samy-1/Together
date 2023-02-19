package com.devmo.together.repositories

import com.devmo.together.helpers.Resource
import com.devmo.together.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepo @Inject constructor(
    private val mAuth: FirebaseAuth,
    private val mDatabase: FirebaseDatabase
) {
    suspend fun login(email: String, password: String) = flow {
        emit(Resource.loading(null))

        try {
            val result = mAuth.signInWithEmailAndPassword(email, password).await()
            emit(Resource.success("welcome" + result.user!!.displayName))
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it.toString()) })
        }

    }.flowOn(Dispatchers.IO)

    suspend fun signUp(email:String ,password: String , name:String) = flow {
     emit(Resource.loading(null))
        try {
            val task = mAuth.createUserWithEmailAndPassword(email, password)
            val re = task.await()
            if (task.isSuccessful){
                val reference = mDatabase.getReference("users")
                val task2 = reference.child(re.user?.uid.toString()).setValue(
                    User(
                        id = re.user?.uid.toString(),
                        name = name,
                        email = email,
                        passL = password.length
                    )
                )
                task2.await()
                if (task2.isSuccessful){
                    emit(Resource.success("account created!"))
                }

            }

        }catch (e:Exception){
            emit(e.localizedMessage?.let { Resource.error(null , it) })
        }


    }.flowOn(Dispatchers.IO)

}