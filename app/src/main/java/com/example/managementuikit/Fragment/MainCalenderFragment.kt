package com.example.managementuikit.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.managementuikit.R
import com.example.managementuikit.databinding.FragmentMainCalenderBinding
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class MainCalenderFragment : Fragment(){
    private lateinit var binding: FragmentMainCalenderBinding
 //   private val viewModel : HomeViewModel by activityViewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainCalenderBinding.inflate(layoutInflater)
        val arguments = arguments
        @Suppress("DEPRECATION")
        val desiredString = arguments?.getSerializable("string_key")
        if (desiredString!= null){
            desiredString as LocalDate
            replaceCalenderFragment(CalenderSingleRowFragment(), desiredString)
        } else{
            replaceCalenderFragment(CalenderSingleRowFragment(), Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
        }

        setOnclick()
        return binding.root
    }


    private fun replaceCalenderFragment(fragment: Fragment, _date: LocalDate) {
        val fragmentManager = activity?.supportFragmentManager
        val arguments = Bundle()
        arguments.putSerializable("string_key",_date)
        fragment.arguments = arguments
        val fragmentTransaction: FragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.addToBackStack("null")
        fragmentTransaction.commit()
    }
    private fun setOnclick() {
        binding.btnBackMainCalender.setOnClickListener {
            @Suppress("DEPRECATION")
            activity?.onBackPressed()
        }
    }

}