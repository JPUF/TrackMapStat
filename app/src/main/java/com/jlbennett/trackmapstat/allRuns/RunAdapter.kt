package com.jlbennett.trackmapstat.allRuns

import android.database.Cursor
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.text.bold
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.jlbennett.trackmapstat.allRuns.AllRunsFragmentDirections
import com.jlbennett.trackmapstat.R
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

/*
    A Class to Adapter the database items to be displayed in a RecyclerView.
 */
class RunAdapter(cursor: Cursor) : RecyclerView.Adapter<RunViewHolder>() {
    //This class has a reference to a database Cursor object, which contains references to the desired items.
    private var runCursor: Cursor = cursor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.run_item, parent, false)
        return RunViewHolder(view)
    }

    override fun getItemCount(): Int {
        return runCursor.count
    }

    /*
        Retrieves each individual field, and updates the respective View within the ViewHolder.
     */
    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        if (!runCursor.moveToPosition(position)) return

        val id = runCursor.getInt(runCursor.getColumnIndex(RunEntry.COLUMN_ID))
        val name = runCursor.getString(runCursor.getColumnIndex(RunEntry.COLUMN_NAME))
        val distance = runCursor.getFloat(runCursor.getColumnIndex(RunEntry.COLUMN_DISTANCE))
        val time = runCursor.getFloat(runCursor.getColumnIndex(RunEntry.COLUMN_TIME)).toLong()
        val seconds = (time / 1000000000)
        val minutes = seconds / 60
        val hours = minutes / 60
        val prettyTime = "%d:%02d:%02d".format(hours, minutes % 60, seconds % 60)
        val prettyDistance = "${"%.0f".format(distance)}m"

        val span = SpannableStringBuilder()
            .append("Distance: ")
            .bold { append(prettyDistance) }
            .append(" - Time: ")
            .bold { append(prettyTime) }

        holder.idText.text = id.toString()
        holder.nameText.text = name
        holder.distanceTimeText.setText(span, TextView.BufferType.SPANNABLE)
        holder.card.setOnClickListener { onItemClick(it as CardView) }
    }

    private fun onItemClick(card: CardView) {
        val cardLayout = card[0] as LinearLayout
        val idText = cardLayout[0] as TextView
        val id = Integer.parseInt(idText.text.toString())//The ID is parsed from the View.
        Log.d("Track", "RunAdapter: onClick: id = $id")
        val action = AllRunsFragmentDirections.actionAllRunsFragmentToViewSimpleRunFragment(id)
        card.findNavController().navigate(action)
        //Navigation.createNavigateOnClickListener(action.actionId)
    }
}

/*
    A class that acts as a wrapper around each list item.
 */
class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    override fun onClick(p0: View?) {/*Implemented in AdapterClass*/
    }

    var card: CardView = itemView.findViewById(R.id.item_card)
    var idText: TextView = itemView.findViewById(R.id.id_text)
    var nameText: TextView = itemView.findViewById(R.id.name_text)
    var distanceTimeText: TextView = itemView.findViewById(R.id.distance_time_text)

    init {
        itemView.setOnClickListener(this)
    }
}