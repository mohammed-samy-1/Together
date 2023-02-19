package com.devmo.together.ui.home.ui.demand

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.devmo.together.models.DemandPost
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateDemandPostViewModel @Inject constructor(private val repo: HomeRepo) : ViewModel() {
    suspend fun post(post: DemandPost,uri: Uri) = repo.createDemandPost(post, uri)
}