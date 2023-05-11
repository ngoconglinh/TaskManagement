package com.example.managementuikit.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.managementuikit.Activity.SplashActivity.Companion.auth
import com.example.managementuikit.Adapter.MessengesRecycleViewAdapter
import com.example.managementuikit.Model.MessageUser
import com.example.managementuikit.Model.User
import com.example.managementuikit.R
import com.example.managementuikit.databinding.ActivityMessagesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.text.SimpleDateFormat
import java.util.*


class MessagesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessagesBinding
    private val SELECT_PICTURE = 200
    private lateinit var storageRef: StorageReference
    private var database = Firebase.database
    private var myRef = database.getReference("Messages")
    private lateinit var userChatTo: User
    private lateinit var path: String
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var adapter: MessengesRecycleViewAdapter
    @Suppress("DEPRECATION")
    private var handler: Handler = Handler()
    private var isLoading: Boolean = false
    private lateinit var newMessagesList: ArrayList<MessageUser>
    private lateinit var messagesListLoad: ArrayList<MessageUser>

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userChatTo = intent.getSerializableExtra("userChatTo") as User
        binding.textViewNameUserChatToMessageActivity.text = userChatTo.name
        newMessagesList= ArrayList()
        messagesListLoad= ArrayList()
        adapter = MessengesRecycleViewAdapter(this)
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        binding.recyclerViewMessenges.layoutManager = linearLayoutManager
        binding.recyclerViewMessenges.adapter = adapter
        binding.progressBarMessengesActivity.visibility = View.VISIBLE

        val array = arrayOf(auth.uid, userChatTo.userID)
        array.sort()
        path = "${array[0]}_X_${array[1]}"

        getMessages()
        binding.btnSendMessages.setOnClickListener {
            val input = binding.textInputMessages.text?.trim().toString()
            if (input.replace(" ", "").isNotEmpty()) {
                addMessages(Date().toString(), auth.uid.toString(), input)
            }
        }
        binding.btnAddImageMessages.setOnClickListener {
            val i = Intent()
            i.type = "image/*"
            i.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
        }
        binding.btnBackMessageActivity.setOnClickListener { onBackPressed() }

      //  getUserChatToStatus(userChatTo)
    }

//    private fun getUserChatToStatus(user: User){
//        when (user.status) {
//            "Online" -> {
//                binding.userStatusActivityMessage.background.setTint(ContextCompat.getColor(this, R.color.green))
//            }
//            "Offline" -> {
//                binding.userStatusActivityMessage.background.setTint(ContextCompat.getColor(this, R.color.red))
//            }
//            "Custom" -> {
//                binding.userStatusActivityMessage.background.setTint(ContextCompat.getColor(this, R.color.yellow_def))
//
//            }
//        }
//        binding.userStatusTextActivityMessage.text = user.status
//    }

    private fun getMessages() {
        myRef.child(path).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messagesListLoad.clear()
                newMessagesList.clear()
                dataSnapshot.children.forEach {
                    val mMessages = MessageUser(
                        it.child("date").value.toString(),
                        it.child("sender").value.toString(),
                        it.child("text").value.toString()
                    )
                    newMessagesList.add(mMessages)
                }
                if (newMessagesList.size >= 10){
                    sortAndAddListScroll()
                    iniScrollListener()
                } else {
                    sortAndAddListScroll()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun sortAndAddListScroll() {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy")
        sdf.timeZone = TimeZone.getTimeZone("GMT+07:00")
        for (i in 0 until newMessagesList.size - 1){
            for (j in i until newMessagesList.size){
                val temp: MessageUser
                if (sdf.parse(newMessagesList[i].date)!!.time < sdf.parse(newMessagesList[j].date)!!.time){
                    temp = newMessagesList[i]
                    newMessagesList[i] = newMessagesList[j]
                    newMessagesList[j] = temp
                }
            }
        }

        var i = 0
        var newMessagesListSize = newMessagesList.size
        if (newMessagesListSize >= 10){
            newMessagesListSize = 10
        }
        while (i < newMessagesListSize) {
            messagesListLoad.add(newMessagesList[i])
            i++
        }
        adapter.setMessage(messagesListLoad)
        binding.recyclerViewMessenges.smoothScrollToPosition(0)
        binding.progressBarMessengesActivity.visibility = View.GONE
    }

    private fun addMessages(date: String,sender: String,text: String) {
        val message = MessageUser(date, sender, text)
        binding.textInputMessages.setText("")
        myRef.child(path).child(Date().toString()).setValue(message)
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
                    Log.d("onActivityResult", "onActivityResult: $selectedImageUri")
                    storageRef = FirebaseStorage.getInstance().reference.child("Images").child(auth.uid + Date())
                    val uploadTask : UploadTask =storageRef.putFile(selectedImageUri)
                    uploadTask.addOnFailureListener {
                        Toast.makeText(this@MessagesActivity, "Add failed photo! Please try again later.", Toast.LENGTH_LONG).show()
                    }.addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { p0 ->
                            addMessages(Date().toString(), auth.uid.toString(), p0.toString())
                        }
                    }
                }
            }
        }
    }

    private fun iniScrollListener() {
        binding.recyclerViewMessenges.addOnScrollListener(object : RecyclerView.OnScrollListener()
        {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == messagesListLoad.size - 1) {
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadMore() {
        handler.post {
            messagesListLoad.add(MessageUser("", "",""))
            adapter.notifyItemInserted(messagesListLoad.size - 1)
        }

        handler.postDelayed({
            messagesListLoad.removeAt(messagesListLoad.size - 1)
            val scrollPosition = messagesListLoad.size
            adapter.notifyItemRemoved(scrollPosition )

            var currentSize = scrollPosition
            val nextLimit: Int = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                if (currentSize == newMessagesList.size){
                    Toast.makeText(this, "Loaded all messages", Toast.LENGTH_SHORT).show()
                    break
                }
                messagesListLoad.add(newMessagesList[currentSize])
                currentSize++
            }
            adapter.notifyDataSetChanged()
            isLoading = false

        },2000)
    }
}
