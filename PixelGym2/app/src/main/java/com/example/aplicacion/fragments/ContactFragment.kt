package com.example.aplicacion.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentContactBinding
import androidx.core.net.toUri


class ContactFragment : Fragment(R.layout.fragment_contact) {
    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactBinding.bind(view)

        // Botón Email Actualizado
        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:info@sintaxterror-gym.com".toUri() // Cambio aquí
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre mis sesiones")
            startActivity(intent)
        }

        // ... (resto de botones Call y Whatsapp se quedan igual)

        setupVideo()
    }

    private fun setupVideo() {
        val videoUri = "android.resource://${requireContext().packageName}/${R.raw.pixelgym_video}".toUri()
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setOnPreparedListener { it.isLooping = true; it.start() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}