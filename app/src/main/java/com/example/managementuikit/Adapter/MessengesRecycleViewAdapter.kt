package com.example.managementuikit.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.managementuikit.Activity.SplashActivity.Companion.auth
import com.example.managementuikit.Model.MessageUser
import com.example.managementuikit.R
import java.text.SimpleDateFormat
import java.util.*

class MessengesRecycleViewAdapter(private val context: Context): RecyclerView.Adapter<MessengesRecycleViewAdapter.MessengesViewHolder>() {
    private val typeSend = 1
    private val typeReciver = 2
    private val typeLoading = 3
    private var mMessageUser: List<MessageUser> = listOf()


    inner class MessengesViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val tvText: TextView = itemView.findViewById(R.id.text_view_text_messenges)
        private val tvTimeLine: TextView = itemView.findViewById(R.id.text_view_timeline_messenges)
        private val imageView: ImageView = itemView.findViewById(R.id.imageView_messages_activity)
        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun onBind(messageUser: MessageUser, position: Int){

            if (messageUser.text.contains("https://firebasestorage.googleapis.com/v0/b/taskmanagement-5ba23.appspot.com/o/Images")){
                imageView.load(messageUser.text)
                tvText.visibility = View.GONE
                imageView.visibility = View.VISIBLE
            } else{
                tvText.visibility = View.VISIBLE
                imageView.visibility = View.GONE
                tvText.text = messageUser.text
            }

            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
            val formatTime = SimpleDateFormat("HH:mm, EEE dd MMM yyyy")
            val formatTimeToday = SimpleDateFormat("EEE dd MMM yyyy")
            if (messageUser.date != ""){
                val date: Date = sdf.parse(messageUser.date) as Date
                var text = formatTime.format(date)
                if (position < mMessageUser.size - 1){
                    if (formatTimeToday.format(date) != formatTimeToday.format(sdf.parse(mMessageUser[position + 1].date) as Date)){
                        tvTimeLine.visibility = View.VISIBLE
                    } else {
                        tvTimeLine.visibility = View.GONE
                    }
                } else {
                    tvTimeLine.visibility = View.VISIBLE
                }
                if (formatTimeToday.format(date) == formatTimeToday.format(Date())){
                    text = "Today"
                }
                tvTimeLine.text = text

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessengesViewHolder {
        return when (viewType) {
            typeSend -> {
                val view: View = LayoutInflater.from(context).inflate(R.layout.messages_send, parent, false)
                MessengesViewHolder(view)
            }
            typeReciver -> {
                val view: View = LayoutInflater.from(context).inflate(R.layout.messages_reciver, parent, false)
                MessengesViewHolder(view)
            }
            typeLoading -> {
                val view: View = LayoutInflater.from(context).inflate(R.layout.item_loading, parent, false)
                MessengesViewHolder(view)
            }
            else -> {
                throw RuntimeException("The type has to be ONE or TWO")
            }
        }
    }

    override fun getItemCount(): Int {
        return mMessageUser.size
    }

    override fun onBindViewHolder(holder: MessengesViewHolder, position: Int) {
        holder.onBind(mMessageUser[position], position)
    }
    override fun getItemViewType(position: Int): Int {
        return if (mMessageUser[position] == MessageUser("", "","")) typeLoading else {
            return if (mMessageUser[position].sender == auth.uid.toString()) {
                typeSend
            } else {
                typeReciver
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMessage(messageUser:List<MessageUser>){
        this.mMessageUser = messageUser
        notifyDataSetChanged()
    }


}