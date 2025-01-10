package com.sindesoft.onmywayapp.ui.main.subscriptions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sindesoft.onmywayapp.R
import com.sindesoft.onmywayapp.data.models.User

class SubscriptionAdapter (private var subscriptionList: List<User>, private val onClickListener:(User) -> Unit) :
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
        holder.render(item, onClickListener)
    }

    fun updateList(newList: List<User>) {
        val diffResult = SubscriptionDiffUtil(subscriptionList, newList)
        val result = DiffUtil.calculateDiff(diffResult)
        subscriptionList = newList
        result.dispatchUpdatesTo(this)
    }
}