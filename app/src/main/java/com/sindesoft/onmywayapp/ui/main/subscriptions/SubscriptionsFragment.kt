package com.sindesoft.onmywayapp.ui.main.subscriptions

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.sindesoft.onmywayapp.data.models.User
import com.sindesoft.onmywayapp.data.repositories.SubscriptionRepository
import com.sindesoft.onmywayapp.databinding.FragmentSubscriptionsBinding
import com.sindesoft.onmywayapp.io.SubscriptionService
import com.sindesoft.onmywayapp.ui.main.rvUtils.SubscriptionAdapter
import com.sindesoft.onmywayapp.ui.main.rvUtils.SubscriptionViewHolder
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager

class SubscriptionsFragment : Fragment() {

    private var _binding: FragmentSubscriptionsBinding? = null
    private lateinit var llmanager : LinearLayoutManager
    private lateinit var adapter : SubscriptionAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val subscriptionService: SubscriptionService by lazy {
        SubscriptionService.create(requireContext())
    }

    private val subscriptionViewModel by activityViewModels<SubscriptionsViewModel>
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SubscriptionsViewModel(SubscriptionRepository(subscriptionService)) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscriptionsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SubscriptionsFragment", "onViewCreated")

        //Fetch the subscriptions of the user
        val userId = fetchUserMongoIDFromPreferences()
        subscriptionViewModel.fetchMySubscriptions(userId)

        initRecyclerView()
    }

    private fun initRecyclerView(){
        llmanager = LinearLayoutManager(context)

        val subscriptionList = subscriptionViewModel.subscriptionList.value ?: emptyList()

        adapter = SubscriptionAdapter(subscriptionList) { subscription ->
            onItemSelected(
                subscription
            )
        }

        val decoration = DividerItemDecoration(context, llmanager.orientation)

        val recyclerView = binding.rvSubscriptions
        recyclerView.layoutManager = llmanager
        recyclerView.adapter = adapter

        subscriptionViewModel.subscriptionList.observe(viewLifecycleOwner) {
            adapter.updateList(it)
        }

        binding.rvSubscriptions.addItemDecoration(decoration)

    }

    fun onItemSelected(subscription: SubscriptionFetchResponse) {
        //Handle item selection
        Toast.makeText(context, "Selected: ${subscription.givenName}", Toast.LENGTH_SHORT).show()

    }

    private fun fetchUserMongoIDFromPreferences() : String {
        //check is there is a token stored
        //val preferences = requireActivity().getSharedPreferences("defaultPrefs", MODE_PRIVATE)
        val preferences = EncryptedPrefsManager.getPreferences()
        val userString = preferences.getString("user", null)
        val gson = Gson()
        val user = gson.fromJson(userString, User::class.java)
        return user.id ?: ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}