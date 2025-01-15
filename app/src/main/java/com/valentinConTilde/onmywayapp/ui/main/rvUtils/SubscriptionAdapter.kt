package com.valentinConTilde.onmywayapp.ui.main.rvUtils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.valentinConTilde.onmywayapp.R
import com.valentinConTilde.onmywayapp.data.DTO.SubscriptionFetchResponse

class SubscriptionAdapter (
    private var subscriptionList: List<SubscriptionFetchResponse>,
    private val onClickListener:(SubscriptionFetchResponse) -> Unit,
    private val onClickDeleted:(Int) -> Unit
) :
    RecyclerView.Adapter<SubscriptionViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SubscriptionViewHolder(
            layoutInflater.inflate(
                R.layout.item_subscription,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val item = subscriptionList[position]
        holder.render(item, onClickListener, onClickDeleted)
    }

    fun updateList(newList: List<SubscriptionFetchResponse>) {
        val diffResult = SubscriptionDiffUtil(subscriptionList, newList)
        val result = DiffUtil.calculateDiff(diffResult)
        subscriptionList = newList
        result.dispatchUpdatesTo(this)
    }
}