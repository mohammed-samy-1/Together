package com.devmo.together.ui.home.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.devmo.together.R
import com.devmo.together.databinding.FragmentProfileBinding
import com.devmo.together.helpers.Status
import com.devmo.together.ui.auth.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()
    private val binding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private lateinit var launcher : ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null)
                uploadImg(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUserInfo()
        initListener()
    }

    private fun initListener() {
        binding.imgProfile.setOnClickListener{
            launcher.launch("image/*")
        }
        binding.txtLogout.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            requireContext().startActivity(Intent(requireContext(),  MainActivity::class.java))
        }
        binding.btnComp.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_completePosttFragment)
        }
        binding.txtGuide.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_guideFragment)
        }
        binding.txtSupportUs.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_supportUsFragment)
        }
        binding.imgDelSupport.setOnClickListener {
            deleteSupport()
        }
        binding.imgDel.setOnClickListener {
            deleteDemand()
        }
    }

    private fun deleteDemand() {
        lifecycleScope.launch{
            viewModel.removeDemand().collect{
                when(it?.status){
                    Status.SUCCESS->{
                        binding.demandPostProfile.visibility = View.GONE
                        Toast.makeText(binding.root.context , "post deleted" , Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar4.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar4.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.demandPostProfile.visibility = View.GONE
                        return@collect
                    }
                }
            }
        }
    }

    private fun deleteSupport() {
        lifecycleScope.launch{
            viewModel.removeSupport().collect{
                when(it?.status){
                    Status.SUCCESS->{
                        binding.supportPostProfile.visibility = View.GONE
                        Toast.makeText(binding.root.context , "post deleted" , Toast.LENGTH_SHORT).show()
                    }
                    Status.LOADING -> {
                        binding.progressBar4.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar4.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        binding.supportPostProfile.visibility = View.GONE
                        return@collect
                    }
                }
            }
        }
    }

    private fun uploadImg(uri: Uri) {
        lifecycleScope.launch{
            viewModel.uploadProfilePic(uri).collect{
                when (it?.status) {
                    Status.LOADING -> {
                        binding.progressBar4.visibility = View.VISIBLE
                    }
                    Status.ERROR -> {
                        binding.progressBar4.visibility = View.GONE
                        Toast.makeText(requireContext() , it.message , Toast.LENGTH_LONG).show()
                    }
                    Status.SUCCESS -> {
                        binding.progressBar4.visibility = View.GONE
                        Glide.with(requireContext())
                            .load(it.data)
                            .placeholder(R.drawable.profile)
                            .into(binding.imgProfile)
                    }
                    else -> {
                        binding.supportPostProfile.visibility = View.GONE
                        return@collect
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        initSupportPost()
        initDemandPost()
    }

    private fun initDemandPost() {
        lifecycleScope.launch {
            viewModel.getDemandPost().collect() {
                when (it?.status) {
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
            viewModel.getSupportPost().collect() {
                when (it?.status) {
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
            viewModel.getUser().collect {
                when (it?.status) {
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
                        if (it.data.imageURL != "") {
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