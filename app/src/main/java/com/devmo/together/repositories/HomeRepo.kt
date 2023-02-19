package com.devmo.together.repositories

import android.net.Uri
import android.util.Log
import com.devmo.together.helpers.Resource
import com.devmo.together.models.DemandPost
import com.devmo.together.models.SupportPost
import com.devmo.together.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.Notification

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.util.UUID

class HomeRepo(
    private val mDatabase: FirebaseDatabase,
    private val mAuth: FirebaseAuth,
    private val mStorage: FirebaseStorage
) {
    private val supportPost = "supportPosts"
    private val demandPost = "demandPosts"
    val userRequestPath = "userRequest"
    val postRequestPath = "postRequest"
    suspend fun getUsers(): DataSnapshot? {
        return mDatabase.getReference("users/${mAuth.currentUser?.uid}").get().await()
    }

    suspend fun getCurrentUser() = flow {
        emit(Resource.loading(null))
        try {
            val user = mDatabase.getReference("users/${mAuth.currentUser?.uid}").get()
                .await()
                .getValue(User::class.java)
            if (user != null) {
                emit(Resource.success(user))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createSupportPost(post: SupportPost, uri: Uri) = flow {
        emit(Resource.loading(null))
        try {
            val user = getUsers()?.getValue(User::class.java)
            val reference = mStorage.getReference("images/support/${mAuth.currentUser?.uid}")
            val uploadTask = reference.putFile(uri)
            uploadTask.await()
            val img = reference.downloadUrl.await().toString()
            post.postImage = img
            if (user != null) {
                post.userImg = user.imageURL
                post.id = user.id
                post.name = user.name
            }
            val task =
                mDatabase.getReference("${supportPost}/${mAuth.currentUser?.uid}").setValue(post)
            task.await()
            val token = FirebaseMessaging.getInstance().token.await()
            val task2 =
                mDatabase.getReference("tokens/${mAuth.currentUser?.uid}")
                    .setValue(token)
            task2.await()
            if (task.isSuccessful && task2.isSuccessful) {
                emit(Resource.success("Posted!"))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { it1 -> Resource.error(null, it1) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getSupportPosts() = flow {
        emit(Resource.loading(null))

        try {
            val children = mDatabase.getReference(supportPost).get().await().children
            val li = mutableListOf<SupportPost>()
            for (i in children) {
                i.getValue(SupportPost::class.java)?.let { li.add(it) }
            }
            emit(Resource.success(li.toList()))
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }

    }.flowOn(Dispatchers.IO)

    suspend fun createDemandPost(post: DemandPost, uri: Uri) = flow {
        emit(Resource.loading(null))
        try {
            val user = getUsers()?.getValue(User::class.java)
            val reference = mStorage.getReference("images/demand/${mAuth.currentUser?.uid}")
            val uploadTask = reference.putFile(uri)
            uploadTask.await()
            val img = reference.downloadUrl.await().toString()
            post.postImage = img
            if (user != null) {
                post.userImg = user.imageURL
                post.id = user.id
                post.name = user.name
            }
            val task =
                mDatabase.getReference("${demandPost}/${mAuth.currentUser?.uid}").setValue(post)
            task.await()
            if (task.isSuccessful) {

                emit(Resource.success("Posted!"))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { it1 -> Resource.error(null, it1) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDemandPosts() = flow {
        emit(Resource.loading(null))
        try {
            val children = mDatabase.getReference(demandPost).get().await().children
            val li = mutableListOf<DemandPost>()
            for (i in children) {
                i.getValue(DemandPost::class.java)?.let { li.add(it) }
            }
            emit(Resource.success(li.toList()))
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }

    }.flowOn(Dispatchers.IO)

    suspend fun getSupportPost() = flow {
        emit(Resource.loading(null))
        try {
            val ref = mDatabase.getReference("$supportPost/${mAuth.currentUser?.uid}")
            if (ref.get().await().exists()) {
                emit(Resource.success(ref.get().await().getValue(SupportPost::class.java)))
            } else {
                emit(Resource.noData("no posts yet!"))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDemandPost() = flow {
        emit(Resource.loading(null))
        try {

            val ref = mDatabase.getReference("$demandPost/${mAuth.currentUser?.uid}")
            if (ref.get().await().exists()) {
                emit(Resource.success(ref.get().await().getValue(DemandPost::class.java)))
            } else {
                emit(Resource.noData("no posts yet!"))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun checkRequests(id: String): Boolean {
        return mDatabase.getReference("requests")
            .child("${mAuth.currentUser?.uid}")
            .child(id).get().await().exists()
    }

    suspend fun request(post: SupportPost) = flow {
        emit(Resource.loading { null })
        try {
//            val task = mDatabase.getReference(postRequestPath).child(post.id)
//                .setValue(mAuth.currentUser?.uid)
//            val task1 = mDatabase.getReference(userRequestPath).child("${mAuth.currentUser?.uid}")
//                .push().setValue(post.id)
//            task.await()
//            task1.await()
//            if (task.isSuccessful && task1.isSuccessful) {}
            val token =
                mDatabase.getReference("tokens").child(post.id).get().await().getValue<String>()
            val user = mDatabase.getReference("users/${mAuth.currentUser?.uid}").get().await()
                .getValue<User>()
            if (token != null) {
                val msg = RemoteMessage.Builder(token).apply {
                    messageId = UUID.randomUUID().toString()
                    data = mapOf(
                        "title" to user?.name,
                        "message" to "can I stay in your house please"
                    )
                }.build()
                Log.d("mohammed", "request: ${msg.toString()}")
                FirebaseMessaging.getInstance().send(
                    msg
                ).also {
                    emit(Resource.success("request sent"))
                }
            }
//
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateUserPic(uri: Uri) {
        TODO("update the img in the posts if exists")
    }
}