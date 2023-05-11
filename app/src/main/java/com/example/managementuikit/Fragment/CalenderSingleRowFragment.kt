package com.example.managementuikit.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.managementuikit.databinding.FragmentCalenderSingleRowBinding
import com.example.managementuikit.R
import com.example.managementuikit.databinding.CalenderItemSelectedBinding
import com.example.managementuikit.Activity.HomeViewModel
import com.example.managementuikit.Model.Task
import com.example.managementuikit.ViewModel.TaskViewModel
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.DaySize
import com.kizitonwose.calendar.view.ViewContainer
import com.kizitonwose.calendar.view.WeekDayBinder
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class CalenderSingleRowFragment : Fragment() {
    private lateinit var binding: FragmentCalenderSingleRowBinding
    private val viewModel: HomeViewModel by activityViewModels()
    private val taskViewModel: TaskViewModel by lazy { ViewModelProvider(this,
            TaskViewModel.TaskViewModelFactory(requireActivity().application))[TaskViewModel::class.java]}
    private lateinit var dateHistory: LocalDate
    private val selectedDates = mutableSetOf<LocalDate>()
    private val calendar = Calendar.getInstance()
    private var currentMonth = 0
    private lateinit var _date: Date
    private lateinit var z: LocalDate
    @SuppressLint("SimpleDateFormat")
    private val formatter2 = SimpleDateFormat("yyyy-MM-dd")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalenderSingleRowBinding.inflate(layoutInflater)
        calendar.time = Date()
        _date = Date()
        currentMonth = calendar[Calendar.MONTH]
        setupWeekCalendar()
        z = dateHistory
        setOnclick()
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun countTasksToday() {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val current = formatter.format(_date)
        val newList: ArrayList<Task> = ArrayList()
        taskViewModel.getAllTask().observe(viewLifecycleOwner) {
            newList.clear()
            it.forEach { s ->
                if (current >= formatter.format(s.starts_time) && current <= formatter.format(s.end_time)) {
                    newList.add(s)
                }
            }
            binding.tvCountTask.text = "${newList.size} tasks today"
        }
        binding.btnForwardFragmentCalenderWeek.setOnClickListener {
            binding.weekCalendarView.smoothScrollToWeek(z.plusWeeks(1))
            z = z.plusWeeks(1)
        }
        binding.btnBackFragmentCalenderWeek.setOnClickListener {
            binding.weekCalendarView.smoothScrollToWeek(z.minusWeeks(1))
            z = z.minusWeeks(1)
        }
    }

    private fun setOnclick() {
        binding.btnCalenderCollapse.setOnClickListener {
            replaceCalenderFragment(CalenderMonthFragment())
        }
    }

    private fun replaceCalenderFragment(fragment: Fragment) {
        val fragmentManager = activity?.supportFragmentManager
        val arguments = Bundle()
        arguments.putSerializable("string_key",_date)
        fragment.arguments = arguments
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setupWeekCalendar() {
        class WeekDayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: WeekDay
            val textView = CalenderItemSelectedBinding.bind(view).tvDayNum2
            val textView2 = CalenderItemSelectedBinding.bind(view).tvDateNum2
            val layout = CalenderItemSelectedBinding.bind(view).layout
            val card = CalenderItemSelectedBinding.bind(view).card
            val dot = CalenderItemSelectedBinding.bind(view).dot
            init {
                layout.setOnClickListener {
                    if (day.position == WeekDayPosition.RangeDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        val arguments = arguments
        @Suppress("DEPRECATION")
        var desiredString = arguments?.getSerializable("string_key")
        if (desiredString == null){
            desiredString = formatter2.format(Date())
        }
        binding.weekCalendarView.dayBinder = object : WeekDayBinder<WeekDayViewContainer> {
            override fun create(view: View): WeekDayViewContainer = WeekDayViewContainer(view)
            @RequiresApi(Build.VERSION_CODES.P)
            override fun bind(container: WeekDayViewContainer, data: WeekDay) {
                container.day = data
                bindDate(data.date, container.textView, container.textView2,container.card,container.dot, data.position == WeekDayPosition.RangeDate)
            }
        }
        binding.weekCalendarView.weekScrollListener = {
            z = it.days.first().date
            val formatter = DateTimeFormatter.ofPattern("MMMM")
            binding.tvDayMonth.text = it.days.first().date.format(formatter)
        }
        val currentMonth = YearMonth.now()
        val startDate = currentMonth.minusMonths(100).atStartOfMonth()
        val endDate = currentMonth.plusMonths(100).atEndOfMonth()
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        dateHistory = LocalDate.parse(desiredString.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        binding.weekCalendarView.setup(startDate, endDate, daysOfWeek.first())
        binding.weekCalendarView.daySize = DaySize.FreeForm
        binding.weekCalendarView.scrollToWeek(LocalDate.parse(desiredString.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun bindDate(date: LocalDate, textView: TextView, textView2: TextView,
                         card: CardView, dot: CardView, isSelectable: Boolean) {
        textView.text = date.dayOfMonth.toString()
        val dateZ = date.dayOfWeek.toString().substring(0,3).lowercase()
        textView2.text = dateZ[0].uppercaseChar() + dateZ.substring(1)
        if (isSelectable) {
            when {
                dateHistory == date -> {
                    if (date == Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()){
                        textView.setTextColor(resources.getColor(R.color.text_color_dark ,null))
                        textView2.setTextColor(resources.getColor(R.color.text_color_dark ,null))
                        dot.visibility = View.VISIBLE
                        card.setCardBackgroundColor(resources.getColor(R.color.yellow, null))
                        card.cardElevation = 5f
                    } else {
                        textView.setTextColor(resources.getColor(R.color.text_color_dark ,null))
                        textView2.setTextColor(resources.getColor(R.color.text_color_dark ,null))
                        dot.visibility = View.INVISIBLE
                        card.setCardBackgroundColor(resources.getColor(R.color.yellow, null))
                        card.cardElevation = 5f
                    }
                    val formatter = DateTimeFormatter.ofPattern("MMMM dd")
                    binding.tvDayMonth.text = date.format(formatter)
                    _date = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                    viewModel.passingData(_date)
                    countTasksToday()
                    return
                }
                Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() == date -> {
                    textView.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    textView2.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    dot.visibility = View.VISIBLE
                    card.setCardBackgroundColor(Color.WHITE)
                    card.cardElevation = 0f
                }
                selectedDates.contains(date) -> {
                    textView.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    textView2.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    dot.visibility = View.INVISIBLE
                    card.setCardBackgroundColor(Color.WHITE)
                    card.cardElevation = 0f
                }
                else -> {
                    textView.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    textView2.setTextColor(resources.getColor(R.color.text_color_dark_50 ,null))
                    dot.visibility = View.INVISIBLE
                    card.setCardBackgroundColor(Color.WHITE)
                    card.cardElevation = 0f
                }
            }
        } else {
            textView.setTextColor(Color.RED)
        }
    }

    private fun dateClicked(date: LocalDate) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        dateHistory = date
        binding.weekCalendarView.notifyCalendarChanged()
    }

}
