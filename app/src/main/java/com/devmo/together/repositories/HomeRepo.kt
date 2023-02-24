package com.devmo.together.repositories

import android.net.Uri
import android.util.Log
import com.devmo.together.helpers.Resource
import com.devmo.together.models.DemandPost
import com.devmo.together.models.HomePost
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

    suspend fun createHomePost(post: HomePost, uri: Uri) = flow {
        emit(Resource.loading(null))
        try {
            val user = getUsers()?.getValue<User>()
            val reference = mDatabase.getReference("$supportPost/${mAuth.currentUser?.uid}")
            val oldPost = reference.get().await().getValue<SupportPost>()
            // TODO: upload the img
            val imgRef = mStorage.getReference("images/support/${mAuth.currentUser?.uid}")
            val uploadTask = imgRef.putFile(uri)
            uploadTask.await()
            val img = imgRef.downloadUrl.await().toString()
            if (user != null && oldPost != null) {
                post.id = user.id
                post.name = user.name
                post.userImg = user.imageURL
                post.location = oldPost.location
                post.postImage = img
                reference.removeValue()
                val task = mDatabase.getReference("homePosts")
                    .child("${mAuth.currentUser?.uid}").setValue(post)
                task.await()
                if (task.isSuccessful) {
                    emit(Resource.success("posted"))
                }
            } else {
                emit(Resource.error(null, "something went wrong!"))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }

    }.flowOn(Dispatchers.IO)

    suspend fun getHomePosts() = flow {
        emit(Resource.loading(null))
        try {
            val children = mDatabase.getReference("homePosts").get().await().children
            val posts = mutableListOf<HomePost>()
            for (i in children){
                i.getValue(HomePost::class.java)?.let { posts.add(it) }
            }
            emit(Resource.success(posts))
        } catch (e: Exception) {
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

    suspend fun updateUserPic(uri: Uri) = flow {
        emit(Resource.loading(null))
        try {
            val reference = mStorage.getReference("images/profile/${mAuth.currentUser?.uid}")
            val uploadTask = reference.putFile(uri)
            uploadTask.await()
            val img = reference.downloadUrl.await().toString()
            val value = mDatabase.getReference("users/${mAuth.currentUser?.uid}/imageURL")
                .setValue(img)
            val ref1 = mDatabase.getReference("$demandPost/${mAuth.currentUser?.uid}")
            val ref2 = mDatabase.getReference("$supportPost/${mAuth.currentUser?.uid}")
            val ref3 = mDatabase.getReference("homePosts/${mAuth.currentUser?.uid}")
            if(ref1.get().await().exists()){
                ref1.child("userImg").setValue(img)
            }
            if(ref2.get().await().exists()){
                ref2.child("userImg").setValue(img)
            }
            if(ref3.get().await().exists()){
                ref3.child("userImg").setValue(img)
            }
            value.await()
            if (value.isSuccessful) {
                emit(Resource.success(img))
            }
        } catch (e: Exception) {
            emit(e.localizedMessage?.let { Resource.error(null, it) })
        }
    }.flowOn(Dispatchers.IO)
    suspend fun removeSupportPost()= flow {
        emit(Resource.loading(null))
        try {
            val reference = mDatabase.getReference("$supportPost/${mAuth.currentUser?.uid}")
            val task = reference.removeValue()
            task.await()
            if (task.isSuccessful){
                emit(Resource.success("post deleted"))
            }
        }catch (e:Exception){
            emit(e.localizedMessage?.let { Resource.error(null , it) })
        }
    }.flowOn(Dispatchers.IO)
    suspend fun removeDemandPost()= flow {
        emit(Resource.loading(null))
        try {
            val reference = mDatabase.getReference("$demandPost/${mAuth.currentUser?.uid}")
            val task = reference.removeValue()
            task.await()
            if (task.isSuccessful){
                emit(Resource.success("post deleted"))
            }
        }catch (e:Exception){
            emit(e.localizedMessage?.let { Resource.error(null , it) })
        }
    }
}