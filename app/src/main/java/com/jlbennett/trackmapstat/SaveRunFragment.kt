package com.jlbennett.trackmapstat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.jlbennett.trackmapstat.databinding.FragmentSaveRunBinding


class SaveRunFragment : Fragment() {

    lateinit var binding: FragmentSaveRunBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_save_run, container, false)

        return binding.root
    }


}
