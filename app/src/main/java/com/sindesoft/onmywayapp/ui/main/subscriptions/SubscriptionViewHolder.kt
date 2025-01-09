package com.sindesoft.onmywayapp.ui.main.subscriptions


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sindesoft.onmywayapp.data.models.User
import com.sindesoft.onmywayapp.databinding.ItemSubscriptionBinding

class SubscriptionViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val binding = ItemSubscriptionBinding.bind(view)

    fun render(userModel: User, onClickListener: (User) -> Unit) {

        binding.tvAssignedCode.text = userModel.assignedCode
        binding.tvFullName.text = "${userModel.givenName} ${userModel.familyName}"

        itemView.setOnClickListener { onClickListener(userModel) }
    }
}