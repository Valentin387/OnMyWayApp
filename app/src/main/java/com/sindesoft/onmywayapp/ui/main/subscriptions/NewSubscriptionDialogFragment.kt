package com.sindesoft.onmywayapp.ui.main.subscriptions

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.sindesoft.onmywayapp.R
import com.sindesoft.onmywayapp.databinding.NewSubscriptionFormBinding

class NewSubscriptionDialogFragment : DialogFragment() {

    private var _binding: NewSubscriptionFormBinding? = null
    private val binding get() = _binding!!
    private val addSubscriptionViewModel: AddSubscriptionViewModel by activityViewModels()

    // Regex pattern to allow alphanumeric and spaces (adjust as needed)
    private val disallowedPattern = Regex("[;':\"{}\\[\\]]")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewSubscriptionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TextWatcher variable
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update character count display
                val maxLength = 6 // Use the same value as in XML
                val remainingChars = maxLength - (s?.length ?: 0)
                binding.characterCountTextView.text = "$remainingChars"

            }
            override fun afterTextChanged(s: Editable?) {}
        }

        //Add the text watcher to the input filed initially
        binding.customSituationInput.addTextChangedListener(textWatcher)

        // Handle Send Button click
        binding.buttonSend.setOnClickListener{
            // Get the text from the input field
            val code = binding.customSituationInput.text.toString()

            // Input validation
            val trimmedInput = code.trim() // Removes leading and trailing whitespace

            if (
                disallowedPattern.containsMatchIn(trimmedInput) ||
                trimmedInput.isEmpty() ||
                trimmedInput.length != 6
                )
            {
                binding.customSituationInput.error = "Invalid input [;':\"{}\\[\\]]"
            }else{
                addSubscriptionViewModel.reportCode(code)
                dismiss()
            }
        }

        // Handle Cancel Button click
        binding.buttonCancel.setOnClickListener{
            dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}