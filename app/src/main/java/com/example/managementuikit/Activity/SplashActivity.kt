package com.example.managementuikit.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.managementuikit.R
import com.example.managementuikit.databinding.ActivitySplashBinding
import com.example.managementuikit.OnBoard.OnBoardingActivity
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_NAME", MODE_PRIVATE)
        val userFirstLogin: Boolean  = sharedPreferences.getBoolean("isFirstTime", true)
        val animation: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.alpha)
        binding.tvSplashText.startAnimation(animation)
        val intent = Intent(this, OnBoardingActivity::class.java)
        val signInActivity = Intent(this, SignInActivity::class.java)
        val homeActivity = Intent(this, HomeActivity::class.java)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (userFirstLogin){
                    startActivity(intent)
                } else {
                    if (isLoggedIn() || auth.currentUser != null) startActivity(homeActivity)
                    else startActivity(signInActivity)
                }
                finish()
            }
            override fun onAnimationRepeat(animation: Animation) {}
        })
        //fwahflaw
        Firebase.messaging.subscribeToTopic("weather")
    }

    companion object{
        var auth: FirebaseAuth = Firebase.auth
        var database = Firebase.database
        var myRefUser = database.getReference("Users")
        fun isLoggedIn(): Boolean {
            val accessToken = AccessToken.getCurrentAccessToken()
            return accessToken != null && !accessToken.isExpired
        }

    }
}