package com.example.managementuikit.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.managementuikit.Model.Task
import com.example.managementuikit.R
import kotlinx.coroutines.*
import kotlin.random.Random

class TaskCollapseRecycleViewAdapter(
    private val context: Context,
    private val onClick: (Task) -> Unit,
    private val onSelectMenu: (Task) -> Unit
) : RecyclerView.Adapter<TaskCollapseRecycleViewAdapter.TaskCollapseViewHolder>() {

    private var tasks: List<Task> = listOf()
    private var listColor: List<Int> =
        listOf(R.color.background_color, R.color.yellow, R.color.blue, R.color.green)

    inner class TaskCollapseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_task_item_collapse_title)
        private val process: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val layout: CardView = itemView.findViewById(R.id.task_item_collapse_layout)
        private val menu: ImageView = itemView.findViewById(R.id.btn_task_item_collapse_menu)
        private val pendingIcon: AppCompatImageView = itemView.findViewById(R.id.pending_icon)
        private val avatar1: CardView = itemView.findViewById(R.id.avatar_1)
        private val avatar2: CardView = itemView.findViewById(R.id.avatar_2)
        private val avatar3: CardView = itemView.findViewById(R.id.avatar_3)
        private var checkTimerRunning = false
        @SuppressLint("SetTextI18n", "InflateParams")
        fun onBind(task: Task) {
            val startTimestamp = task.starts_time
            val endTimestamp = task.end_time
            val totalTime = endTimestamp - startTimestamp //totalTime se la max cua progress
            process.max = totalTime.toInt()
            process.min = 0
            val countDownTimer = object : CountDownTimer(totalTime, 1000) {
                @SuppressLint("NotifyDataSetChanged")
                override fun onTick(millisUntilFinished: Long) {
                    val currentTimestamp = System.currentTimeMillis() // lay thoi gian hien tai
                    val elapsedTime = currentTimestamp - startTimestamp
                    process.progress = elapsedTime.toInt()
                    checkTimerRunning = true
                }
                override fun onFinish() {
                    checkTimerRunning = false
                }
            }

            if (System.currentTimeMillis() in startTimestamp..endTimestamp) {
                countDownTimer.start()
            } else {
                if (System.currentTimeMillis() < startTimestamp) {
                    process.progress = 0
                }
                if (System.currentTimeMillis() > endTimestamp) {
                    process.progress = totalTime.toInt()
                }
                if (checkTimerRunning){
                    countDownTimer.cancel()
                }
            }

            tvTitle.text = task.title
            menu.setOnClickListener {
                onSelectMenu(task)
                countDownTimer.cancel()
            }

            layout.setOnClickListener { onClick(task) }
            val words: List<String> = task.participants.split(", ")
            val listColor2: ArrayList<Int> = arrayListOf(
                R.color.yellow_def,
                R.color.purple_500,
                R.color.black,
                R.color.purple_200,
                R.color.second_blue2
            )
            when (words.size) {
                2 -> {
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.GONE
                    avatar3.visibility = View.GONE
                    val randomNum = Random.nextInt(0, listColor2.size - 1)
                    avatar1.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum]
                        )
                    )
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                }
                3 -> {
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.VISIBLE
                    avatar3.visibility = View.GONE
                    val randomNum = Random.nextInt(0, listColor2.size - 1)
                    avatar1.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum]
                        )
                    )
                    listColor2.removeAt(randomNum)
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                    val randomNum2 = Random.nextInt(0, listColor2.size - 1)
                    avatar2.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum2]
                        )
                    )
                    avatar2.findViewById<TextView>(R.id.text_avatar).text = words[1]
                }
                else -> {
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.VISIBLE
                    avatar3.visibility = View.VISIBLE

                    val randomNum = Random.nextInt(0, listColor2.size - 1)
                    avatar1.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum]
                        )
                    )
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                    listColor2.removeAt(randomNum)

                    val randomNum2 = Random.nextInt(0, listColor2.size - 1)
                    avatar2.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum2]
                        )
                    )
                    avatar2.findViewById<TextView>(R.id.text_avatar).text = words[1]
                    listColor2.removeAt(randomNum2)

                    val randomNum3 = Random.nextInt(0, listColor2.size - 1)
                    avatar3.setCardBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            listColor2[randomNum3]
                        )
                    )
                    avatar3.findViewById<TextView>(R.id.text_avatar).text = words[2]
                }
            }

            when (task.status) {
                "OnGoing" -> setColor(0)
                "Pending" -> setColor(1)
                "Completed" -> setColor(2)
                "Cancel" -> setColor(3)
            }
        }

        private fun setColor(int: Int) {
            process.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, listColor[int]))
            pendingIcon.setColorFilter(ContextCompat.getColor(context, listColor[int]))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskCollapseViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.task_item_collapse, parent, false)

        return TaskCollapseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: TaskCollapseViewHolder, position: Int) {
        holder.onBind(tasks[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }
}