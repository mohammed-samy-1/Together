package com.devmo.together.ui.home.ui.demand

import androidx.lifecycle.ViewModel
import com.devmo.together.repositories.HomeRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DemandViewModel @Inject constructor(private val repo: HomeRepo): ViewModel() {
    suspend fun getPosts() = repo.getDemandPosts()
}