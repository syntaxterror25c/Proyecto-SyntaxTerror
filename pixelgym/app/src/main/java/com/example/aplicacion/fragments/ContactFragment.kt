package com.example.aplicacion.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.aplicacion.R
import com.example.aplicacion.databinding.FragmentContactBinding

class ContactFragment : Fragment(R.layout.fragment_contact) {

    private var _binding: FragmentContactBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentContactBinding.bind(view)

        binding.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+34123456789")
            startActivity(intent)
        }

        binding.btnWhatsapp.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=34123456789" // Número en formato internacional
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:info@salas.com")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app")
            startActivity(intent)
        }

        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.videosound}")
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)
        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.requestFocus()
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
            binding.videoView.start()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}