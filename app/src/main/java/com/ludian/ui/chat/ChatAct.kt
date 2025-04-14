package com.ludian.ui.chat


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.*
import com.google.gson.Gson
import com.ludian.R
import com.ludian.databinding.ActivityChatBinding
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.SharedPrf
import com.ludian.viewmodels.UserDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class ChatAct : AppCompatActivity() {
    private val TAG = "ChatAct"
    private lateinit var binding: ActivityChatBinding
    private lateinit var reference1: DatabaseReference
    private var requestId = ""
    private var userId = ""
    private var userName: String? = null
    private var userImage: String? = null
    private var receiverId = ""
    private var receiverName: String? = null
    private var receiverImage: String? = null
    private var bookingID: String? = null
    private var chatArrayList: ArrayList<ChatMsgModel.Result>? = null

    private lateinit var userDataViewModel: UserDataViewModel
    private val sharedPrf by lazy { SharedPrf(this) }
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initializeViews()
        intent?.let {
            userName = it.getStringExtra("name")
            userImage = it.getStringExtra("img")
            userId = it.getStringExtra("id") ?: ""
         //   setUserInfo(it.getStringExtra("UserId") ?: "")
            receiverId = it.getStringExtra("UserId") ?: ""
            receiverName = it.getStringExtra("UserName")
            receiverImage = it.getStringExtra("UserImage")
            bookingID = it.getStringExtra("bookingID")
            allChatNsg(bookingID!!,receiverId,userId)
        }
    }

    private fun initializeViews() {
        chatArrayList = ArrayList()
        binding.backNavigation.setOnClickListener { finish() }
        binding.ChatLayout.imgSendIcon.setOnClickListener {
            if (binding.ChatLayout.tvMessage.text.isNotEmpty()) {
                val messageText = binding.ChatLayout.tvMessage.text.toString().trim()
                val phoneNumberRegex = "^(\\+?[0-9]{1,4}[-.\\s]?)?\\(?\\d{1,4}\\)?[-.\\s]?\\d{1,4}[-.\\s]?\\d{1,4}$".toRegex()

                if (messageText.isNotEmpty()) {
                    if (phoneNumberRegex.containsMatchIn(messageText)) {
                        Toast.makeText(
                            this@ChatAct,
                            getString(R.string.message_contain_phone_number),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {

                        val map = HashMap<String, String>()
                        map["message"] = Helper.toBase64(messageText)!!
                        map["user"] = userName ?: ""
                        map["date"] = Helper.getCurrent()
                        map["msg_type"] = "1"
                        map["sender_id"] = userId
                        Log.e("send msg===", map.toString())
                        // reference1.push().setValue(map)


                        val sendPushNotificationsRequest = sendPushNotificationsRequest(
                            receiverId,
                            userId,
                            messageText,
                            Helper.getCurrent(),
                            bookingID!!
                        )
                        Log.e("send message Request===", sendPushNotificationsRequest.toString())
                        Helper.showProgressMessage(this@ChatAct, getString(R.string.please_wait))
                        userDataViewModel.sendNotificationChat(sendPushNotificationsRequest)
                    }

                }
                binding.ChatLayout.tvMessage.setText("")
            } else {
                binding.ChatLayout.tvMessage.error = "Field is Blank"
            }
        }

        disableSendButton()

        binding.ChatLayout.tvMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    binding.ChatLayout.imgSendIcon.isEnabled = true
                    binding.ChatLayout.imgSendIcon.alpha = 1.0f
                } else {
                    disableSendButton()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        bindObservers()

    }



    private fun allChatNsg(bookingID:String,receiverID: String,senderID: String){
        val allChatMsg = getAllChatMsg(bookingID,receiverID,senderID)
        Log.e("get All Chat message Request===", allChatMsg.toString())
        Helper.showProgressMessage(this@ChatAct,getString(R.string.please_wait))
        userDataViewModel.allChatMsg(allChatMsg)
    }


    private fun bindObservers() {
        userDataViewModel.chatLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("send message Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                allChatNsg(bookingID!!,receiverId,userId)
                                userDataViewModel.clearChatData()
                            } else {

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
                    Helper.showProgressMessage(
                        this@ChatAct,
                        getString(R.string.please_wait)
                    )
                }

                else -> {}
            }
        })


        userDataViewModel.allChatMsgLiveData.observe(this, Observer { it ->
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    try {
                        it.data?.let {
                            val jsonObject = JSONObject(it.string())
                            Log.e("get All Chat Msg message Response===", jsonObject.toString())
                            if (jsonObject.getString("status") == "1") {
                                val chatMsgModel: ChatMsgModel = Gson().fromJson(
                                    jsonObject.toString(),
                                    ChatMsgModel::class.java
                                )
                                chatArrayList!!.clear()
                                chatArrayList!!.addAll(chatMsgModel.result)
                                binding.layout1.removeAllViews()

                                for (i in 0 until chatArrayList!!.size) {
                                    val senderId = chatArrayList!![i].sender_id
                                    val message = chatArrayList!![i].chat_message
                                    val time = chatArrayList!![i].current_date_time
                                    if (senderId == this@ChatAct.userId) {
                                        addMessageBox(message, 1, time, "", "")
                                        Log.e("left====","=====")
                                    } else {
                                        addMessageBox(message, 2, time, "", "")
                                        Log.e("right====","=====")

                                    }
                                }



                                userDataViewModel.clearAllChatMsgData()
                            } else {

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
                    Helper.showProgressMessage(
                        this@ChatAct,
                        getString(R.string.please_wait)
                    )
                }

                else -> {}
            }
        })


    }


    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@ChatAct, text, Toast.LENGTH_SHORT).show()

    }





    private fun sendPushNotificationsRequest(receiverID: String, senderID: String, message: String,dateTime:String,bookingID:String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "receiver_id" to receiverID ,
                "sender_id" to senderID,
                "chat_message" to message,
                "current_date_time" to dateTime,
                "booking_id" to bookingID

            )
        }
    }

    private fun getAllChatMsg(bookingID:String,receiverID: String,senderID: String): Map<String, String> {
        return binding.run {
            mapOf(
                "token" to sharedPrf.getStoredTag(SharedPrf.TOKEN),
                "booking_id" to bookingID,
                "receiver_id" to receiverID ,
                "sender_id" to senderID,
            )
        }
    }





    private fun disableSendButton() {
        binding.ChatLayout.imgSendIcon.isEnabled = false
        binding.ChatLayout.imgSendIcon.alpha = 0.3f
    }

    private fun setUserInfo(pid: String) {
        val selfUser = userId
        val otherUser = pid
        reference1 = if (selfUser.toDouble() > otherUser.toDouble()) {
            FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://decoded-reducer-294611.firebaseio.com/messages-$selfUser-$otherUser")
        } else {
            FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://decoded-reducer-294611.firebaseio.com/messages-$otherUser-$selfUser")
        }
        reference1.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val genericTypeIndicator = object : GenericTypeIndicator<Map<String, String>>() {}
                val map = dataSnapshot.getValue(genericTypeIndicator)
                val message = map?.get("message") ?: ""
                Log.e("ttttt", map.toString())

                map?.let {
                    val senderId = it["sender_id"] ?: ""
                    val userName = it["user"] ?: ""
                    val time = it["date"] ?: ""
                    val msgType = it["msg_type"] ?: ""
                    if (senderId == this@ChatAct.userId) {
                        addMessageBox(message, 1, time, msgType, "")
                    } else {
                        addMessageBox(message, 2, time, msgType, "")
                    }
                }
                Log.e("mSgChat", "onChildAdded: $message")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun addMessageBox(message: String, type: Int, date: String, msgType: String, url: String) {
        val tvTime: TextView
        val tvMessage: TextView
        val relativeImgMessage: RelativeLayout
        val imgMessage: ImageView
        val view: View
        if (type == 1) {
            view = LayoutInflater.from(applicationContext).inflate(R.layout.layout_chat_white_bg, null)
            tvTime = view.findViewById(R.id.tv_time)
            tvMessage = view.findViewById(R.id.tv_message)
            relativeImgMessage = view.findViewById(R.id.relative_img_message)
            imgMessage = view.findViewById(R.id.ivImg)
            val tvName: TextView = view.findViewById(R.id.tvName)

            tvName.text = userName
            tvTime.text = date
            Glide.with(applicationContext)
                .load(userImage)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default))
                .into(imgMessage)
          //  tvMessage.text = Helper.fromBase64(message)
            tvMessage.text = message


        } else {
            view = LayoutInflater.from(applicationContext).inflate(R.layout.layout_chat_left_bg, null)
            tvTime = view.findViewById(R.id.tv_time1)
            tvMessage = view.findViewById(R.id.tv_message1)
            relativeImgMessage = view.findViewById(R.id.relative_img_message1)
            imgMessage = view.findViewById(R.id.ivImg1)
            tvTime.text = date
            val tvName: TextView = view.findViewById(R.id.tvName1)

            tvName.text = receiverName
            Glide.with(applicationContext)
                .load(receiverImage)
                .apply(RequestOptions.placeholderOf(R.drawable.user_default))
                .into(imgMessage)
          //  tvMessage.text = Helper.fromBase64(message)
            tvMessage.text = message

        }

        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 10)
        view.layoutParams = lp

        binding.layout1.addView(view)

        binding.scrollView.post { binding.scrollView.fullScroll(View.FOCUS_DOWN) }
    }
}
