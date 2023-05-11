package com.example.managementuikit.Activity

import android.app.PendingIntent.*
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.managementuikit.Fragment.CalenderFragment
import com.example.managementuikit.Fragment.HomeFragment
import com.example.managementuikit.Fragment.ProfileFragment
import com.example.managementuikit.Fragment.StickyNoteFragment
import com.example.managementuikit.R
import com.example.managementuikit.databinding.ActivityHomeBinding
import com.facebook.*
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var id : Int = -1
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if( binding.bottomNavigationView.selectedItemId != R.id.home) {
            binding.bottomNavigationView.selectedItemId = R.id.home
        }
        else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null) {
            id = if (bundle.getString("id") != null){
                bundle.getString("id")!!.toInt()
            } else{
                val intent = intent
                intent.getIntExtra("Task_ID", -1)
            }
            if (id >= 0){
                val i1 = Intent(this, CreateTaskActivity::class.java)
                i1.putExtra("Task_ID",id)
                i1.putExtra("isCreate", false)
                startActivity(i1)
                id = -1
            }
        }
        replaceCalenderFragment(HomeFragment())
        binding.bottomAppBar.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    replaceCalenderFragment(HomeFragment())
                    true
                }
                R.id.send -> {
                    replaceCalenderFragment(StickyNoteFragment())
                    true
                }
                R.id.stickynote -> {
                    replaceCalenderFragment(CalenderFragment())
                    true
                }
                R.id.profile -> {
                    replaceCalenderFragment(ProfileFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }

        binding.fab.setOnClickListener {
            val intent2 = Intent(this, CreateTaskActivity::class.java)
            intent2.putExtra("isCreate", true)
            startActivity(intent2)
        }

    }

//    @SuppressLint("SimpleDateFormat")
//    private fun setAlarNotification() {
//        val arrayList: ArrayList<Task> = ArrayList()
//        val sDF = SimpleDateFormat("dd/MM/yyyy")
//        val sdfHOUR = SimpleDateFormat("HH:mm:ss")
//        taskViewModel.getAllTask().observe(this){iy->
//            arrayList.clear()
//            iy.forEach { iu->
//                if (sDF.format(iu.starts_time) == sDF.format(Date())
//                    && sdfHOUR.format(iu.starts_time) > sdfHOUR.format(Date())){
//                    arrayList.add(iu)
//                }
//            }
//            val intent = Intent(this, ServiceAlarm::class.java)
//            val bundle = Bundle()
//            bundle.putSerializable("listTask", arrayList)
//            intent.putExtras(bundle)
//            startService(intent)
//        }
//    }


    private fun replaceCalenderFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
        fragmentTransaction.addToBackStack("null")
        fragmentTransaction.commit()
    }

}