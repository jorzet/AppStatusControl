package com.app.statuscontrol.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentUserStatusBinding
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.ui.home.adapter.UserStatusAdapter
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.viewmodel.UserStatusViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserStatusFragment: Fragment() {
    // Binding
    private lateinit var binding: FragmentUserStatusBinding

    // Adapter
    private var userStatusAdapter: UserStatusAdapter = UserStatusAdapter()

    // View Model
    private val viewModel: UserStatusViewModel by viewModels()

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

        initRecyclerView()
        initObservers()
        setUpListener()
        getAllUserStatus()
    }

    private fun initRecyclerView() {
        binding.rvUserStatusList.apply {
            adapter = userStatusAdapter
        }
    }

    private fun initObservers() {
        viewModel.userListState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is Resource.Success -> userStatusAdapter.submitList(state.data)
                is Resource.Error -> Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT)
                    .show()
                else -> Unit
            }
        }
    }

    private fun setUpListener() {
        binding.btnLaneStatus click {
            activity?.onBackPressed()
        }
    }

    private fun getAllUserStatus() {
        viewModel.getAllUSerStatus()
    }
}