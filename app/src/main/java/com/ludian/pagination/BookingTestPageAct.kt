package com.ludian.pagination

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ludian.databinding.ActivityBookingPageTestBinding
import com.ludian.viewmodels.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@AndroidEntryPoint
class BookingTestPageAct : AppCompatActivity() {
    private lateinit var binding:ActivityBookingPageTestBinding
    private val mainViewModel:BookingViewModel by viewModels()
    @Inject
    lateinit var dogsAdapter: BookAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingPageTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initRecyclerview()
        lifecycleScope.launchWhenStarted {
            mainViewModel.getAllBooking.collectLatest { response->
                binding.apply {
                    progressBar.isVisible=false
                    recyclerview.isVisible=true
                }
                dogsAdapter.submitData(response)
            }
        }
    }

    private fun initRecyclerview() {
        binding.apply {
            recyclerview.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(this@BookingTestPageAct,2)
                adapter = dogsAdapter.withLoadStateHeaderAndFooter(
                    header = LoaderStateAdapter { dogsAdapter :: retry},
                    footer = LoaderStateAdapter{dogsAdapter :: retry}
                )
            }
        }
    }
}

