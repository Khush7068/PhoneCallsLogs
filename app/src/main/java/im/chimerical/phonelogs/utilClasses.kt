package im.chimerical.phonelogs

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

class Contact (var name: String, var number: String, var photo: Bitmap?)

class CallLog (var name: String?, var number: String, var date: String, var time: String, val callType: Int, var photo: Bitmap?)

fun String.toToast(requireContext: Context) = Toast.makeText(requireContext,this,Toast.LENGTH_SHORT).show()
fun String.toSnack(requireView: View) = Snackbar.make(requireView,this,Snackbar.LENGTH_SHORT).show()
fun String.toLog(tag: String = "logger") = Log.e(tag,this)