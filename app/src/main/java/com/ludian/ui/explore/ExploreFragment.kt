package com.ludian.ui.explore

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
import com.ludian.ui.bookmark.BookmarkAdapter
import com.ludian.databinding.FragmentExploreBinding
import com.ludian.models.ExploreModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ExploreViewModel
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.awaitAll
import org.json.JSONObject

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var exploreAdapter: ExploreAdapter
    private lateinit var exploreViewModel: ExploreViewModel
    private var arrayList:ArrayList<ExploreModel.Property>?=null
    private val sharedPrf by lazy { SharedPrf(requireActivity()) }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        exploreViewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)

        initViews()
        return binding.root
    }

    private fun initViews() {
        arrayList = ArrayList()

        exploreAdapter = ExploreAdapter(requireActivity(), arrayList)
        binding.rvExplore.adapter = exploreAdapter

        bindObservers()

        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        exploreViewModel.explore(sharedPrf.getStoredTag(SharedPrf.TOKEN))

    }



    private fun bindObservers() {
        exploreViewModel.exploreLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let {
                        val jsonObject = JSONObject(it.string())
                        Log.e("explore category Response===", jsonObject.toString())
                        if (jsonObject.getInt("status") == 1) {
                            val exploreModel: ExploreModel = Gson().fromJson(
                                jsonObject.toString(),
                                ExploreModel::class.java
                            )
                            arrayList!!.clear()
                            arrayList!!.addAll(exploreModel.result)
                            exploreAdapter.notifyAdapter(arrayList!!)
                            binding.tvNotFound.visibility = View.GONE;


                            exploreViewModel.clearExploreData()
                        } else {
                            arrayList!!.clear()
                            exploreAdapter.notifyAdapter(arrayList!!)
                            binding.tvNotFound.visibility = View.VISIBLE;


                        }
                        //  Log.e("TAG", "observers: $it.")
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