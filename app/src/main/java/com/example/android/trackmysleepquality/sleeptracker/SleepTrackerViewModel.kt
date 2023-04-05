package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,         // 1. to access data in the db via DAO class...
        application: Application                // 2. to access resources [strings, styles,..]
) : AndroidViewModel(application) {

        // 3. we need a factory to instantiate the ViewModel and provide it with the data source:
        // see: SleepTrackerViewModelFactory

}

