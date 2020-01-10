package com.jlbennett.trackmapstat


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.jlbennett.trackmapstat.databinding.FragmentHomeBinding

/*
    This is the simple HomeFragment. It is static, with no dynamic view elements.
 */
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //DataBinding provides compile-time access to view elements. Eliminating the need for findViewById()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        //Each image is displayed within a card.
        //When the user clicks anywhere on each card, they are navigated to the appropriate fragment.
        //Navigation is done using Android Navigation component.
        // This allows for paths to be defined in a clear manner, in res/navigation/navigation.xml
        binding.trackCard.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_trackFragment) }
        binding.allRunsCard.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_allRunsFragment) }

        return binding.root
    }


}
