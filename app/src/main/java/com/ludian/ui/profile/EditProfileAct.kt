package com.ludian.ui.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ludian.R
import com.ludian.databinding.ActivityEditProfileBinding
import com.ludian.viewmodels.UserDataViewModel
import com.ludian.utils.Helper
import com.ludian.utils.NetworkResult
import com.ludian.utils.RealPathUtil
import com.ludian.utils.SharedPrf
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File

@AndroidEntryPoint
class EditProfileAct : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var userDataViewModel: UserDataViewModel

    private var userId : String? = null
    private var image : String? = null
    private val sharedPrf by lazy { SharedPrf(this@EditProfileAct) }
    private  lateinit var bitmapNew : Bitmap
    private var profileImage: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        userDataViewModel = ViewModelProvider(this).get(UserDataViewModel::class.java)

        initViews()
    }


    private fun initViews() {
        if (sharedPrf.getStoredTag(SharedPrf.LANGUAGE) == "ar") {
            binding.edFirstName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edLastName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            binding.edPhoneNumber.textAlignment = View.TEXT_ALIGNMENT_VIEW_START

        } else {
            binding.edFirstName.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edLastName.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edEmail.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
            binding.edPhoneNumber.textAlignment = View.TEXT_ALIGNMENT_VIEW_END

        }



        if(intent!=null){
            userId = intent.getStringExtra("userId")
            image = intent.getStringExtra("image")
            Glide.with(this@EditProfileAct)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.user_default)
                .error(R.drawable.user_default)
                .centerInside()
                .into(binding.ivImage)
            binding.edFirstName.setText(intent.getStringExtra("firstName"))
            binding.edLastName.setText(intent.getStringExtra("lastName"))
            binding.edEmail.setText(intent.getStringExtra("email"))
            binding.edPhoneNumber.setText(intent.getStringExtra("mobile"))

        }
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivImage.setOnClickListener {
            checkPermission()
        }



        binding.btnSave.setOnClickListener {
            Helper.hideKeyboard(it)
            val validationResult = validateUserInput()
            if (validationResult.first) {
                Helper.showProgressMessage(this@EditProfileAct,getString(R.string.please_wait))
                val token =
                    sharedPrf.getStoredTag(SharedPrf.TOKEN).toRequestBody("text/plain".toMediaTypeOrNull())
                val userId =
                    sharedPrf.getStoredTag(SharedPrf.USER_ID).toRequestBody("text/plain".toMediaTypeOrNull())
                val firstName =
                    binding.edFirstName.text.toString()!!.toRequestBody("text/plain".toMediaTypeOrNull())
                val lastName = binding.edLastName.text.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val email = binding.edEmail.text.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val mobile = binding.edPhoneNumber.text.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())


                val profileFilePart: MultipartBody.Part
                val attachmentEmpty: RequestBody
                if (profileImage == null) {
                    attachmentEmpty = "".toRequestBody("profile_picture/*".toMediaTypeOrNull())
                    profileFilePart = MultipartBody.Part.createFormData(
                        "attachment",
                        "profile_picture", attachmentEmpty
                    )
                } else {
                    profileFilePart = MultipartBody.Part.createFormData(
                        "profile_picture",
                        profileImage!!.name, profileImage!!
                            .asRequestBody("profile_picture/*".toMediaTypeOrNull())
                    )

                }

                userDataViewModel.updateProfile(token,userId,firstName,lastName,email,mobile,profileFilePart)

            } else {
                showValidationErrors(validationResult.second)
            }
        }

        bindObservers()
    }


    private fun validateUserInput(): Pair<Boolean, String> {
        val fName = binding.edFirstName.text.toString()
        val lName = binding.edLastName.text.toString()
        val emailAddress = binding.edEmail.text.toString()
        val mobileNumber = binding.edPhoneNumber.text.toString()
        return userDataViewModel.validateProfile(
            this@EditProfileAct,
            fName,
            lName,
            emailAddress,
            mobileNumber,
        )
    }

    @SuppressLint("StringFormatMatches")
    private fun showValidationErrors(error: String) {
        val text = String.format(resources.getString(R.string.txt_error_message, error))
        Toast.makeText(this@EditProfileAct,text, Toast.LENGTH_SHORT).show()
    }



    private fun dialogForImagePick() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this@EditProfileAct)
        builder.setTitle("Select Action")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun choosePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickFromGallery.launch(intent)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(intent)
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Image captured successfully, handle the result here
                val data: Intent? = result.data
                // Extract the image from the intent and do something with it
                // imageArrayList!!.add(data.toString())
                //arrayList!![position].imageList.add(data?.data.toString())
                // rateElementAdapter.notifyAdapter(arrayList!!)
                try {
                    if (data != null) {
                        val extras = data.extras
                        bitmapNew = extras!!["data"] as Bitmap
                        //  val imageBitmap: Bitmap =
                        //     BITMAP_RE_SIZER(bitmapNew, bitmapNew.width, bitmapNew.height)!!
                        val tempUri: Uri = Helper.getImageUri(this@EditProfileAct, bitmapNew)!!
                        val image = RealPathUtil.getRealPath(this@EditProfileAct, tempUri)
                        profileImage = File(image)
                        Log.e("camera image path==", image!!)
                        binding.ivImage.setImageURI(Uri.parse(image))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }


            }
        }

    private val pickFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Image selected from gallery successfully, handle the result here
                val data: Intent? = result.data
                bitmapNew = getBitmapFromUri(Uri.parse(data!!.data.toString()))!!
                val path: String = RealPathUtil.getRealPath(this@EditProfileAct, data!!.data)!!
                //   val imageBitmap: Bitmap =
                //        BITMAP_RE_SIZER(bitmapNew!!, bitmapNew.width, bitmapNew.height)!!
                Log.e("gallery image path==", path!!)

                profileImage = File(path)
                binding.ivImage.setImageURI(data!!.data)


            }
        }


    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888 // Force ARGB_8888
                BitmapFactory.decodeStream(stream, null, options)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                this@EditProfileAct,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this@EditProfileAct,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@EditProfileAct,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                1000
            )
        } else {
            dialogForImagePick()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with using the camera or accessing the gallery
                dialogForImagePick()
            } else {
                Toast.makeText(this@EditProfileAct, R.string.permission_denied, Toast.LENGTH_LONG)
                    .show()
                // Permission denied, handle accordingly (e.g., display a message, disable certain features)
            }
        }
    }



    private fun bindObservers() {
        userDataViewModel.userResponseLiveData.observe(this, Observer {
            Helper.hideProgressMessage()
            when (it) {
                is NetworkResult.Success -> {
                    it.data?.let {
                        val jsonObject = JSONObject(it.string())
                        Log.e("update profile Response===",jsonObject.toString())
                        if (jsonObject.getString("status") == "1") {
                            Toast.makeText(
                                this@EditProfileAct,
                                "Profile update Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()


                        } else {
                            Toast.makeText(
                                this@EditProfileAct,
                                "" + jsonObject.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        //  Log.e("TAG", "observers: $it.")
                    }

                }

                is NetworkResult.Error -> {
                    showValidationErrors(it.message.toString())
                }

                is NetworkResult.Loading -> {
                    Helper.showProgressMessage(this@EditProfileAct,getString(R.string.please_wait))
                }

                else -> {}
            }
        })
    }

}