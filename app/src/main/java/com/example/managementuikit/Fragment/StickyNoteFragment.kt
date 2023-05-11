package com.example.managementuikit.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.managementuikit.Activity.CreateTaskActivity
import com.example.managementuikit.Activity.MessagesActivity
import com.example.managementuikit.Activity.SplashActivity.Companion.auth
import com.example.managementuikit.Activity.SplashActivity.Companion.myRefUser
import com.example.managementuikit.Adapter.UserRecycleViewAdapter
import com.example.managementuikit.Model.User
import com.example.managementuikit.databinding.FragmentStickyNoteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class StickyNoteFragment : Fragment() {
    private lateinit var binding: FragmentStickyNoteBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentStickyNoteBinding.inflate(inflater, container, false)
        val adapter = UserRecycleViewAdapter(requireContext(), onItemClick)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewStickyNote.layoutManager = linearLayoutManager
        binding.recyclerViewStickyNote.adapter = adapter
        myRefUser.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val newUserList: ArrayList<User> = ArrayList()
                snapshot.children.forEach {
                    if (it.child("userID").value != auth.uid){
                        val mUser = User(
                            it.child("avatar").value.toString(),
                            it.child("userID").value.toString(),
                            it.child("name").value.toString()
                        )
                        newUserList.add(mUser)
                    }
                }
                binding.progressBarStickyNote.visibility = View.GONE
                if (newUserList.size == 0){
                    binding.imageViewNotFoundStickyNote.visibility = View.VISIBLE
                }else {
                    adapter.setUsers(newUserList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Load User Failed", Toast.LENGTH_SHORT).show()
            }
        })
        return binding.root
    }
    private val onItemClick: (User) -> Unit = {
        val intent = Intent(context, MessagesActivity::class.java)
        intent.putExtra("userChatTo", it)
        startActivity(intent)
    }
}