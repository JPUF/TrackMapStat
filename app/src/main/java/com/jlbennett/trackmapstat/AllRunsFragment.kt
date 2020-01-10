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


class AllRunsFragment : Fragment() {

    private lateinit var binding: FragmentAllRunsBinding
    private lateinit var runAdapter: RunAdapter
    private lateinit var runRecycler: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_runs, container, false)

        runRecycler = binding.runRecycler
        runRecycler.layoutManager = LinearLayoutManager(activity)
        runAdapter = RunAdapter(getAllRuns()!!)
        runAdapter.notifyDataSetChanged()
        runRecycler.adapter = runAdapter


        return binding.root
    }

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
