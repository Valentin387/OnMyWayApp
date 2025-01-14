package com.valentinConTilde.onmywayapp.ui.main.rvUtils

import androidx.recyclerview.widget.DiffUtil
import com.valentinConTilde.onmywayapp.data.DTO.SubscriptionFetchResponse

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