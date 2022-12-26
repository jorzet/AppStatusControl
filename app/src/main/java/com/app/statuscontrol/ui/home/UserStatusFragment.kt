package com.app.statuscontrol.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.statuscontrol.databinding.FragmentUserStatusBinding

class UserStatusFragment: Fragment() {

    private lateinit var binding: FragmentUserStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        setUpListeners()
    }

    private fun initObservers() {

    }

    private fun setUpListeners() {

    }
}