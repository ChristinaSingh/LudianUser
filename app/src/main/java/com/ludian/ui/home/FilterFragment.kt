package com.ludian.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ludian.databinding.FragmentFilterBinding
import com.ludian.ui.calender.CalendarAct

class FilterFragment : Fragment() {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {


        binding.btnApply.setOnClickListener {
            requireActivity().finish()
        }



        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }


        binding.btnDate.setOnClickListener {
            startActivity(Intent(requireActivity(), CalendarAct::class.java))
        }

        binding.ivDate.setOnClickListener {
            startActivity(Intent(requireActivity(), CalendarAct::class.java))

        }


    }

}