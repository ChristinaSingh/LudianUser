package com.ludian.ui.bookmark

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.FragmentBookmarkBinding
import com.ludian.models.BookingModel
import com.ludian.models.BookmarkModel
import com.ludian.models.PropertyModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private lateinit var userDataViewModel: UserDataViewModel

    private val binding get() = _binding!!
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var arrayList: ArrayList<PropertyModel.Property>? = null

    private val sharedPrf by lazy { SharedPrf(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
        return binding.root
    }

    private fun initViews() {
        arrayList = ArrayList()

        bookmarkAdapter = BookmarkAdapter(requireActivity(), arrayList,"bookmark")
        binding.rvBookmark.adapter = bookmarkAdapter

        bindObservers()

        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.getBookmark(sharedPrf.getStoredTag(SharedPrf.TOKEN))


    }


    private fun bindObservers() {
        userDataViewModel.bookmarkLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                  try {
                      it.data?.let {
                          val jsonObject = JSONObject(it.string())
                          Log.e("get bookmark property Response===", jsonObject.toString())
                          if (jsonObject.getInt("status") == 1) {
                              val bookmarkModel: PropertyModel = Gson().fromJson(
                                  jsonObject.toString(),
                                  PropertyModel::class.java
                              )
                              binding.tvNotFound.visibility = View.GONE
                              arrayList!!.clear()
                              arrayList!!.addAll(bookmarkModel.properties)
                              bookmarkAdapter.notifyAdapter(arrayList!!)

                              userDataViewModel.clearBookmarkData()
                          } else {
                              binding.tvNotFound.visibility = View.VISIBLE
                              arrayList!!.clear()
                              bookmarkAdapter.notifyAdapter(arrayList!!)
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
        Toast.makeText(requireActivity(), text, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}