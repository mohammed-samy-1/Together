package com.devmo.together.ui.home.ui.support

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devmo.together.models.SupportPost
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SupportViewModel
@Inject constructor(private val repo: HomeRepo) : ViewModel() {
    suspend fun getPosts() = repo.getSupportPosts()
    suspend fun check(id:String) = repo.checkRequests(id)
    suspend fun request(post: SupportPost) = repo.request(post)
}