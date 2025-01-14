package com.sindesoft.onmywayapp.ui.main.subscribers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.sindesoft.onmywayapp.databinding.FragmentSubscribersBinding
import com.sindesoft.onmywayapp.io.SubscriptionService
import com.sindesoft.onmywayapp.ui.main.rvUtils.SubscriptionAdapter
import com.sindesoft.onmywayapp.ui.main.subscriptions.NewSubscriptionDialogFragment
import com.sindesoft.onmywayapp.ui.main.subscriptions.SubscriptionsViewModel
import com.sindesoft.onmywayapp.utils.EncryptedPrefsManager

class SubscribersFragment : Fragment() {

    private var _binding: FragmentSubscribersBinding? = null
    private lateinit var llmanager : LinearLayoutManager
    private lateinit var adapter : SubscriptionAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val subscriptionService: SubscriptionService by lazy {
        SubscriptionService.create(requireContext())
    }

    private val subscribersViewModel by activityViewModels<SubscribersViewModel>
    {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SubscribersViewModel(SubscriptionRepository(subscriptionService)) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubscribersBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SubscribersFragment", "onViewCreated")

        //Fetch the subscriptions of the user
        val userId = fetchUserMongoIDFromPreferences()

        showLoadingSpinner()
        subscribersViewModel.fetchMySubscribers(userId)

        initRecyclerView()
    }

    private fun initRecyclerView(){
        llmanager = LinearLayoutManager(context)

        val subscriptionList = subscribersViewModel.subscriptionList.value ?: emptyList()

        adapter = SubscriptionAdapter(
            subscriptionList = subscriptionList,
            onClickListener = { subscription -> onItemSelected(subscription) },
            onClickDeleted = { position ->
                onDeletedItem(position)
            }
        )

        val decoration = DividerItemDecoration(context, llmanager.orientation)

        val recyclerView = binding.rvSubscribers
        recyclerView.layoutManager = llmanager
        recyclerView.adapter = adapter

        subscribersViewModel.subscriptionList.observe(viewLifecycleOwner) {
            hideLoadingSpinner()
            adapter.updateList(it)
        }

        binding.rvSubscribers.addItemDecoration(decoration)

    }

    private fun onDeletedItem(position: Int) {
        val subscription = subscribersViewModel.subscriptionList.value?.get(position)
        subscription?.let {
            subscribersViewModel.deleteSubscription(it.subscriptionId ?: "")
            Toast.makeText(context, "Deleted: ${subscription.givenName}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun onItemSelected(subscription: SubscriptionFetchResponse) {
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

    private fun showLoadingSpinner() {
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingSpinner() {
        binding.progressBar.visibility = View.GONE
    }
}