package com.devmo.together.ui.home.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.devmo.together.R
import com.devmo.together.databinding.FragmentGuideBinding
import com.devmo.together.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class GuideFragment : Fragment() {
    // TODO: Rename and change types of parameters

    val binding by lazy {
        FragmentGuideBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if(activity is HomeActivity){
            val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            nav.visibility = View.GONE
        }else{
            binding.btnNext.visibility = View.VISIBLE
            binding.btnNext.setOnClickListener{
                findNavController().navigate(R.id.action_guideFragment2_to_logInFragment)
            }
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if(activity is HomeActivity){
            val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            nav.visibility = View.VISIBLE
        }
    }

}