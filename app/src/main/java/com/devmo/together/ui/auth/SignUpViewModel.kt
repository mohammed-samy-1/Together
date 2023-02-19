package com.devmo.together.ui.auth

import androidx.lifecycle.ViewModel
import com.devmo.together.repositories.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repo: AuthRepo) : ViewModel() {
    suspend fun signUp(email: String, password: String, name: String) =
        repo.signUp(email, password, name)
}