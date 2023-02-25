package com.devmo.together.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.devmo.together.R
import com.devmo.together.databinding.FragmentLogInBinding
import com.devmo.together.helpers.Helpers.Companion.validateEmail
import com.devmo.together.helpers.Helpers.Companion.validatePass
import com.devmo.together.helpers.Status
import com.devmo.together.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogInFragment : Fragment() {


    private val viewModel: LogInViewModel by viewModels()
    private val binding: FragmentLogInBinding by lazy { FragmentLogInBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPass.text.toString()
            if (email.validateEmail() && password.validatePass()) {
                lifecycleScope.launch {
                    login(email, password)
                }
            } else {
                when {
                    !email.validateEmail() -> binding.etEmail.error =
                        "please enter a valid email"
                    !password.validatePass() -> binding.etPass.error =
                        "password should be at least 8 letters or symbols"
                }
            }
        }
        binding.txtSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
        }
    }

    private suspend fun login(email: String, password: String) {
        viewModel.login(email, password).collect() {
            when (it?.status) {
                Status.LOADING -> {
                    binding.progressBar2.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.GONE
                }
                Status.ERROR -> {
                    binding.progressBar2.visibility = View.GONE
                    binding.constraintLayout.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                }
                Status.SUCCESS -> {
                    binding.progressBar2.visibility = View.GONE
                    binding.constraintLayout.visibility = View.VISIBLE
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    activity?.finish()
                }
                else -> {
                    return@collect
                }
            }
        }
    }



}