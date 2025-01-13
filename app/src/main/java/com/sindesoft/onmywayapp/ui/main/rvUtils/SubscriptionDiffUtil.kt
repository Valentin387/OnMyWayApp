package com.sindesoft.onmywayapp.ui.main.rvUtils

import androidx.recyclerview.widget.DiffUtil
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.sindesoft.onmywayapp.data.models.User

class SubscriptionDiffUtil (
    private val oldList: List<SubscriptionFetchResponse>,
    private val newList: List<SubscriptionFetchResponse>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].subscriptionId == newList[newItemPosition].subscriptionId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].assignedCode == newList[newItemPosition].assignedCode
    }
}