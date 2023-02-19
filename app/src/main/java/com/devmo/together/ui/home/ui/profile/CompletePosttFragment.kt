package com.devmo.together.ui.home.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.devmo.together.R

class CompletePosttFragment : Fragment() {

    companion object {
        fun newInstance() = CompletePosttFragment()
    }

    private lateinit var viewModel: CompletePosttViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete_postt, container, false)
    }



}