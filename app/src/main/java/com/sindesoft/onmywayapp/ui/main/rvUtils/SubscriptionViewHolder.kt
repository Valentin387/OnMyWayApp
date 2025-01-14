package com.sindesoft.onmywayapp.ui.main.rvUtils


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sindesoft.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.sindesoft.onmywayapp.databinding.ItemSubscriptionBinding

class SubscriptionViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemSubscriptionBinding.bind(view)

    fun render(
        subscriptionModel: SubscriptionFetchResponse,
        onClickListener: (SubscriptionFetchResponse) -> Unit,
        onClickDeleted: (Int) -> Unit
    ) {

        binding.tvAssignedCode.text = subscriptionModel.assignedCode
        binding.tvFullName.text = "${subscriptionModel.givenName} ${subscriptionModel.familyName}"

        itemView.setOnClickListener { onClickListener(subscriptionModel) }
        binding.btDelete.setOnClickListener { onClickDeleted(adapterPosition) }
    }
}