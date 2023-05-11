package com.example.managementuikit.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.managementuikit.Activity.MessagesActivity
import com.example.managementuikit.Activity.SignInActivity
import com.example.managementuikit.Activity.SplashActivity.Companion.auth
import com.example.managementuikit.Activity.SplashActivity.Companion.isLoggedIn
import com.example.managementuikit.databinding.FragmentProfileBinding
import com.facebook.login.LoginManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.layoutCallFragmentProfile.setOnClickListener {
            if (isLoggedIn()){
                LoginManager.getInstance().logOut()
                Toast.makeText(context, "Logout successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
            }
            if (auth.currentUser!=null){
                Firebase.auth.signOut()
                Toast.makeText(context, "Logout successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, SignInActivity::class.java)
                startActivity(intent)
            }
        }
        binding.btnBackFragmentProfile.setOnClickListener {
            @Suppress("DEPRECATION")
            activity?.onBackPressed()
        }
        val sharedPreferences = activity?.getSharedPreferences("SHARED_PREFERENCES_USER", AppCompatActivity.MODE_PRIVATE)
        binding.nameFragmentProfile.text =  sharedPreferences!!.getString("name", "Unknown")
        binding.avatarFragmentProfile.load(sharedPreferences.getString("avatar", "Unknown"))
        return binding.root
    }

}