package com.example.managementuikit.OnBoard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.managementuikit.Activity.SignInActivity
import com.example.managementuikit.OnBoard.Adapter.ViewPagerAdapter
import com.example.managementuikit.R
import com.example.managementuikit.databinding.ActivityOnBoardingBinding


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding:ActivityOnBoardingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback(){
            @SuppressLint("ResourceAsColor")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == 0){
                    binding.tvBackObf2a?.visibility = View.GONE
                    binding.cvDot1?.setCardBackgroundColor(R.color.background_color)
                    binding.cvDot2?.setCardBackgroundColor(R.color.grey)
                    binding.cvDot1?.updateLayoutParams{width = convertDpToPixel(16, applicationContext).toInt() }
                    binding.cvDot2?.updateLayoutParams{width = convertDpToPixel(8, applicationContext).toInt() }
                } else{
                    binding.tvBackObf2a?.visibility = View.VISIBLE
                    binding.cvDot1?.setCardBackgroundColor(R.color.grey)
                    binding.cvDot2?.setCardBackgroundColor(R.color.background_color)
                    binding.cvDot1?.updateLayoutParams{width = convertDpToPixel(8, applicationContext).toInt() }
                    binding.cvDot2?.updateLayoutParams{width = convertDpToPixel(16, applicationContext).toInt() }
                }
            }

        })
        setOnClick()
    }
    fun convertDpToPixel(dp: Int, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
    @SuppressLint("CommitPrefEdits")
    fun setOnClick(){
        binding.btnSkipOnBoarding.setOnClickListener {

            remember()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
        binding.cvNext.setOnClickListener {
            when(binding.viewPager.currentItem){
                0 -> binding.viewPager.currentItem = 1
                1 -> {
                    remember()
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                }
            }

        }
        binding.tvBackObf2a?.setOnClickListener {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
        }

    }

    private fun remember() {
        val sharedPreferences = getSharedPreferences("SHARED_PREFERENCES_NAME", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }
}