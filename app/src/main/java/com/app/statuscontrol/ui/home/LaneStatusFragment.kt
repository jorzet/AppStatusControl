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
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.model.UserType
import com.app.statuscontrol.ui.home.adapter.LaneStatusAdapter
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.setGone
import com.app.statuscontrol.utils.setInvisible
import com.app.statuscontrol.utils.setVisible
import com.app.statuscontrol.viewmodel.LaneStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LaneStatusFragment(val user: User): Fragment() {
    // Binding
    private lateinit var binding: FragmentLaneStatusBinding

    // Adapter
    private lateinit var laneAdapter: LaneStatusAdapter

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
        initObservers()
        setUpListener()
        //viewModel.getUser()
        initRecyclerView()
        getAllLaneStatus()
    }

    private fun initRecyclerView() {
        val isAdmin = user.userType == UserType.ADMIN.userType

        laneAdapter = LaneStatusAdapter(isAdmin)
        if (isAdmin) {
            laneAdapter.setEditLaneClickListener { laneStatus ->
                (activity as HomeActivity).showEditLane(laneStatus)
            }
        }
        binding.rvLaneStatusList.apply {
            adapter = laneAdapter
        }
    }

    private fun initObservers() {
        viewModel.laneListState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    laneAdapter.submitList(state.data)
                    handleLoading(false)
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                        .show()
                    handleLoading(false)
                }
                else -> Unit
            }
        }
        viewModel.userLaneState.observe(viewLifecycleOwner) { userState ->
            when(userState) {
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    initRecyclerView()
                    getAllLaneStatus()
                    handleLoading(false)
                }
                is Resource.Error -> {
                    handleLoading(false)
                }
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

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                loading.setVisible()
                scrollView.setInvisible()
            } else {
                loading.setGone()
                scrollView.setVisible()
            }
        }
    }
}