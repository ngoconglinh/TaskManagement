package com.example.managementuikit.Activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.example.managementuikit.R
import com.example.managementuikit.databinding.ActivityCreateTaskBinding
import com.example.managementuikit.Fragment.MainCalenderFragment
import com.example.managementuikit.Model.Task
import com.example.managementuikit.Service.ServiceAlarm
import com.example.managementuikit.ViewModel.TaskViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*


class CreateTaskActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityCreateTaskBinding
    private var mYearStart = 0
    private var mMonthStart = 0
    private var mDayStart = 0
    private var mHourStart = 0
    private var mMinuteStart = 0
    private var mYearStop = 0
    private var mMonthStop = 0
    private var mDayStop = 0
    private var mHourStop= 0
    private var mMinuteStop = 0
    private var timeStart:Long = 0
    private var timeStop:Long = 0
    private var mStatus:String = "OnGoing"
    private var checkCreateOrUpdate = true
    private lateinit var taskUpdate:Task
    private var listParticipants = ""
    private lateinit var listParticipantsEnter : ArrayList<String>
    @SuppressLint("SimpleDateFormat")
    private var formatTime = SimpleDateFormat("hh:mm a")
    private val taskViewModel:TaskViewModel by lazy {
        ViewModelProvider(this, TaskViewModel.TaskViewModelFactory(application))[TaskViewModel::class.java]
    }
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTaskBinding.inflate(layoutInflater)
        listParticipantsEnter = ArrayList()
        setContentView(binding.root)
        val intent = intent
        checkCreateOrUpdate = intent.getBooleanExtra("isCreate", true)
        if (checkCreateOrUpdate){
            //Create
            binding.btnCreateTask.text = getString(R.string.create_task)
            replaceCalenderFragment(MainCalenderFragment(),Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
            val time = Calendar.getInstance()
            viewModel.getDate().observe(this) { value ->
                if (value != null ) {
                    time.time = value
                    //time stop
                    mYearStart = time[Calendar.YEAR]
                    mMonthStart = time[Calendar.MONTH]
                    mDayStart = time[Calendar.DAY_OF_MONTH]
                    if(mHourStart == 0 || mMinuteStart == 0){
                        val calendarStop = Calendar.getInstance()
                        mHourStart = calendarStop.get(Calendar.HOUR_OF_DAY)
                        mMinuteStart= calendarStop.get(Calendar.MINUTE)
                    }
                    time[Calendar.HOUR_OF_DAY] = mHourStart
                    time[Calendar.MINUTE] = mMinuteStart
                    timeStart = time.timeInMillis
                    binding.tvDateStart.text = formatTime.format(time.time)
                    //time stop
                    mYearStop= time[Calendar.YEAR]
                    mMonthStop = time[Calendar.MONTH]
                    mDayStop = time[Calendar.DAY_OF_MONTH]
                    if(mHourStop== 0 || mMinuteStop == 0){
                        val calendarStop = Calendar.getInstance()
                        mHourStop = calendarStop.get(Calendar.HOUR_OF_DAY) + 1
                        mMinuteStop= calendarStop.get(Calendar.MINUTE)
                    }
                    time[Calendar.HOUR_OF_DAY] = mHourStop
                    time[Calendar.MINUTE] = mMinuteStop
                    timeStop = time.timeInMillis
                    binding.tvDateEnd.text = formatTime.format(time.time)
                }
            }
        } else {
            //Update
            @Suppress("DEPRECATION")
            if (intent.getSerializableExtra("UPDATE_TASK") == null){
                val id = intent.getIntExtra("Task_ID", -1)
                if (id >= 0){
                    taskViewModel.getAllTask().observe(this){
                        it.forEach {iz->
                            if (iz.id == id){
                                taskUpdate = iz
                                updateTaskSetLayout(taskUpdate)
                                val localDate = Instant.ofEpochMilli(taskUpdate.starts_time).atZone(ZoneId.systemDefault()).toLocalDate()
                                replaceCalenderFragment(MainCalenderFragment(),localDate)
                                return@forEach
                            }
                        }
                    }
                }
            } else{
                taskUpdate = intent.getSerializableExtra("UPDATE_TASK") as Task
                updateTaskSetLayout(taskUpdate)
                val localDate = Instant.ofEpochMilli(taskUpdate.starts_time).atZone(ZoneId.systemDefault()).toLocalDate()
                replaceCalenderFragment(MainCalenderFragment(),localDate)
            }

        }
        onClick()
        setColorSelectedStatus()
    }

    @SuppressLint("SimpleDateFormat", "UseCompatLoadingForDrawables")
    @Suppress("DEPRECATION")
    private fun updateTaskSetLayout(task : Task) {
        binding.btnCreateTask.text = getString(R.string.update_task)

        binding.txtTitle.setText(task.title)

        timeStart = task.starts_time
        binding.tvDateStart.text = formatTime.format(timeStart)
        val calendarStart = Calendar.getInstance()
        calendarStart.timeInMillis = task.starts_time
        mHourStart = calendarStart.get(Calendar.HOUR_OF_DAY)
        mMinuteStart = calendarStart.get(Calendar.MINUTE)
        timeStop = task.end_time
        binding.tvDateEnd.text = formatTime.format(timeStop)
        val calendarStop = Calendar.getInstance()
        calendarStop.timeInMillis = task.end_time
        mHourStop = calendarStop.get(Calendar.HOUR_OF_DAY)
        mMinuteStop= calendarStop.get(Calendar.MINUTE)

        listParticipants = task.participants

        val time = Calendar.getInstance()
        viewModel.getDate().observe(this) { value ->
            if (value != null ) {
                time.time = value
                //time stop
                mYearStart = time[Calendar.YEAR]
                mMonthStart = time[Calendar.MONTH]
                mDayStart = time[Calendar.DAY_OF_MONTH]
                time[Calendar.HOUR_OF_DAY] = mHourStart
                time[Calendar.MINUTE] = mMinuteStart
                timeStart = time.timeInMillis
                binding.tvDateStart.text = formatTime.format(time.time)
                //time stop
                mYearStop= time[Calendar.YEAR]
                mMonthStop = time[Calendar.MONTH]
                mDayStop = time[Calendar.DAY_OF_MONTH]
                time[Calendar.HOUR_OF_DAY] = mHourStop
                time[Calendar.MINUTE] = mMinuteStop
                timeStop = time.timeInMillis
                binding.tvDateEnd.text = formatTime.format(time.time)
            }
        }

        when(task.status){
            getString(R.string.ongoing) -> {
                setStatusColor(getString(R.string.ongoing), 1)
            }
            getString(R.string.pending) -> {
                setStatusColor(getString(R.string.pending), 2)
            }
            getString(R.string.completed) -> {
                setStatusColor(getString(R.string.completed), 3)
            }
            getString(R.string.cancel) -> {
                setStatusColor(getString(R.string.cancel), 4)
            }
        }

        val words: List<String> = listParticipants.split(", ")
        for (i in 0 until words.size -1){
            addChip(words[i])
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }
    private fun replaceCalenderFragment(fragment: Fragment, _date: LocalDate) {
        val fragmentManager = supportFragmentManager
        val arguments = Bundle()
        arguments.putSerializable("string_key",_date)
        fragment.arguments = arguments
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frag_container_view, fragment)
        fragmentTransaction.addToBackStack("null")
        fragmentTransaction.commit()
    }
    private fun updateTask(_task:Task) {
        _task.title = binding.txtTitle.text.toString().trim()
        _task.status = mStatus
        _task.starts_time = timeStart
        _task.end_time = timeStop
        _task.participants = getListParticipants()

        setAlarmNotification(_task)
        taskViewModel.updateTask(_task)
        Toast.makeText(this, "Update Task Successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setAlarmNotification(task: Task) {
        val intent = Intent(this, ServiceAlarm::class.java)
        val bundle = Bundle()
        bundle.putSerializable("Task", task)
        intent.putExtras(bundle)
        startService(intent)
    }


    @Suppress("NAME_SHADOWING")
    @SuppressLint("SetTextI18n")
    private fun onClick(){
        binding.btnCreateTask.setOnClickListener {
            @Suppress("EqualsBetweenInconvertibleTypes")
            if (binding.txtTitle.text!!.isNotEmpty() && timeStart < timeStop && timeStart.toInt() != 0
                && timeStop.toInt() != 0 && binding.chipGroup.size > 0){
                test()
            } else Toast.makeText(this, "Please enter valid item", Toast.LENGTH_SHORT).show()
        }
        binding.layoutPickDateEnd.setOnClickListener {
            timePickerStop()
        }
        binding.layoutPickDateStart.setOnClickListener {
            timePickerStart()
        }
        binding.btnAddParticipants.setOnClickListener {
            binding.txtTitle.clearFocus()
            showTextDialog()
        }
    }
    private fun test() {
        val arrayList: ArrayList<Task> = ArrayList()
        var checkFail = true
        var haha = 1
        taskViewModel.getAllTask().observe(this) { list ->
            arrayList.clear()
            list.forEach {
                if (checkCreateOrUpdate){
                    arrayList.add(it)
                } else{
                    if (taskUpdate.id == it.id){
                        return@forEach
                    }
                    arrayList.add(it)
                }
            }
            if (haha == 1){
                when(arrayList.size){
                    0 -> {
                        insertOrUpdate(checkCreateOrUpdate)
                    }
                    1 -> {
                        if (timeStart >= arrayList[0].end_time || timeStop <= arrayList[0].starts_time){
                            insertOrUpdate(checkCreateOrUpdate)
                        } else {
                            Toast.makeText(this, "Invalid start time", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else ->{
                        for (i in 0 until arrayList.size - 1){
                            for (j in i until arrayList.size){
                                val temp:Task
                                if (arrayList[i].starts_time > arrayList[j].starts_time){
                                    temp = arrayList[i]
                                    arrayList[i] = arrayList[j]
                                    arrayList[j] = temp
                                }
                            }
                        }
                        for (i in 0 until arrayList.size){
                            if (i == arrayList.size -1){
                                if (timeStart >= arrayList[arrayList.size -1].end_time ){
                                    insertOrUpdate(checkCreateOrUpdate)
                                    checkFail = false
                                    break
                                }
                            } else if(i == 0){
                                if (timeStop <= arrayList[0].starts_time){
                                    insertOrUpdate(checkCreateOrUpdate)
                                    checkFail = false
                                    break
                                }
                            } else {
                                if (timeStart >= arrayList[i].end_time && timeStop <= arrayList[i + 1].starts_time){
                                    insertOrUpdate(checkCreateOrUpdate)
                                    checkFail = false
                                    break
                                }
                            }
                        }
                        if (checkFail){
                            if (checkCreateOrUpdate){
                                Toast.makeText(this, "Create task failed", Toast.LENGTH_SHORT).show()
                            } else{
                                Toast.makeText(this, "Update task failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                haha++
            }
        }
    }
    private fun insertOrUpdate(boolean: Boolean){
        if (boolean){
            insertTask()
        } else{
            updateTask(taskUpdate)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showTextDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_textinput)
        dialog.window!!.setBackgroundDrawable(getDrawable(R.drawable.card_3))

        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (LinearLayout.LayoutParams.WRAP_CONTENT)
        dialog.window?.setLayout(width, height)

        val dialogTextInput: TextInputEditText = dialog.findViewById(R.id.btn_custom_dialog_text_input) as TextInputEditText
        val dialogCreate: Button = dialog.findViewById(R.id.btn_custom_dialog_create) as Button
        dialogCreate.setOnClickListener {
            val textInput = dialogTextInput.text.toString().trim()
            if (textInput.replace(" ", "") != ""){
                addChip(textInput)
                dialog.dismiss()
            }
        }
        val dialogClose: ImageView = dialog.findViewById(R.id.btn_custom_dialog_close) as ImageView
        dialogClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addChip(text:String) {
        listParticipantsEnter.add(text)
        val chip = Chip(this)
        chip.text = text
        chip.textSize = 14f
        chip.setTextColor(getColor(R.color.text_color_dark_70))
        chip.setChipIconResource(R.drawable.ic_launcher_foreground)
        chip.typeface = Typeface.create(ResourcesCompat.getFont(this,R.font.poppins_medium),Typeface.NORMAL)
        chip.chipIcon = TextDrawable.builder().buildRoundRect(text.first().toString().uppercase(), R.color.background_color, 100)
        chip.closeIcon = getDrawable(R.drawable.x)
        chip.closeIconSize = 30f
        chip.elevation = dpToPixel(3, this)
        chip.closeIconEndPadding = dpToPixel(5, this)
        chip.closeIconStartPadding = dpToPixel(10, this)
        chip.iconStartPadding = dpToPixel(5, this)
        chip.isCloseIconEnabled = true
        chip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.background_color_light))
        chip.chipCornerRadius = dpToPixel(10, this)
        chip.setOnCloseIconClickListener {
            val position = binding.chipGroup.indexOfChild(chip)
            listParticipantsEnter.removeAt(position)
            binding.chipGroup.removeView(it)
        }
        binding.chipGroup.addView(chip)
    }

    private fun insertTask() {
        val txtTitle = binding.txtTitle.text.toString().trim()
        val txtStatus = mStatus
        val txtDateStart = timeStart
        val txtDateEnd = timeStop
        val task = Task(txtTitle, txtStatus, txtDateStart, txtDateEnd, getListParticipants())
        taskViewModel.insertTask(task)
        taskViewModel.getAllTask().observe(this){
            var idTemp = 0
            it.forEach { itTask->
                if (itTask.id > idTemp){
                    idTemp = itTask.id
                }
            }
            it.forEach { itTask->
                if (itTask.id == idTemp){
                    setAlarmNotification(itTask)
                }
            }
        }

        Toast.makeText(this, "Create Task Successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun getListParticipants():String{
        listParticipants = ""
        listParticipantsEnter.forEach {ic -> listParticipants += "$ic, " }
        return listParticipants
    }

    @SuppressLint("SetTextI18n")
    private fun timePickerStart() {
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            val cal = Calendar.getInstance()
            mHourStart = hourOfDay
            mMinuteStart = minute
            cal[Calendar.HOUR_OF_DAY] = mHourStart
            cal[Calendar.MINUTE] = mMinuteStart
            cal[Calendar.SECOND] = 0
            cal[Calendar.YEAR] = mYearStart
            cal[Calendar.MONTH] = mMonthStart
            cal[Calendar.DAY_OF_MONTH] = mDayStart
            binding.tvDateStart.text = formatTime.format(cal.time)
            timeStart = cal.timeInMillis
            mHourStop = mHourStart + 1
            mMinuteStop = mMinuteStart
            setTimeStop()
            }, mHourStart, mMinuteStart, false
        )
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun timePickerStop() {
        val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
            mHourStop = hourOfDay
            mMinuteStop = minute
            setTimeStop()
            }, mHourStop, mMinuteStop, false
        )
        timePickerDialog.show()
    }

    private fun setTimeStop() {
        val cal = Calendar.getInstance()
        cal[Calendar.HOUR_OF_DAY] = mHourStop
        cal[Calendar.MINUTE] = mMinuteStop
        cal[Calendar.SECOND] = 0
        cal[Calendar.YEAR] = mYearStop
        cal[Calendar.MONTH] = mMonthStop
        cal[Calendar.DAY_OF_MONTH] = mDayStop
        binding.tvDateEnd.text = formatTime.format(cal.time)
        timeStop = cal.timeInMillis
    }

    private fun setColorSelectedStatus() {
        binding.selectionOngoing.setOnClickListener {
            setStatusColor(getString(R.string.ongoing), 1)
        }
        binding.selectionPending.setOnClickListener {
            setStatusColor(getString(R.string.pending), 2)
        }
        binding.selectionCompleted.setOnClickListener {
            setStatusColor(getString(R.string.completed), 3)
        }
        binding.selectionCancel.setOnClickListener {
            setStatusColor(getString(R.string.cancel), 4)
        }
    }

    private fun setStatusColor(text: String, position: Int) {
        when(position){
            1->{
                binding.selectionOngoing.background.setTint(ContextCompat.getColor(this, R.color.text_color_dark))
                binding.selectionOngoing.setTextColor(ContextCompat.getColor(this,  R.color.text_color_dark))
                binding.selectionPending.background.setTint(ContextCompat.getColor(this, R.color.yellow_40))
                binding.selectionPending.setTextColor(ContextCompat.getColor(this,  R.color.yellow_40))
                binding.selectionCompleted.background.setTint(ContextCompat.getColor(this, R.color.blue_40))
                binding.selectionCompleted.setTextColor(ContextCompat.getColor(this,  R.color.blue_40))
                binding.selectionCancel.background.setTint(ContextCompat.getColor(this, R.color.green_40))
                binding.selectionCancel.setTextColor(ContextCompat.getColor(this,  R.color.green_40))
            }
            2->{
                binding.selectionOngoing.background.setTint(ContextCompat.getColor(this, R.color.text_color_dark_40))
                binding.selectionOngoing.setTextColor(ContextCompat.getColor(this,  R.color.text_color_dark_40))
                binding.selectionPending.background.setTint(ContextCompat.getColor(this, R.color.yellow_def))
                binding.selectionPending.setTextColor(ContextCompat.getColor(this,  R.color.yellow_def))
                binding.selectionCompleted.background.setTint(ContextCompat.getColor(this, R.color.blue_40))
                binding.selectionCompleted.setTextColor(ContextCompat.getColor(this,  R.color.blue_40))
                binding.selectionCancel.background.setTint(ContextCompat.getColor(this, R.color.green_40))
                binding.selectionCancel.setTextColor(ContextCompat.getColor(this,  R.color.green_40))
            }
            3->{
                binding.selectionOngoing.background.setTint(ContextCompat.getColor(this, R.color.text_color_dark_40))
                binding.selectionOngoing.setTextColor(ContextCompat.getColor(this,  R.color.text_color_dark_40))
                binding.selectionPending.background.setTint(ContextCompat.getColor(this, R.color.yellow_40))
                binding.selectionPending.setTextColor(ContextCompat.getColor(this,  R.color.yellow_40))
                binding.selectionCompleted.background.setTint(ContextCompat.getColor(this, R.color.blue))
                binding.selectionCompleted.setTextColor(ContextCompat.getColor(this,  R.color.blue))
                binding.selectionCancel.background.setTint(ContextCompat.getColor(this, R.color.green_40))
                binding.selectionCancel.setTextColor(ContextCompat.getColor(this,  R.color.green_40))
            }
            4->{
                binding.selectionOngoing.background.setTint(ContextCompat.getColor(this, R.color.text_color_dark_40))
                binding.selectionOngoing.setTextColor(ContextCompat.getColor(this,  R.color.text_color_dark_40))
                binding.selectionPending.background.setTint(ContextCompat.getColor(this, R.color.yellow_40))
                binding.selectionPending.setTextColor(ContextCompat.getColor(this,  R.color.yellow_40))
                binding.selectionCompleted.background.setTint(ContextCompat.getColor(this, R.color.blue_40))
                binding.selectionCompleted.setTextColor(ContextCompat.getColor(this,  R.color.blue_40))
                binding.selectionCancel.background.setTint(ContextCompat.getColor(this, R.color.green))
                binding.selectionCancel.setTextColor(ContextCompat.getColor(this,  R.color.green))
            }
        }
        mStatus = text
    }
    private fun dpToPixel(dp: Int, context: Context): Float {
        return (dp * (context.resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }
}