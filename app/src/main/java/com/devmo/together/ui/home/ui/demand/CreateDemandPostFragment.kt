package com.devmo.together.ui.home.ui.demand

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
import com.devmo.together.R
import com.devmo.together.databinding.FragmentCreateDemandPostBinding
import com.devmo.together.helpers.Status
import com.devmo.together.models.DemandPost
import com.devmo.together.models.SupportPost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class CreateDemandPostFragment : Fragment() {

    private val viewModel: CreateDemandPostViewModel by viewModels()
    private val binding by lazy {
        FragmentCreateDemandPostBinding.inflate(layoutInflater)
    }
    private lateinit var launcher : ActivityResultLauncher<String>
    private var uri :Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            uri = it
            binding.txtImgPath.text = uri?.path
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPost.setOnClickListener {
            post()
        }
        binding.llImage.setOnClickListener {
            getImg()
        }
    }


    private fun validateData(): Boolean {
        if (uri != null
            &&binding.etLocation.text.isNotBlank()
            &&binding.etPhone.text.isNotBlank()
            &&binding.etDesc.text.isNotBlank()
        ){
            return true
        }else{
            Toast.makeText(requireContext() , "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    private fun getImg() {
        launcher.launch("image/*")
    }

    private fun post() {
        if (validateData()){
            lifecycleScope.launch{
                viewModel.post(
                    DemandPost(
                    "", "","",
                    binding.etDesc.text.toString(), "",
                    binding.etPhone.text.toString(),
                    binding.etLocation.text.toString(),
                ),uri!!).collect{
                    when(it?.status){
                        Status.LOADING -> {
                            binding.lld.visibility = View.GONE
                            binding.progressBar6.visibility = View.VISIBLE
                            Toast.makeText(requireContext(),"posting..", Toast.LENGTH_LONG).show()
                        }
                        Status.ERROR -> {
                            binding.lld.visibility = View.VISIBLE
                            binding.progressBar6.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.SUCCESS -> {
                            binding.lld.visibility = View.VISIBLE
                            binding.progressBar6.visibility = View.GONE
                            Toast.makeText(requireContext(), it.data, Toast.LENGTH_LONG).show()
                            findNavController().popBackStack()
                        }
                        else -> {
                            return@collect
                        }
                    }
                }
            }
        }
    }

}