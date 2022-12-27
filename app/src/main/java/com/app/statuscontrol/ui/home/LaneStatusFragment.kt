package com.app.statuscontrol.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentLaneStatusBinding
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.ui.home.adapter.LaneStatusAdapter
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.viewmodel.LaneStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaneStatusFragment: Fragment() {
    // Binding
    private lateinit var binding: FragmentLaneStatusBinding

    // Adapter
    private var laneAdapter: LaneStatusAdapter = LaneStatusAdapter()

    // View Model
    private val viewModel: LaneStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaneStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initObservers()
        setUpListener()
        getAllLaneStatus()
    }

    private fun initRecyclerView() {
        binding.rvLaneStatusList.apply {
            adapter = laneAdapter
        }
    }

    private fun initObservers() {
        viewModel.laneListState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Success -> laneAdapter.submitList(state.data)
                is Resource.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                    .show()
                else -> Unit
            }
        }
    }

    private fun setUpListener() {
        binding.btnUserStatus click {
            activity?.let {
                (it as HomeActivity).showUserStatus()
            }
        }
    }

    private fun getAllLaneStatus() {
        viewModel.getAllLaneStatus()
    }
}