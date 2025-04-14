package com.ludian.ui.booking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.ludian.R
import com.ludian.models.PromoCodeModel
import com.ludian.ui.booking.adapter.PromoCodeAdapter
import com.ludian.viewmodels.UserDataViewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class PromoCodeBottomSheet : BottomSheetDialogFragment(), PromoCodeAdapter.OnPromoCodeListener {
    private lateinit var bottomSheetView: View
    private var userId: String? = null
    private var propertyId: String? = null

    private lateinit var userDataViewModel: UserDataViewModel
    private lateinit var promoCodeAdapter: PromoCodeAdapter
    private var arrayList: ArrayList<PromoCodeModel.Result>? = null
    private lateinit var listener: SelectCodeListener
    private val sharedPrf by lazy { SharedPrf(requireActivity()) }


    companion object {
        fun newInstance(
            userID: String,
            propertyId:String,
            listener: SelectCodeListener
        ): PromoCodeBottomSheet {
            val fragment = PromoCodeBottomSheet()
            fragment.userId = userID
            fragment.propertyId = propertyId
            fragment.listener = listener

            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bottomSheetView = inflater.inflate(R.layout.bottomsheet_promo_code, container, false)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        return bottomSheetView


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ivBack = bottomSheetView.findViewById<ImageView>(R.id.ivClose)
        val rvPromoCode = bottomSheetView.findViewById<RecyclerView>(R.id.rvPromoCode)

        arrayList = ArrayList()

        promoCodeAdapter =
            PromoCodeAdapter(requireActivity(), arrayList!!, this@PromoCodeBottomSheet)
        rvPromoCode.adapter = promoCodeAdapter

        ivBack.setOnClickListener {
              dialog!!.dismiss()
        }


        Helper.showProgressMessage(requireActivity(),getString(R.string.please_wait))
        userDataViewModel.getPromoCode(sharedPrf.getStoredTag(SharedPrf.TOKEN),propertyId!!)


        bindObserver()

    }

    interface SelectCodeListener {
        fun onSelectedCode(value: PromoCodeModel.Result)
    }


    private fun bindObserver() {
        userDataViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                   try {
                       it.data?.let {
                           val jsonObject = JSONObject(it.string())
                           Log.e("get coupon Code Response===", jsonObject.toString())
                           if (jsonObject.getInt("status") == 1) {
                               val promoCodeModel: PromoCodeModel = Gson().fromJson(
                                   jsonObject.toString(),
                                   PromoCodeModel::class.java
                               )
                               arrayList!!.clear()
                               arrayList!!.addAll(promoCodeModel.result)
                               promoCodeAdapter.notifyAdapter(arrayList!!)

                               userDataViewModel.clearData()
                           } else {
                               arrayList!!.clear()
                               promoCodeAdapter.notifyAdapter(arrayList!!)


                               Toast.makeText(
                                   requireActivity(),
                                   "" + jsonObject.getString("message"),
                                   Toast.LENGTH_SHORT
                               ).show()
                           }
                           //  Log.e("TAG", "observers: $it.")
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


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(requireActivity(),text, Toast.LENGTH_SHORT).show()
    }


    override fun onPromoCode(propertyList: ArrayList<PromoCodeModel.Result>, position: Int) {
        listener.onSelectedCode(propertyList[position])
        dialog!!.dismiss()
    }

}