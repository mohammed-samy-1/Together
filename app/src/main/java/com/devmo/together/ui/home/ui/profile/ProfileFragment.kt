package com.devmo.together.ui.home.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devmo.together.R
import com.devmo.together.databinding.FragmentProfileBinding
import com.devmo.together.helpers.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private var uri:Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        var launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri = it
        }
    }

    override fun onStart() {
        super.onStart()
        initSupportPost()
        initDemandPost()
    }

    private fun initDemandPost() {
        lifecycleScope.launch {
            viewModel.getDemandPost().collect(){
                when(it?.status){
                    Status.LOADING -> {
                        binding.demandPostProfile.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        binding.demandPostProfile.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        binding.demandPostProfile.visibility = View.VISIBLE
                        binding.txtBodey.text = it.data?.postBody
                    }
                    else -> {
                        binding.demandPostProfile.visibility = View.GONE
                        return@collect
                    }
                }

            }
        }
    }

    private fun initSupportPost() {
        lifecycleScope.launch {
            viewModel.getSupportPost().collect(){
                when(it?.status){
                    Status.LOADING -> {
                        binding.supportPostProfile.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        binding.supportPostProfile.visibility = View.GONE
                    }
                    Status.SUCCESS -> {
                        binding.supportPostProfile.visibility = View.VISIBLE
                        binding.txtBody.text = it.data?.postBody
                    }
                    else -> {
                        binding.supportPostProfile.visibility = View.GONE
                        return@collect
                    }
                }

            }
        }
    }

    private fun initUserInfo() {
        lifecycleScope.launch {
            viewModel.getUser().collect{
                when(it?.status){
                    Status.LOADING -> {
                        binding.nestedScrollView.visibility = View.GONE
                        binding.progressBar4.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.nestedScrollView.visibility = View.GONE
                        binding.progressBar4.visibility = View.GONE
                        findNavController().popBackStack()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.nestedScrollView.visibility = View.VISIBLE
                        binding.progressBar4.visibility = View.GONE
                        Glide.with(requireContext())
                            .load(it.data!!.imageURL)
                            .placeholder(R.drawable.profile)
                            .into(binding.imgProfile)
                        Glide.with(requireContext())
                            .load(it.data.imageURL)
                            .placeholder(R.drawable.profile)
                            .into(binding.userImg)
                        Glide.with(requireContext())
                            .load(it.data.imageURL)
                            .placeholder(R.drawable.profile)
                            .into(binding.imgDemandPostProfile)
                        binding.txtName.text = it.data.name
                        binding.txtProfileName.text = it.data.name
                        binding.txtNameDemand.text = it.data.name
                        if (it.data.imageURL != ""){
                            binding.imgAdd.visibility = View.GONE
                        }

                    }
                    else -> {
                        return@collect
                    }
                }

            }
        }
    }

}