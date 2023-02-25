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
import com.devmo.together.databinding.FragmentSignUpBinding
import com.devmo.together.helpers.Helpers.Companion.validateEmail
import com.devmo.together.helpers.Helpers.Companion.validateName
import com.devmo.together.helpers.Helpers.Companion.validatePass
import com.devmo.together.helpers.Status
import com.devmo.together.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()
    private val binding: FragmentSignUpBinding by lazy {
        FragmentSignUpBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    private fun validateData(): Boolean {
        if (
            binding.etEmail.text.toString().validateEmail()
            && binding.etName.toString().validateName()
            && binding.etPass.toString().validatePass()
        )
            return true
        else {
            when {
                !binding.etEmail.text.toString().validateEmail() -> {
                    binding.etEmail.error = "please enter valid email"
                }
                !binding.etPass.text.toString().validatePass() -> {
                    binding.etPass.error =
                        "password should be at least 8 letters, number or symbols"
                }
                !binding.etName.text.toString().validateName() -> {
                    binding.etName.error = "name should be at least 3 letters!"
                }
            }
            return false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btSignUp.setOnClickListener {
            if (validateData())
                lifecycleScope.launch {
                    signUp(
                        binding.etEmail.text.toString(),
                        binding.etPass.text.toString(),
                        binding.etName.text.toString()
                    )
                }
        }
        binding.txtSignUp.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private suspend fun signUp(email: String, password: String, name: String) {
        viewModel.signUp(email, password, name).collect{
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