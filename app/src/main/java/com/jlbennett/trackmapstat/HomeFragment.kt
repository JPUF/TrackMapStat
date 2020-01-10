package com.jlbennett.trackmapstat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.jlbennett.trackmapstat.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.trackCard.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_trackFragment) }
        binding.allRunsCard.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_allRunsFragment) }

        return binding.root
    }


}
