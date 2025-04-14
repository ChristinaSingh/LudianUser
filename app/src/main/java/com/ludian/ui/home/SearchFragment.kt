package com.ludian.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.FragmentSearchBinding
import com.ludian.models.PropertyModel
import com.ludian.ui.bookmark.BookmarkAdapter
import com.ludian.ui.home.adapter.TrendingDestinationAdapter
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class SearchFragment : Fragment(), FilterBottomSheet.FilterSelectListener {

    private var _binding: FragmentSearchBinding? = null
    private lateinit var userDataViewModel: UserDataViewModel

    private val binding get() = _binding!!
    private lateinit var trendingDestinationAdapter: TrendingDestinationAdapter

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var arrayList : ArrayList<PropertyModel.Property>?=null
    private var strName:String=""
    private val sharedPrf by lazy { SharedPrf(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
        return binding.root
    }

    private fun initViews() {

        arrayList = ArrayList()

        bookmarkAdapter = BookmarkAdapter(requireActivity(),arrayList,"search")
        binding.rvSearch.adapter = bookmarkAdapter

        binding.ivFilter.setOnClickListener {
            val filterBottomSheet = FilterBottomSheet
                .newInstance( this@SearchFragment)
            filterBottomSheet.show(childFragmentManager, "")

        }

        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && s.length == 2) {
                    strName = s.toString()
                    // Debounce the API request
                    Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
                    userDataViewModel.searchProperty(sharedPrf.getStoredTag(SharedPrf.TOKEN), s.toString(),"","","","")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        trendingDestinationAdapter = TrendingDestinationAdapter(requireActivity())
        binding.rvTrendingDestination.adapter = trendingDestinationAdapter

        bindObservers()

    }

    private fun bindObservers() {
        userDataViewModel.searchLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                   try {
                       it.data?.let {
                           val jsonObject = JSONObject(it.string())
                           Log.e("search property Response===", jsonObject.toString())
                           if (jsonObject.getInt("status") == 1) {
                               val bookmarkModel: PropertyModel = Gson().fromJson(
                                   jsonObject.toString(),
                                   PropertyModel::class.java
                               )
                               binding.tvNotFound.visibility =View.GONE

                               arrayList!!.clear()
                               arrayList!!.addAll(bookmarkModel.properties)
                               bookmarkAdapter.notifyAdapter(arrayList!!)

                               userDataViewModel.clearSearchData()
                           } else {
                               arrayList!!.clear()
                               bookmarkAdapter.notifyAdapter(arrayList!!)
                               binding.tvNotFound.visibility =View.VISIBLE
                           }
                       }
                   }catch (e:Exception){
                       e.printStackTrace()
                   }
                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
                }

                else -> {}
            }
        })
    }

    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(requireActivity(),text, Toast.LENGTH_SHORT).show()
    }

    override fun onFilter(rating: String, lat: String,lon:String, priceRange: String) {
        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.searchProperty(sharedPrf.getStoredTag(SharedPrf.TOKEN), strName,rating, lat,lon,priceRange)
    }

}