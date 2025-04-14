package com.ludian.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ludian.databinding.FragmentSearchDeatilBinding
import com.ludian.ui.home.adapter.SearchListAdapter

class SearchDetailFragment : Fragment() {

    private var _binding: FragmentSearchDeatilBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchListAdapter: SearchListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDeatilBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {

        searchListAdapter = SearchListAdapter(requireActivity())
        binding.rvSearch.adapter = searchListAdapter


    }
}