package com.devmo.together.ui.home.ui.profile

import androidx.lifecycle.ViewModel
import com.devmo.together.models.HomePost
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CompletePosttViewModel @Inject constructor(private val repo: HomeRepo) : ViewModel() {
    suspend fun createPost(post: HomePost) = repo.createHomePost(post)

}