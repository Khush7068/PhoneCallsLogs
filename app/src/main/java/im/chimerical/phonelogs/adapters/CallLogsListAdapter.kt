package im.chimerical.phonelogs.adapters

import android.content.Context
import android.provider.CallLog.Calls.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import androidx.constraintlayout.widget.ConstraintLayout
import im.chimerical.phonelogs.CallLog
import im.chimerical.phonelogs.R
import im.chimerical.phonelogs.interfaces.OnSelected

class CallLogsListAdapter(private var list: ArrayList<CallLog>,
                          private val context: Context, private val onSelected: OnSelected
) :
    RecyclerView.Adapter<CallLogsListAdapter.CustomViewHolder>() {

    class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.callLogName)
        val number: TextView = view.findViewById(R.id.callLogNumber)
        val date: TextView = view.findViewById(R.id.callLogDate)
        val time: TextView = view.findViewById(R.id.callLogTime)
        val callTypeIcon: ImageView = view.findViewById(R.id.callTypeIcon)
        val photo: ImageView = view.findViewById(R.id.callLogImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.call_logs_list_item, parent, false)
        return CustomViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        if (list[position].name == null) {
            holder.name.text = list[position].number
            holder.number.visibility = View.GONE
            val dateTextViewParams = holder.date.layoutParams as ConstraintLayout.LayoutParams
            dateTextViewParams.marginStart = 10
        }
        else {
            holder.name.text = list[position].name
            holder.number.text = list[position].number
        }
        when (list[position].callType){
            INCOMING_TYPE -> holder.callTypeIcon.setImageDrawable(getDrawable(context, R.drawable.ic_incoming_call))
            MISSED_TYPE -> holder.callTypeIcon.setImageDrawable(getDrawable(context, R.drawable.ic_missed_call))
            OUTGOING_TYPE -> holder.callTypeIcon.setImageDrawable(getDrawable(context, R.drawable.ic_outgoing_call))
        }
        holder.date.text = list[position].date
        holder.time.text = list[position].time
        if (list[position].photo != null)
            holder.photo.setImageBitmap(list[position].photo)

        holder.itemView.setOnClickListener {
            onSelected.onClick(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

}