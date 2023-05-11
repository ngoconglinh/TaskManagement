package com.example.managementuikit.Fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.managementuikit.databinding.CalendarDayLayoutBinding
import com.example.managementuikit.databinding.FragmentCalenderMonthBinding
import com.example.managementuikit.R
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*


class CalenderMonthFragment : Fragment() {
    private lateinit var binding: FragmentCalenderMonthBinding
    private val selectedDates = mutableSetOf<LocalDate>()
    private lateinit var z: LocalDate

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentCalenderMonthBinding.inflate(layoutInflater)
        setUpCalendar()
        setOnClick()
        return binding.root
    }

    private fun setOnClick() {
        binding.btnForwardFragmentCalenderMonth.setOnClickListener {
            if (z.monthValue == 12){
                z = z.withYear(z.year + 1)
                z = z.withMonth(1)
            } else {
                z = z.withMonth(z.monthValue + 1)
            }
            binding.calendarView.smoothScrollToMonth(z.yearMonth)
        }
        binding.btnBackFragmentCalenderMonth.setOnClickListener {
            if (z.monthValue == 1){
                z = z.withYear(z.year - 1)
                z = z.withMonth(12)
            } else {
                z = z.withMonth(z.monthValue - 1)
            }
            binding.calendarView.smoothScrollToMonth(z.yearMonth)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
    private fun setUpCalendar() {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            init {
                textView.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        dateClicked(date = day.date)
                    }
                }
            }
        }
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        val arguments = arguments
        @Suppress("DEPRECATION")
        val desiredString = arguments!!.getSerializable("string_key")
        val current = formatter.format(desiredString)
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        z = LocalDate.parse(current.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        binding.calendarView.scrollToDate(LocalDate.parse(current, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                bindDate(data.date, container.textView, data.position == DayPosition.MonthDate,
                    LocalDate.parse(current.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            }
        }
        binding.calendarView.monthScrollListener = {
            z = it.yearMonth.atStartOfMonth()
            @Suppress("NAME_SHADOWING")
            val formatter = DateTimeFormatter.ofPattern("MMMM")
            binding.tvMonth.text = formatter.format(z).toString()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun bindDate(date: LocalDate, textView: TextView, isSelectable: Boolean, check:LocalDate) {
        textView.text = date.dayOfMonth.toString()
        if (isSelectable) {
            when {
                check == date -> {
                    textView.background = resources.getDrawable(R.drawable.card_4, null)
                    textView.setTextColor(resources.getColor(R.color.text_color_dark, null))
                }
                date == LocalDate.now() -> {
                    textView.setTextColor(resources.getColor(R.color.yellow, null))
                    textView.background = null
                }
                else -> {
                    textView.setTextColor(resources.getColor(R.color.text_color_dark_50, null))
                    textView.background = null
                }
            }
        } else {
            textView.setTextColor(Color.WHITE)
        }

        textView.setOnClickListener {
            textView.background = resources.getDrawable(R.drawable.card_4, null)
            textView.setTextColor(resources.getColor(R.color.text_color_dark, null))
            replaceCalenderFragment(CalenderSingleRowFragment(), date)
        }
    }

//    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat")
//    private fun setUpCalendar() {
//        val formatter = SimpleDateFormat("yyyy-MM-dd")
//        val arguments = arguments
//        @Suppress("DEPRECATION")
//        val desiredString = arguments!!.getSerializable("string_key")
//        val current = formatter.format(desiredString)
//        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY)
//        val currentMonth = YearMonth.now()
//        val startMonth = currentMonth.minusMonths(100)
//        val endMonth = currentMonth.plusMonths(100)
//        binding.calendarView.setup(startMonth, endMonth, daysOfWeek.first())
//        binding.calendarView.scrollToDate(LocalDate.parse(current, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
//        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
//            override fun create(view: View) = DayViewContainer(view)
//            override fun bind(container: DayViewContainer, data: CalendarDay) {
//                container.textView.text = data.date.dayOfMonth.toString()
//                month = data.date
//                binding.tvMonth.text = SimpleDateFormat("MMMM").format(desiredString)
//                if (data.date == Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()){
//                    container.textView.setTextColor(resources.getColor(R.color.yellow, null))
//                }
//                if (current == data.date.toString()){
//                    container.textView.background = resources.getDrawable(R.drawable.card_4, null)
//                    container.textView.setTextColor(resources.getColor(R.color.text_color_dark, null))
//                }
//                if (data.position == DayPosition.MonthDate) {
//                    container.textView.setTextColor(resources.getColor(R.color.text_color_dark_50, null))
//                } else {
//                    container.textView.setTextColor(Color.WHITE)
//                }
//                container.textView.setOnClickListener {
//                    container.textView.setTextColor(resources.getColor(R.color.text_color_dark, null))
//                    container.textView.background = resources.getDrawable(R.drawable.card_4, null)
//                    replaceCalenderFragment(CalenderSingleRowFragment(), data.date)
//                }
//            }
//        }
//    }

    private fun dateClicked(date: LocalDate) {
        if (selectedDates.contains(date)) {
            selectedDates.remove(date)
        } else {
            selectedDates.add(date)
        }
        binding.calendarView.notifyCalendarChanged()
    }
    private fun replaceCalenderFragment(fragment: Fragment, _date: LocalDate) {
        val fragmentManager = activity?.supportFragmentManager
        val arguments = Bundle()
        arguments.putSerializable("string_key", _date)
        fragment.arguments = arguments
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
//class DayViewContainer(view: View) : ViewContainer(view) {
//    val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
//}