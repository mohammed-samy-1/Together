package com.devmo.together.ui.home.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.TransitionBuilder.validate
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.devmo.together.R
import com.devmo.together.databinding.FragmentCompletePosttBinding
import com.devmo.together.models.HomePost
import dagger.hilt.android.AndroidEntryPoint
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
        init()
    }

    private fun init() {
        binding.cclGetImg.setOnClickListener {
            if (uri != null
                && binding.etBankAcc.text.toString().isNotBlank()
            ) {
                lifecycleScope.launch {
                    viewModel.createPost(
                        HomePost(
                            bankAccount = binding.etBankAcc.text.toString()
                        )
                    )
                }
            }
        }
    }


}