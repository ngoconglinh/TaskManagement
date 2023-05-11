package com.example.managementuikit.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.managementuikit.R
import com.example.managementuikit.Model.Task
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class TaskRecycleViewAdapter(
    private val context: Context,
    private val onClick: (Task) -> Unit,
    private val onSelectMenu: (Task) -> Unit
): RecyclerView.Adapter<TaskRecycleViewAdapter.TaskViewHolder>() {

    private var tasks: List<Task> = listOf()
    private var listColor: List<Int> = listOf(R.color.background_color, R.color.yellow, R.color.blue, R.color.green)

    inner class TaskViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val tvTitle:TextView = itemView.findViewById(R.id.tv_task_item_title)
        private val tvStatus:TextView = itemView.findViewById(R.id.tv_task_item_status)
        private val tvTime:TextView = itemView.findViewById(R.id.tv_task_item_time)
        private val tvTimeStart:TextView = itemView.findViewById(R.id.tv_task_item_time_start)
        private val tvTimeStop:TextView = itemView.findViewById(R.id.tv_task_item_time_stop)
        private val btnMenu:ImageButton = itemView.findViewById(R.id.btn_task_item_menu)
        private val layoutItem: LinearLayout = itemView.findViewById(R.id.task_item_layout)
        private val view: View = itemView.findViewById(R.id.view)
        private val avatar1: CardView = itemView.findViewById(R.id.avatar_1)
        private val avatar2: CardView = itemView.findViewById(R.id.avatar_2)
        private val avatar3: CardView = itemView.findViewById(R.id.avatar_3)

        @SuppressLint("SetTextI18n", "SimpleDateFormat", "InflateParams", "MissingInflatedId",
            "CutPasteId"
        )
        fun onBind(task: Task){
            val formatDateTime = SimpleDateFormat("HH:mm")
            tvTitle.text = task.title
            tvStatus.text = task.status
            tvTimeStart.text = SimpleDateFormat("hh:mm aa").format(task.starts_time)
            tvTimeStop.text = SimpleDateFormat("hh:mm aa").format(task.end_time)
            tvTime.text = formatDateTime.format(task.starts_time) + "-" + formatDateTime.format(task.end_time)
            btnMenu.setOnClickListener { onSelectMenu(task) }
            layoutItem.setOnClickListener { onClick(task) }
            val words: List<String> = task.participants.split(", ")
            val listColor3: ArrayList<Int> = arrayListOf(R.color.yellow_def, R.color.purple_500, R.color.black, R.color.purple_200, R.color.second_blue2)

            when (words.size){
                2 ->{
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.GONE
                    avatar3.visibility = View.GONE
                    val randomNum = Random.nextInt(0, listColor3.size - 1)
                    avatar1.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum]))
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                }
                3 ->{
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.VISIBLE
                    avatar3.visibility = View.GONE
                    val randomNum = Random.nextInt(0, listColor3.size - 1)
                    avatar1.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum]))
                    listColor3.removeAt(randomNum)
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                    val randomNum2 = Random.nextInt(0, listColor3.size - 1)
                    avatar2.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum2]))
                    avatar2.findViewById<TextView>(R.id.text_avatar).text = words[1]
                }
                else ->{
                    avatar1.visibility = View.VISIBLE
                    avatar2.visibility = View.VISIBLE
                    avatar3.visibility = View.VISIBLE

                    val randomNum = Random.nextInt(0, listColor3.size - 1)
                    avatar1.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum]))
                    avatar1.findViewById<TextView>(R.id.text_avatar).text = words[0]
                    listColor3.removeAt(randomNum)

                    val randomNum2 = Random.nextInt(0, listColor3.size - 1)
                    avatar2.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum2]))
                    avatar2.findViewById<TextView>(R.id.text_avatar).text = words[1]
                    listColor3.removeAt(randomNum2)

                    val randomNum3 = Random.nextInt(0, listColor3.size - 1)
                    avatar3.setCardBackgroundColor(ContextCompat.getColor(context, listColor3[randomNum3]))
                    avatar3.findViewById<TextView>(R.id.text_avatar).text = words[2]
                }
            }

            when(task.status){
                "OnGoing" -> setColor(0)
                "Pending" -> setColor(1)
                "Completed" -> setColor(2)
                "Cancel" -> setColor(3)
            }
        }
        private fun setColor(int: Int){
            view.setBackgroundColor(ContextCompat.getColor(context, listColor[int]))
            tvStatus.setTextColor(ContextCompat.getColor(context, listColor[int]))
            tvStatus.background.setTint(ContextCompat.getColor(context, listColor[int]))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false)

        return TaskViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.onBind(tasks[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(tasks:List<Task>){
        this.tasks = tasks
        notifyDataSetChanged()
    }
}