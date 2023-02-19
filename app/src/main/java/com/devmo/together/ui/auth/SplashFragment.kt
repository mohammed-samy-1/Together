package com.devmo.together.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.devmo.together.R
import com.devmo.together.databinding.FragmentSplashBinding
import com.devmo.together.helpers.Helpers
import com.devmo.together.ui.home.HomeActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SplashFragment : Fragment() {

    lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences =
            requireActivity().getSharedPreferences(Helpers.preferencesName, Context.MODE_PRIVATE)
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
            sharedPreferences.edit().putBoolean("first_time", false).apply()
        }
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences =
            requireActivity().getSharedPreferences(Helpers.preferencesName, Context.MODE_PRIVATE)
        if (sharedPreferences.contains("first_time")){
            binding.btnGetStarted.visibility = View.GONE
            lifecycleScope.launch {
                delay(1500)
                if (FirebaseAuth.getInstance().currentUser == null){
                    findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
                }else{
                    startActivity(Intent(requireContext() , HomeActivity::class.java))
                }
            }
        }
    }
}