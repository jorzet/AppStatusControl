package com.app.statuscontrol.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentCreateLaneBinding
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.utils.click
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline
import com.app.statuscontrol.viewmodel.CreateLaneViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateLaneFragment: Fragment() {

    private lateinit var binding: FragmentCreateLaneBinding

    private val viewModel: CreateLaneViewModel by viewModels()

    private var laneStatus: LaneStatus? = null
    private var currentUser: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateLaneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (laneStatus != null) {
            setUpView()
        }

        initObservers()
        setUpListeners()
        setUpComponents()
        getEmployees()
    }

    override fun onDestroy() {
        (activity as HomeActivity).getUserInfo()
        super.onDestroy()
    }

    private fun initObservers() {
        viewModel.saveLaneState.observe(viewLifecycleOwner) { saveState ->
            when (saveState) {
                is Resource.Success -> {
                    activity?.onBackPressed()
                }
                is Resource.Error -> {
                    Toast.makeText(requireActivity(), "Ha ocurrido un error vuelva a intentarlo", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
        viewModel.employeesState.observe(viewLifecycleOwner) { employeesState ->
            when(employeesState) {
                is Resource.Success -> {
                    binding.spinnerEmployee
                }
                is Resource.Error -> {

                }
                else -> Unit
            }
        }
    }

    private fun setUpComponents() {

    }

    private fun setUpListeners() {
        binding.spinnerLaneStatus.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(av: AdapterView<*>?, v: View?, i: Int, l: Long) {
                if (i == 0)
                    binding.onlineImageView.showOnline()
                else
                    binding.onlineImageView.showOffline()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        binding.btnSave click {

            if (this.laneStatus != null) {
                laneStatus?.let {
                    it.id = currentUser?.laneId ?: "0"
                    val status = binding.spinnerLaneStatus.selectedItem.toString()
                    it.lane = binding.laneTextInputEditText.text.toString()
                    it.status = binding.spinnerLaneStatus.selectedItem.toString() == "Abierto"
                }
            } else {
                laneStatus = LaneStatus()
                laneStatus?.let {
                    it.id = currentUser?.laneId ?: "0"
                    it.lane = binding.laneTextInputEditText.text.toString()
                    it.status = binding.spinnerLaneStatus.selectedItem.toString() == "Abierto"
                }
            }

            viewModel.save(laneStatus!!)
        }
    }

    private fun setUpView() {
        laneStatus?.let {
            binding.laneTextInputEditText.setText(it.lane)
            binding.spinnerLaneStatus.setSelection(if (it.status) 0 else 1)
            if (it.status) binding.onlineImageView.showOnline()
            else binding.onlineImageView.showOffline()
        }
    }

    fun setLaneStatus(laneStatus: LaneStatus) {
        this.laneStatus = laneStatus
    }

    fun setCurrentUser(user: User) {

    }

    private fun getEmployees() {
        viewModel.getEmployees()
    }
}