package com.devmo.together.ui.home.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmo.together.databinding.FragmentHomeBinding
import com.devmo.together.helpers.Status
import com.devmo.together.ui.adapters.HomePostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val binding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }
    val viewModel : HomeViewModel by viewModels()
    private lateinit var adapter :HomePostAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = HomePostAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
            visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        initData()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.getPosts().collect{
                when (it?.status) {
                    Status.LOADING -> {
                        binding.progressBar8.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar8.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.progressBar8.visibility = View.GONE
                        adapter.submitList(it.data)
                    }
                    else -> {
                        return@collect
                    }
                }

            }
        }
    }
}