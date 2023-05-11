package com.example.managementuikit.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.managementuikit.Model.User
import com.example.managementuikit.R
import kotlinx.coroutines.*

class UserRecycleViewAdapter(private val context: Context, private val onClick: (User) -> Unit) :
    RecyclerView.Adapter<UserRecycleViewAdapter.UserCollapseViewHolder>() {

    private var listUser: List<User> = listOf()

    inner class UserCollapseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewName: TextView = itemView.findViewById(R.id.textview_name_user_layout)
        private val textViewUID: TextView = itemView.findViewById(R.id.textview_uid_user_layout)
        private val imageViewAvatar: ImageView = itemView.findViewById(R.id.imageView_avatar_user_layout)
        private val cardViewLayout: CardView = itemView.findViewById(R.id.card_view_layout_user_layout)
      //  private val userStatusUserLayout: View = itemView.findViewById(R.id.user_status_user_layout)
        fun onBind(user: User) {
            textViewName.text = user.name
            textViewUID.text = user.userID
            imageViewAvatar.load(user.avatar)
            cardViewLayout.setOnClickListener { onClick(user) }
//            when (user.status) {
//                "Online" -> userStatusUserLayout.background.setTint(ContextCompat.getColor(context, R.color.green))
//                "Offline" -> userStatusUserLayout.background.setTint(ContextCompat.getColor(context, R.color.red))
//                "Custom" -> userStatusUserLayout.background.setTint(ContextCompat.getColor(context, R.color.yellow_def))
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCollapseViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)

        return UserCollapseViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: UserCollapseViewHolder, position: Int) {
        holder.onBind(listUser[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setUsers(listUsers: List<User>) {
        this.listUser = listUsers
        notifyDataSetChanged()
    }
}