package com.devmo.together.ui.home.ui.support

import android.content.Intent
import android.net.Uri
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
import com.devmo.together.databinding.FragmentCreateSupportPostBinding
import com.devmo.together.helpers.Status
import com.devmo.together.models.SupportPost
import com.devmo.together.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateSupportPostFragment : Fragment() {


    private val viewModel: CreateSupportPostViewModel by viewModels()
    private val binding by lazy {
        FragmentCreateSupportPostBinding.inflate(layoutInflater)
    }
    private var uri: Uri? = null
    private lateinit var launcher: ActivityResultLauncher<String>
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
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.GONE
        binding.btnPost.setOnClickListener {
            post()
        }
        binding.llImage.setOnClickListener {
            getImg()
        }
    }

    private fun validateData(): Boolean {
        if (uri != null
            && binding.etLocation.text.isNotBlank()
            && binding.etPhone.text.isNotBlank()
            && binding.etDesc.text.isNotBlank()
        ) {
            return true
        } else {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT)
                .show()
            return false
        }
    }

    private fun getImg() {
        launcher.launch("image/*")
    }

    private fun post() {
        if (validateData()) {
            lifecycleScope.launch {
                viewModel.post(
                    SupportPost(
                        "", "", "",
                        binding.etDesc.text.toString(), "",
                        binding.etPhone.text.toString(),
                        binding.etLocation.text.toString(),
                        binding.spGender.selectedItem as String,
                        (binding.spRooms.selectedItem as String).toInt(),
                        (binding.spPeople.selectedItem as String).toInt(),
                    ), uri!!
                ).collect {
                    when (it?.status) {
                        Status.LOADING -> {
                            binding.nestedScrollView2.visibility = View.VISIBLE
                            binding.progressBar5.visibility = View.GONE
                            Toast.makeText(requireContext(), "posting..", Toast.LENGTH_LONG).show()
                        }
                        Status.ERROR -> {
                            binding.nestedScrollView2.visibility = View.VISIBLE
                            binding.progressBar5.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        }
                        Status.SUCCESS -> {
                            binding.nestedScrollView2.visibility = View.VISIBLE
                            binding.progressBar5.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.VISIBLE
    }
}