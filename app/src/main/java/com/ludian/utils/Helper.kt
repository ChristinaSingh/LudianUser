package com.ludian.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.ludian.R
import java.io.ByteArrayOutputStream
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Helper {
    companion object {

        private var mDialog: Dialog? = null
        private var isProgressDialogRunning = false

        fun isValidEmail(email: String): Boolean {
            return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun hideKeyboard(view: View){
            try {
                val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }catch (e: Exception){

            }
        }


        fun showProgressMessage(dialogActivity: Activity?, msg: String?) {
            try {
                if (isProgressDialogRunning) {
                    hideProgressMessage()
                }
                isProgressDialogRunning = true
                mDialog = Dialog(dialogActivity!!)
                mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                mDialog!!.setContentView(R.layout.dialog_loading)
                mDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val lp = mDialog!!.window!!.attributes
                lp.dimAmount = 0.0f
                mDialog!!.window!!.attributes = lp
                mDialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
               // mDialog!!.findViewById<TextView>(R.id.loading_text).text = msg
                mDialog!!.setCancelable(true)
                mDialog!!.setCanceledOnTouchOutside(true)
                mDialog!!.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun hideProgressMessage() {
            isProgressDialogRunning = true
            try {
                if (mDialog != null) {
                    mDialog!!.dismiss()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun showSnackbar(view: View, message: String, isError: Boolean) {
            val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG)

            // Customize Snackbar background color
            snackbar.setBackgroundTint(if (isError) android.graphics.Color.RED else android.graphics.Color.GREEN)

            // Customize Snackbar text color
            snackbar.setTextColor(android.graphics.Color.WHITE)

            snackbar.show()
        }



        fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(
                inContext.contentResolver,
                inImage,
                "Title" + System.currentTimeMillis(),
                null
            )
            return Uri.parse(path)
        }

        fun fromBase64(message: String): String? {
            val data = Base64.decode(message, Base64.DEFAULT)
            return try {
                String(data, Charsets.UTF_8)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }
        }


        fun getCurrent(): String {
            val sdf = SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault())
            return sdf.format(Date())
        }

        fun toBase64(message: String): String? {
            return try {
                val data = message.toByteArray(Charsets.UTF_8)
                Base64.encodeToString(data, Base64.DEFAULT)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                null
            }
        }


        fun decodeUnicodeToString(unicodeStr: String): String {
            // Regular expression to match Unicode escape sequences (e.g., \uXXXX)
            return unicodeStr.replace("\\\\u([0-9a-fA-F]{4})".toRegex()) { matchResult ->
                // Convert the matched Unicode escape sequence into the corresponding character
                val hexValue = matchResult.groupValues[1]
                val charValue = hexValue.toInt(16).toChar()
                charValue.toString()
            }
        }


        fun formatDates(dateString: String): List<String> {
            // Define the output date format
            val outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

            // Split the input string by commas
            val dateList = dateString.split(",")

            // Format each date and store them in a list
            return dateList.map { dateString ->
                // Parse each date string into LocalDate and format it
                val date = LocalDate.parse(dateString)
                date.format(outputFormatter)
            }
        }


    }





}