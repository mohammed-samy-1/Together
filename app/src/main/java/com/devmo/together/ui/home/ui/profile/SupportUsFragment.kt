package com.devmo.together.ui.home.ui.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.devmo.together.R
import com.devmo.together.databinding.FragmentSupportUsBinding
import com.devmo.together.ui.home.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class SupportUsFragment : Fragment() {

    val binding by lazy {
        FragmentSupportUsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        nav.visibility = View.GONE
        binding.linearLayout.setOnClickListener {
            val clipboardManager =
                requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", binding.tvNum.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(binding.root.context, "text copied", Toast.LENGTH_SHORT).show()
        }
        binding.button.setOnClickListener {
            val recipient = "medo.samy.897@outlook.com"
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            }
            startActivity(intent)

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        if (activity is HomeActivity) {
            val nav = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            nav.visibility = View.VISIBLE
        }
    }
}