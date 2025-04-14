package com.ludian.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ludian.R
import com.ludian.databinding.DialogFilterBinding
import com.ludian.databinding.FragmentHomeBinding
import com.ludian.ui.calender.CalendarAct

class FilterDialogFragment : DialogFragment() {
    private var _binding: DialogFilterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogFilterBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Any additional setup for your dialog

        binding.ivBack.setOnClickListener {
            dismiss()
        }

        binding.btnApply.setOnClickListener {
            dismiss()
        }

        binding.btnDate.setOnClickListener {
            startActivity(Intent(requireActivity(),CalendarAct::class.java))
        }

        binding.ivDate.setOnClickListener {
            startActivity(Intent(requireActivity(),CalendarAct::class.java))

        }

    }
}