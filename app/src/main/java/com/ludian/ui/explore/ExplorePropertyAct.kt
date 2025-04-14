package com.ludian.ui.explore

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityExplorePropertyBinding
import com.ludian.models.PropertyModel
import com.ludian.ui.bookmark.BookmarkAdapter
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.ExploreViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ExplorePropertyAct : AppCompatActivity() {
    private lateinit var binding: ActivityExplorePropertyBinding
    private lateinit var exploreViewModel: ExploreViewModel


    private lateinit var bookmarkAdapter: BookmarkAdapter
    private var arrayList: ArrayList<PropertyModel.Property>? = null
    private var categoryId:String?=null
    private val sharedPrf by lazy { SharedPrf(this@ExplorePropertyAct) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_property)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_explore_property)
        exploreViewModel = ViewModelProvider(this).get(ExploreViewModel::class.java)
        initViews()
    }


    private fun initViews() {

        categoryId = intent.getStringExtra("categoryId")

        arrayList = ArrayList()

        bookmarkAdapter = BookmarkAdapter(this, arrayList,"explore")
        binding.rvBookmark.adapter = bookmarkAdapter

        binding.ivBack.setOnClickListener {
            finish()
        }



        bindObservers()




        val explorePropertyRequest = explorePropertyRequest(categoryId!!)
        Log.e("explore property List Request===", explorePropertyRequest.toString())
        Helper.showProgressMessage(this@ExplorePropertyAct, getString(R.string.please_wait))
        exploreViewModel.exploreProperty(explorePropertyRequest)


    }





    private fun explorePropertyRequest(categoryId: String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "property_category_id" to categoryId
                )

        }
    }


    private fun bindObservers() {
        exploreViewModel.explorePropertyLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("explore property list Response===", jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {
                                val bookmarkModel: PropertyModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    PropertyModel::class.java
                                )
                                binding.tvNotFound.visibility = View.GONE
                                arrayList!!.clear()
                                arrayList!!.addAll(bookmarkModel.properties)
                                bookmarkAdapter.notifyAdapter(arrayList!!)

                                exploreViewModel.clearExplorePropertyData()
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
                    Helper.showProgressMessage(this@ExplorePropertyAct, getString(R.string.please_wait))
                }

                else -> {}
            }
        })
    }


    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@ExplorePropertyAct, text, Toast.LENGTH_SHORT).show()
    }

}