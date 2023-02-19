package com.devmo.together.ui.home.ui.support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.devmo.together.R
import com.devmo.together.databinding.FragmentSupportBinding
import com.devmo.together.helpers.Status
import com.devmo.together.models.SupportPost
import com.devmo.together.ui.adapters.SupportPotAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SupportFragment : Fragment() {

    private val binding by lazy { FragmentSupportBinding.inflate(layoutInflater) }
    private val viewModel: SupportViewModel by viewModels()
    private lateinit var mAdapter: SupportPotAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_createSupportPostFragment)
        }

        mAdapter = SupportPotAdapter {
            request(it)
        }
        binding.rv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
            visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.getPosts().collect {
                when (it?.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        mAdapter.submitList(it.data)
                    }
                    else -> {
                        return@collect
                    }
                }
            }
        }
    }

    private fun request(supportPost: SupportPost) {
        lifecycleScope.launch {
            viewModel.request(supportPost).collect {
                when (it?.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.data.toString(), Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        return@collect
                    }
                }
            }
        }
    }
}