package com.devmo.together.ui.home.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repo: HomeRepo): ViewModel() {
    suspend fun getUser() = repo.getCurrentUser()
    suspend fun getSupportPost() = repo.getSupportPost()
    suspend fun getDemandPost() = repo.getDemandPost()
    suspend fun uploadProfilePic(uri: Uri) = repo.updateUserPic(uri)
}