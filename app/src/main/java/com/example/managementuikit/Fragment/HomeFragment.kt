package com.example.managementuikit.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managementuikit.Activity.CreateTaskActivity
import com.example.managementuikit.Adapter.TaskCollapseRecycleViewAdapter
import com.example.managementuikit.Model.Task
import com.example.managementuikit.R
import com.example.managementuikit.ViewModel.TaskViewModel
import com.example.managementuikit.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var checkShowAllTask = true
    private val taskViewModel: TaskViewModel by lazy { ViewModelProvider(this,
            TaskViewModel.TaskViewModelFactory(requireActivity().application))[TaskViewModel::class.java]
    }
    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        binding.tvHomeFragmentToday.text = formatter.format(time)
        binding.homeFragmentShowAllTask.setOnClickListener {
            if (checkShowAllTask){
                binding.rcvHomeFragment.visibility = View.GONE
                binding.homeFragmentIconDropdown.animate().rotation(180F).start()
                checkShowAllTask = false
            } else {
                binding.rcvHomeFragment.visibility = View.VISIBLE
                binding.homeFragmentIconDropdown.animate().rotation(0f).start()
                checkShowAllTask = true
            }
        }
        setCountTask(getString(R.string.ongoing), binding.tvOngoing)
        setCountTask(getString(R.string.pending), binding.tvPending)
        setCountTask(getString(R.string.completed), binding.tvCompleted)
        setCountTask(getString(R.string.cancel), binding.tvCancel)
        val sharedPreferences = activity?.getSharedPreferences("SHARED_PREFERENCES_USER", AppCompatActivity.MODE_PRIVATE)
        binding.textViewUserNameHomeFragment.text =  sharedPreferences!!.getString("name", "Unknown")

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        getListTask()
    }
    private fun getListTask() {
        val adapter = TaskCollapseRecycleViewAdapter(requireContext(), onItemClick, onItemMenuClick)
        binding.rcvHomeFragment.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvHomeFragment.adapter = adapter
        val newList: ArrayList<Task> = ArrayList()
        taskViewModel.getAllTask().observe(viewLifecycleOwner) {
            newList.clear()
            it.forEach { s ->
                newList.add(s)
            }
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
    @SuppressLint("SetTextI18n", "FragmentLiveDataObserve")
    private fun setCountTask(status: String, textView: TextView) {
        val arrayList: ArrayList<Task> = ArrayList()
        taskViewModel.findTaskByStatus(status).observe(this){ tasks ->
            arrayList.clear()
            tasks.forEach {
                arrayList.add(it)
            }
            textView.text = "${arrayList.size} Tasks"
        }
    }
}