package com.sindesoft.onmywayapp.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.sindesoft.onmywayapp.BuildConfig
import com.sindesoft.onmywayapp.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var webSocket: WebSocket
    private val client = OkHttpClient()
    private val url : String = BuildConfig.BASE_URL + "socket/infinitePing"


    //private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        textView.text = "This is home Fragment"
/*        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageTextView = binding.messageTextView
        val messageInput = binding.messageInput
        val sendButton = binding.sendButton

        // Establish WebSocket connection
        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                activity?.runOnUiThread {
                    messageTextView.text = "Connected to server"
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Received message: $text")
                activity?.runOnUiThread {
                    messageTextView.append("\nServer: $text")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                Log.d("WebSocket", "Received bytes: ${bytes.hex()}")
                activity?.runOnUiThread {
                    messageTextView.append("\nServer: ${bytes.utf8()}")
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing: $code $reason")
                webSocket.close(1000, null)
                activity?.runOnUiThread {
                    messageTextView.text = "Connection closing: $reason"
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error: ${t.message}")
                activity?.runOnUiThread {
                    messageTextView.text = "Connection failed: ${t.message}"
                }
            }
        })

        // Handle send button click
        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                webSocket.send(message)
                messageInput.text.clear()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Close WebSocket when fragment is destroyed
        webSocket.close(1000, "Fragment destroyed")
    }
}