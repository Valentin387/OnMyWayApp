package com.sindesoft.onmywayapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sindesoft.onmywayapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageTextView = binding.messageTextView
        val messageInput = binding.messageInput
        val sendButton = binding.sendButton

        // Observe WebSocket messages
        lifecycleScope.launch {
            homeViewModel.messages.collect { messages ->
                messageTextView.text = messages.joinToString("\n")
            }
        }

        // Observe WebSocket connection status
        lifecycleScope.launch {
            homeViewModel.connectionStatus.collect { connected ->
                val statusText = if (connected) "Connected" else "Disconnected"
                messageTextView.append("\n$statusText")
            }
        }

        // Send message on button click
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            homeViewModel.sendMessage(message)
            messageInput.text.clear()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}