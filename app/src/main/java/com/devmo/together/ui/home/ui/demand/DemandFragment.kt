package com.devmo.together.ui.home.ui.demand

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmo.together.R
import com.devmo.together.databinding.FragmentDemandBinding
import com.devmo.together.helpers.Status
import com.devmo.together.ui.adapters.DemandPostAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DemandFragment : Fragment() {

    private val viewModel: DemandViewModel by viewModels()
    private val binding by lazy {
        FragmentDemandBinding.inflate(layoutInflater)
    }
    private lateinit var mAdapter: DemandPostAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton2.setOnClickListener {
            findNavController().navigate(R.id.action_demandFragment_to_createDemandPostFragment)
        }
        mAdapter = DemandPostAdapter()
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            visibility = View.VISIBLE
        }
    }
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch{
            viewModel.getPosts().collect {
                when(it?.status){
                    Status.LOADING -> {
                        binding.progressBar3.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar3.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.progressBar3.visibility = View.GONE
                        mAdapter.submitList(it.data)
                    }
                    else -> {
                        return@collect
                    }
                }
            }
        }
    }
}