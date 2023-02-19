package com.devmo.together.ui.auth

import androidx.lifecycle.ViewModel
import com.devmo.together.repositories.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(private val mRepo: AuthRepo) : ViewModel() {
    suspend fun login(email: String, password: String)  = mRepo.login(email, password)
}