package com.ludian.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ludian.api.UserDataApi
import com.ludian.models.BookTestModel
import com.ludian.repository.UserBookingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(private val apiService: UserDataApi) : ViewModel() {
    val getAllBooking: Flow<PagingData<BookTestModel>> = Pager(config = PagingConfig(20,enablePlaceholders = false)){
        UserBookingRepository(apiService)
    }.flow.cachedIn(viewModelScope)
}