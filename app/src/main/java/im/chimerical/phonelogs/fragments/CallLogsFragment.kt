package im.chimerical.phonelogs.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import im.chimerical.phonelogs.adapters.CallLogsListAdapter
import android.provider.CallLog.Calls
import android.widget.Toast
import im.chimerical.phonelogs.CallLog
import im.chimerical.phonelogs.databinding.CallLogsFragmentBinding
import im.chimerical.phonelogs.interfaces.OnSelected
import im.chimerical.phonelogs.toToast
import java.text.SimpleDateFormat
import java.util.*
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.collections.ArrayList

class CallLogsFragment : Fragment() {
    private lateinit var binding: CallLogsFragmentBinding
    private lateinit var callLogsAdapter: CallLogsListAdapter //RecyclerView.Adapter<*>

    private var logsList: ArrayList<CallLog> = arrayListOf()
    private lateinit var mContext: Context
    // Request code for READ_CONTACTS. It can be any number > 0.

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CallLogsFragmentBinding.inflate(layoutInflater)
        callLogsAdapter = CallLogsListAdapter(logsList, mContext, object : OnSelected {
            override fun onClick(list: CallLog) {
                when{
                    list.name.isNullOrEmpty()-> list.name?.toToast(requireContext())
                    list.number.isNotEmpty() -> list.number.toToast(requireContext())
                    list.number.isNotEmpty() && list.name.isNullOrEmpty()->
                        "${list.number} : ${list.name}".toToast(requireContext())
                }
            }
        })
        binding.callLogsRecyclerView.apply {
            layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            adapter = callLogsAdapter
        }

        readCallLogs()

        return binding.root
    }


    @SuppressLint("SimpleDateFormat", "Range")
    private fun readCallLogs() {
        Log.i("readContacts", "Reading Contacts")
        val contentResolver = mContext.contentResolver
        val callLogsUri: Uri = Uri.parse("content://call_log/calls")
        val callLogsCursor: Cursor? =
            contentResolver.query(callLogsUri, null, null, null, Calls.DEFAULT_SORT_ORDER)

        if (callLogsCursor!!.moveToFirst()) {
            do {
                val name: String? =
                    callLogsCursor.getString(callLogsCursor.getColumnIndex(Calls.CACHED_NAME))
                val photoUri: String? =
                    callLogsCursor.getString(callLogsCursor.getColumnIndex(Calls.CACHED_PHOTO_URI))
                var photo: Bitmap? = null
                val number: String =
                    callLogsCursor.getString(callLogsCursor.getColumnIndex(Calls.NUMBER))
                val date: Long = callLogsCursor.getLong(callLogsCursor.getColumnIndex(Calls.DATE))
                val callType: Int =
                    Integer.parseInt(callLogsCursor.getString(callLogsCursor.getColumnIndex(Calls.TYPE)))

                Log.i("photoURI", "photoUri: $photoUri")

                val formatter = SimpleDateFormat("dd/mm/yyyy, hh:mm a")
                val dateString: String = formatter.format(Date(date))

                if (photoUri != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Files.exists(
                        Paths.get(photoUri)
                    )
                ) {
                    photo = BitmapFactory.decodeFile(photoUri)
                }
                Log.i("photo BITMAP", "photo : $photo")
                Log.i("photo BITMAP", "photo : $callLogsUri")

                logsList.add(
                    CallLog(
                        name,
                        number,
                        dateString.split(",")[0],
                        dateString.split(",")[1],
                        callType,
                        photo
                    )
                )
            } while (callLogsCursor.moveToNext())
        }
        callLogsCursor.close()
    }
}