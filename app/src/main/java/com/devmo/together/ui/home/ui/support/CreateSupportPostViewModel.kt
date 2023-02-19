package com.devmo.together.ui.home.ui.support

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.devmo.together.models.SupportPost
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateSupportPostViewModel @Inject constructor(private val repo: HomeRepo): ViewModel() {
    suspend fun post(post: SupportPost , uri: Uri) = repo.createSupportPost(post , uri)
}