package com.jlbennett.trackmapstat


import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jlbennett.trackmapstat.database.RunContract
import com.jlbennett.trackmapstat.databinding.FragmentAllRunsBinding

/*
    A UI Fragment to allow users to see and select all of their saved runs.
 */
class AllRunsFragment : Fragment() {

    private lateinit var binding: FragmentAllRunsBinding
    private lateinit var runAdapter: RunAdapter
    private lateinit var runRecycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_runs, container, false)

        runRecycler = binding.runRecycler
        runRecycler.layoutManager = LinearLayoutManager(activity)
        runAdapter = RunAdapter(getAllRuns()!!)//The run adapter is initialised with a Cursor pointing to all Runs.
        runAdapter.notifyDataSetChanged()
        runRecycler.adapter = runAdapter

        if (runAdapter.itemCount == 0) {
            binding.noRunsText.visibility = View.VISIBLE
        } else {
            binding.noRunsText.visibility = View.GONE
        }


        return binding.root
    }

    /*
        Queries the ContentProvider to return a Cursor containing references to all of the Runs stored in the DB.
     */
    private fun getAllRuns(): Cursor? {
        return context!!.contentResolver.query(
            RunContract.CONTENT_URI,
            null,
            null,
            null,
            RunContract.RunEntry.COLUMN_ID
        )
    }
}
