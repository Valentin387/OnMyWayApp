package com.valentinConTilde.onmywayapp.ui.main.rvUtils


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.valentinConTilde.onmywayapp.data.DTO.SubscriptionFetchResponse
import com.valentinConTilde.onmywayapp.databinding.ItemSubscriptionBinding

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