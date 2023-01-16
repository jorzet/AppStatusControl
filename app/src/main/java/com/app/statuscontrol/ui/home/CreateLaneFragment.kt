package com.app.statuscontrol.ui.home

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.app.statuscontrol.databinding.FragmentCreateLaneBinding
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.domain.model.Resource
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.domain.model.UserType
import com.app.statuscontrol.utils.*
import com.app.statuscontrol.viewmodel.CreateLaneViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.checkerframework.checker.units.qual.s
import java.util.stream.Collectors
import java.util.stream.Stream
import kotlin.streams.toList


@AndroidEntryPoint
class CreateLaneFragment: Fragment() {

    private lateinit var binding: FragmentCreateLaneBinding

    private val viewModel: CreateLaneViewModel by viewModels()

    private var laneStatus: LaneStatus? = null
    private var selectedUser: User? = null

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
                is Resource.Loading -> {
                    handleLoading(true)
                }
                is Resource.Success -> {
                    setUpEmployeesSpinner(employeesState.data)
                    handleLoading(false)
                }
                is Resource.Error -> {
                    handleLoading(false)
                }
                else -> Unit
            }
        }
    }

    private fun setUpEmployeesSpinner(employeeList: List<User>) {

        var employees: List<String> = employeeList
            .stream()
            .map { it.name }
            .collect(Collectors.toList())

        if (employees.isNotEmpty()) {
            employees = listOf("Seleccione un empleado") + employees
        }

        val spinnerAdapter= object : ArrayAdapter<String>(requireContext(),R.layout.simple_spinner_item, employees) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if (position == 0) {
                    view.setTextColor(Color.GRAY)
                } else {
                    view.setTextColor(Color.BLACK)
                }
                return view
            }
        }

        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerEmployee.adapter = spinnerAdapter
        binding.spinnerEmployee.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, v: View?, i: Int, l: Long) {
                if (i > 0) {
                    selectedUser = employeeList[i - 1]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        if (selectedUser != null) {
            selectedUser = employeeList.find { it.uid == selectedUser?.uid }
            val employeeIndex = employeeList.indexOfFirst { it.uid == selectedUser?.uid } + 1
            binding.spinnerEmployee.setSelection(employeeIndex)
        }
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
                    it.lane = binding.laneTextInputEditText.text.toString()
                    it.status = binding.spinnerLaneStatus.selectedItem.toString() == "Abierto"
                    it.userUid = selectedUser?.uid ?: "0"
                    it.modifiedBy = selectedUser?.name?: ""
                }
            } else {
                laneStatus = LaneStatus()
                laneStatus?.let {
                    it.id = selectedUser?.laneId ?: "0"
                    it.lane = binding.laneTextInputEditText.text.toString()
                    it.status = binding.spinnerLaneStatus.selectedItem.toString() == "Abierto"
                    it.userUid = selectedUser?.uid ?: "0"
                    it.modifiedBy = selectedUser?.name?: ""
                }
            }

            if (selectedUser != null) {
                selectedUser?.let {
                    it.lane = laneStatus?.lane.toString()
                    it.laneId = laneStatus?.id.toString()
                }
            } else {
                selectedUser = User()
                selectedUser?.let {
                    it.lane = laneStatus?.lane.toString()
                    it.laneId = laneStatus?.id.toString()
                }
            }

            viewModel.saveLane(laneStatus!!)
            //viewModel.saveUser(selectedUser!!)
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

    fun setSelectedUser(user: String, uid: String) {
        this.selectedUser = User(name = user, uid = uid)
    }

    private fun getEmployees() {
        viewModel.getEmployees()
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                loading.setVisible()
                scrollViewContainer.setGone()
            } else {
                loading.setGone()
                scrollViewContainer.setVisible()
            }
        }
    }
}