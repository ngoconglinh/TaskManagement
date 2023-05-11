package com.example.managementuikit.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managementuikit.Activity.CreateTaskActivity
import com.example.managementuikit.Activity.HomeViewModel
import com.example.managementuikit.Adapter.TaskRecycleViewAdapter
import com.example.managementuikit.Model.Task
import com.example.managementuikit.R
import com.example.managementuikit.ViewModel.TaskViewModel
import com.example.managementuikit.databinding.FragmentCalenderBinding
import java.text.SimpleDateFormat
import java.util.*

class CalenderFragment : Fragment() {
    private lateinit var binding: FragmentCalenderBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(
            this,
            TaskViewModel.TaskViewModelFactory(requireActivity().application)
        )[TaskViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalenderBinding.inflate(inflater, container, false)
        replaceCalenderFragment(MainCalenderFragment())
        return binding.root
    }

    private fun initEvent() {
        viewModel.getDate().observe(viewLifecycleOwner) { value ->
            if (value != null) {
                initControls(value)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }
    @SuppressLint("SimpleDateFormat", "FragmentLiveDataObserve")
    private fun initControls(_date: Date) {
        val adapter = TaskRecycleViewAdapter(requireContext(), onItemClick, onItemMenuClick)
        binding.rcvFmCalender.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvFmCalender.adapter = adapter
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val current = formatter.format(_date)
        val newList: ArrayList<Task> = ArrayList()
        taskViewModel.getAllTask().observe(this) {
            newList.clear()
            it.forEach { s ->
                if (current >= formatter.format(s.starts_time) && current <= formatter.format(s.end_time)) {
                    newList.add(s)
                }
            }
            if (newList.size == 0){
                binding.imageViewNotFound.visibility = View.VISIBLE
                binding.rcvFmCalender.visibility = View.INVISIBLE
            } else {
                binding.imageViewNotFound.visibility = View.INVISIBLE
                binding.rcvFmCalender.visibility = View.VISIBLE

                for (i in 0 until newList.size - 1){
                    for (j in i until newList.size){
                        val temp:Task
                        if (newList[i].starts_time > newList[j].starts_time){
                            temp = newList[i]
                            newList[i] = newList[j]
                            newList[j] = temp
                        }
                    }
                }
                adapter.setTasks(newList)
            }
        }
    }

    private val onItemClick: (Task) -> Unit = {
        val intent = Intent(requireContext(), CreateTaskActivity::class.java)
        intent.putExtra("isCreate", false)
        intent.putExtra("UPDATE_TASK", it)
        requireContext().startActivity(intent)
    }

    private val onItemMenuClick: (Task) -> Unit = {
        taskViewModel.deleteTask(it)
        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
    }
    private fun replaceCalenderFragment(fragment: Fragment) {
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_replace, fragment)
        fragmentTransaction.addToBackStack("null")
        fragmentTransaction.commit()
    }
}