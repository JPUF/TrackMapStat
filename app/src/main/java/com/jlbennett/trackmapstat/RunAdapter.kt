package com.jlbennett.trackmapstat

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
import androidx.recyclerview.widget.RecyclerView
import com.jlbennett.trackmapstat.database.RunContract.RunEntry

class RunAdapter(cursor: Cursor) : RecyclerView.Adapter<RunViewHolder>() {
    private var runCursor: Cursor = cursor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.run_item, parent, false)
        return RunViewHolder(view)
    }

    override fun getItemCount(): Int {
        return runCursor.count
    }

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
    }
}

class RunViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
    var idText: TextView = itemView.findViewById(R.id.id_text)
    var nameText: TextView = itemView.findViewById(R.id.name_text)
    var distanceTimeText: TextView = itemView.findViewById(R.id.distance_time_text)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        val cardView: CardView = view as CardView
        val cardLayout = cardView[0] as LinearLayout
        val idText = cardLayout[0] as TextView
        Log.d("Track", "RunAdapter: onClick: id = ${idText.text}")
        //TODO could send info to a RunFragment class by displaying an ID in Recycler.
        //Read that ID here. Send that to the fragment. Read the appropriate Run from DB in that fragment.

        /*
        val intent = Intent(view.context, RunFragment::class.java)
        intent.putExtra("MODE", RunFragment.VIEW)
        intent.putExtra("RECIPE_ID", recipeID)

        view.context.startActivity(intent)
        */
    }
}