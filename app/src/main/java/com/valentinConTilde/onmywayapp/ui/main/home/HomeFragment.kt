package com.valentinConTilde.onmywayapp.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.valentinConTilde.onmywayapp.databinding.FragmentHomeBinding
import com.valentinConTilde.onmywayapp.io.WebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webSocketClient: WebSocketClient
    private val messageChannel = Channel<String>()


    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        //textView.text = "This is home Fragment"
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

        // Initialize WebSocketClient
        webSocketClient = WebSocketClient()

        // Connect to WebSocket
        webSocketClient.connectToWebSocket() { message ->
            // Handle incoming message
            // Update UI or handle the message as needed
            //Log.d("WebSocket", "Received message: $message")
        }

        // Handle send button click
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            lifecycleScope.launch(Dispatchers.IO) {
                webSocketClient.sendMessage(message)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        webSocketClient.closeWebSocket()  // Close WebSocket when fragment is destroyed
    }
}