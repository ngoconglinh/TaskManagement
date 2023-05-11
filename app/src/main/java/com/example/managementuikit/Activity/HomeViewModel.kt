package com.example.managementuikit.Activity

import android.app.Application
import androidx.lifecycle.*
import java.util.Date
 class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var passingData = MutableLiveData<Date>()
    internal fun getDate(): LiveData<Date> = passingData

    internal fun passingData(data: Date){
            passingData.value = data
    }
}