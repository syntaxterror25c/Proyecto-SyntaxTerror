package com.example.aplicacion.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

        // Botón Email
        binding.btnEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = "mailto:info@sintaxterror-gym.com".toUri()
            intent.putExtra(Intent.EXTRA_SUBJECT, "Consulta sobre mis sesiones")
            startActivity(intent)
        }

        // Botón Teléfono
        binding.btnCall.setOnClickListener {
            val numero = "666666666"
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = "tel:$numero".toUri()
            startActivity(intent)
        }

        // Botón WhatsApp
        binding.btnWhatsapp.setOnClickListener {
            val numero = "34666245315" // Importante: Código de país (34 para España) + número sin espacios
            val mensaje = "Hola, quería consultar sobre mis sesiones en PixelGym"
            val url = "https://api.whatsapp.com/send?phone=$numero&text=${Uri.encode(mensaje)}"

            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = url.toUri()
                startActivity(intent)
            } catch (e: Exception) {
                // Por si no tienen WhatsApp instalado, enviamos a la web o avisamos
                Toast.makeText(requireContext(), "WhatsApp no está instalado", Toast.LENGTH_SHORT).show()
            }
        }
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