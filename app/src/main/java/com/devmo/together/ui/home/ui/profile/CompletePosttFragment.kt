package com.devmo.together.ui.home.ui.profile

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
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.devmo.together.R
import com.devmo.together.databinding.FragmentCompletePosttBinding
import com.devmo.together.helpers.Status
import com.devmo.together.models.HomePost
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompletePosttFragment : Fragment() {
    private lateinit var launcher: ActivityResultLauncher<String>
    private var uri: Uri? = null
    private val viewModel: CompletePosttViewModel by viewModels()
    private val binding by lazy {
        FragmentCompletePosttBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.GONE
        launcher = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null) {
                uri = it
                binding.txtImg.text = it.path
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.getImg.setOnClickListener {
            launcher.launch("image/*")
        }
        init()
    }

    private fun init() {

        binding.btn.setOnClickListener {
            if (uri != null
                && binding.etBankAcc.text.toString().isNotBlank()
            ) {
                lifecycleScope.launch {
                    viewModel.createPost(
                        HomePost(
                            bankAccount = binding.etBankAcc.text.toString()
                        ),
                        uri!!
                    ).collect{
                        when(it?.status){
                            Status.LOADING -> {
                                binding.scrolview.visibility = View.GONE
                                binding.progressBar7.visibility = View.VISIBLE
                                Toast.makeText(requireContext(),"posting..", Toast.LENGTH_LONG).show()
                            }
                            Status.ERROR -> {
                                binding.scrolview.visibility = View.VISIBLE
                                binding.progressBar7.visibility = View.GONE
                                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                            }
                            Status.SUCCESS -> {
                                binding.scrolview.visibility = View.VISIBLE
                                binding.progressBar7.visibility = View.GONE
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
    override fun onDestroy() {
        super.onDestroy()
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.VISIBLE
    }

}