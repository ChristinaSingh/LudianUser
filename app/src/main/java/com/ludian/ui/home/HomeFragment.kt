package com.ludian.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.FragmentHomeBinding
import com.ludian.models.PropertyModel
import com.ludian.pagination.PaginationScrollListener
import com.ludian.ui.home.adapter.PopularHomeAdapter
import com.ludian.ui.home.adapter.RecentSearchAdapter
import com.ludian.ui.home.adapter.StoryAdapter
import com.ludian.ui.notification.NotificationAct
import com.ludian.utils.Constants.USER_TOKEN
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class HomeFragment : Fragment(), RecentSearchAdapter.OnBookmarkListener,
    PopularHomeAdapter.OnBookmarkMostListener, StoryAdapter.OnStoryListener {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding
    private lateinit var userDataViewModel: UserDataViewModel
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var popularHomeAdapter: PopularHomeAdapter
    private var recentSearchArrayList: ArrayList<PropertyModel.Property>? = null
    private var popularPropertyArrayList: ArrayList<PropertyModel.Property>? = null
    private var storyArrayList: ArrayList<PropertyModel.Property>? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var bookmarkFrom: String = ""
    private val sharedPrf by lazy { SharedPrf(requireActivity()) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // _binding = FragmentHomeBinding.inflate(inflater, container, false)

        //    if (this::_binding.isInitialized.not()) {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        initViews()
        //    }


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initViews()


    }

    private fun initViews() {
        recentSearchArrayList = ArrayList()
        popularPropertyArrayList = ArrayList()
        storyArrayList = ArrayList()

        binding.searchBar.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_homeFragment_to_searchFragment)

        }

        storyAdapter = StoryAdapter(requireActivity(),storyArrayList,this@HomeFragment)
        binding.rvStory.adapter = storyAdapter

        recentSearchAdapter =
            RecentSearchAdapter(requireActivity(), recentSearchArrayList, this@HomeFragment, "Home")
        binding.rvRecentSearch.adapter = recentSearchAdapter

        popularHomeAdapter = PopularHomeAdapter(
            requireActivity(),
            popularPropertyArrayList,
            this@HomeFragment,
            "Home"
        )
        binding.rvPopularHome.adapter = popularHomeAdapter

        binding.ivNotification.setOnClickListener {
            startActivity(Intent(requireActivity(),NotificationAct::class.java))
        }


        loadMoreData()

        loadMoreData1()

        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.getUserProfile(sharedPrf.getStoredTag(USER_TOKEN))

        bindObservers()

        getCurrentLocation()
    }

    private fun loadMoreData() {
        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.getAllRecentSearch(sharedPrf.getStoredTag(SharedPrf.USER_ID))
    }

    private fun loadMoreData1() {
        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.getAllMostPopularProperty(sharedPrf.getStoredTag(SharedPrf.USER_ID))
    }


    private fun bindObservers() {
        userDataViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {

                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("user profile Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {

                                binding.tvName.text = jsonObject.getJSONObject("data")
                                    .getString("first_name") + " "+ jsonObject.getJSONObject("data")
                                    .getString("last_name")

                                Glide.with(this)
                                    .load(jsonObject.getJSONObject("data").getString("image"))
                                    .error(R.drawable.user_default)
                                    .placeholder(R.drawable.user_default)
                                    .override(50,50)
                                    .into(binding.ivUserImg)

                                userDataViewModel.clearData()
                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "" + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            //  Log.e("TAG", "observers: $it.")
                        }
                    } catch (e: Exception) {
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


        userDataViewModel.storyListLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get nearest property Response===", jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {
                                val propertyModel: PropertyModel = Gson().fromJson(jsonObject.toString(), PropertyModel::class.java)
                                    storyArrayList!!.clear()
                                     storyArrayList!!.addAll(propertyModel.properties)
                                    storyAdapter.notifyAdapter(storyArrayList!!)


                                userDataViewModel.clearStoryPropertyData()
                            } else {
                                storyArrayList!!.clear()
                                storyAdapter.notifyAdapter(storyArrayList!!)

                            }

                        }
                    } catch (e: Exception) {
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

        userDataViewModel.recentSearchLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get added property Response===", jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {
                                val propertyModel: PropertyModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    PropertyModel::class.java
                                )
                                binding.tvRecentSearch.visibility = View.VISIBLE


                                if (propertyModel.properties.isNotEmpty()) {
                                    recentSearchArrayList!!.clear()
                                    recentSearchArrayList!!.addAll(propertyModel.properties)
                                    recentSearchAdapter.notifyAdapter(recentSearchArrayList!!)
                                }


                                userDataViewModel.clearRecentSearchData()

                            } else {
                                recentSearchArrayList!!.clear()
                                recentSearchAdapter.notifyAdapter(recentSearchArrayList!!)
                                binding.tvRecentSearch.visibility = View.GONE


                            }
                        }
                    } catch (e: Exception) {
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

        userDataViewModel.popularPropertyLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get popular property Response===", jsonObject.toString())
                            if (jsonObject.getInt("status") == 1) {
                                val propertyModel: PropertyModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    PropertyModel::class.java
                                )
                                binding.tvPopularHome.visibility = View.VISIBLE
                                if (propertyModel.properties.isNotEmpty()) {
                                    popularPropertyArrayList!!.clear()
                                    popularPropertyArrayList!!.addAll(propertyModel.properties)
                                    popularHomeAdapter.notifyAdapter(popularPropertyArrayList!!)

                                }
                                userDataViewModel.clearMostPopularPropertyData()
                            } else {
                                popularPropertyArrayList!!.clear()
                                popularHomeAdapter.notifyAdapter(popularPropertyArrayList!!)
                                binding.tvPopularHome.visibility = View.GONE

                            }

                        }
                    } catch (e: Exception) {
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

        userDataViewModel.addBookmarkLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("add bookmark Response===", jsonObject.toString())
                            if (jsonObject.getString("status").equals("1")) {
                                if (bookmarkFrom == "bookmark_recent") loadMoreData()
                                else loadMoreData1()

                                userDataViewModel.clearAddBookmarkData()

                            } else {
                                Toast.makeText(
                                    requireActivity(),
                                    "" + jsonObject.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            //  Log.e("TAG", "observers: $it.")
                        }
                    } catch (e: Exception) {
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

    override fun onResume() {
        super.onResume()

    }


    override fun onDestroyView() {
        super.onDestroyView()
        // _binding = null
    }

    override fun onBookmark(model: PropertyModel.Property, position: Int, type: String) {
        bookmarkFrom = type
        val addBookmarkRequest = addBookmarkRequest(model.property_id)
        Log.e("add bookmark Request===", addBookmarkRequest.toString())
        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.addToBookmark(addBookmarkRequest)
    }

    override fun onBookmarkMost(model: PropertyModel.Property, position: Int, type: String) {
        bookmarkFrom = type
        val addBookmarkRequest = addBookmarkRequest(model.property_id)
        Log.e("add bookmark Request===", addBookmarkRequest.toString())
        Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
        userDataViewModel.addToBookmark(addBookmarkRequest)
    }


    private fun addBookmarkRequest(propertyId: String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "property_id" to propertyId,
            )

        }
    }

    override fun onStory(model: PropertyModel.Property, position: Int, type: String) {

    }




    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Permission already granted, proceed with location retrieval
            getCurrentLocation()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location retrieval
                getCurrentLocation()
            } else {
                // Permission denied
                Toast.makeText(requireActivity(), "Location permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // Use the location object
                    val latitude = location.latitude
                    val longitude = location.longitude


                    val storyPropertyRequest = storyPropertyRequest(latitude.toString(),longitude.toString())
                    Log.e("story property Request===", storyPropertyRequest.toString())
                    Helper.showProgressMessage(requireActivity(), getString(R.string.please_wait))
                    userDataViewModel.getNearestProperty(storyPropertyRequest)


                    Toast.makeText(requireActivity(), "Location: $latitude, $longitude", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireActivity(), "Location not available", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Request location permission if not granted
            requestLocationPermission()
        }
    }



    private fun storyPropertyRequest(lat: String,lon:String): Map<String, String> {
        return binding.run {
            mapOf(
                "user_id" to sharedPrf.getStoredTag(SharedPrf.USER_ID),
                "latitude" to lat,
                "longitude" to lon
                )

        }
    }

}