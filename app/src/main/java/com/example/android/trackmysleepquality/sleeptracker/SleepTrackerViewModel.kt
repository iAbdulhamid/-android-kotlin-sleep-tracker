package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

class SleepTrackerViewModel(
        val database: SleepDatabaseDao,         // 1. to access data in the db via DAO class...
        application: Application                // 2. to access resources [strings, styles,..]
) : AndroidViewModel(application) {

        // 3. we need a factory to instantiate the ViewModel and provide it with the data source:
        // see: SleepTrackerViewModelFactory

        /* NO LONGER NEEDED: DEPRECATED!

        private var viewModelJob = Job()        // to manage all co-routines

        override fun onCleared() {
                super.onCleared()
                viewModelJob.cancel()           // cancel all co-routines when viewModel destroyed
        }

        // define a scope for co-routines to run within ...
        // Scope determine what thread the co-routine will run on.
        // Scope also needs to know about the job.
        private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
        // co-routine launched in the uiScope will run on the Main Thread.
        */

        private var tonight = MutableLiveData<SleepNight?>()
        private var nights = database.getAllNights()

        val nightsString = Transformations.map(nights) { nights ->
                formatNights(nights, application.resources)
        }

        init {
            initializeTonight()
        }

        private fun initializeTonight() {
                viewModelScope.launch {
                        tonight.value = getTonightFromDatabase()
                }
        }

        private suspend fun getTonightFromDatabase(): SleepNight? {
                return withContext(Dispatchers.IO) {
                        var night = database.getTonight()
                        if (night?.startTimeMilli != night?.endTimeMilli) {
                                night = null
                        }
                        night
                }
        }

        fun onStartTracking() {
                viewModelScope.launch {
                        val newNight = SleepNight()
                        insert(newNight)
                        tonight.value = getTonightFromDatabase()
                }
        }

        private suspend fun insert(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.insert(night)
                }
        }
        fun onStopTracking() {
                viewModelScope.launch {
                        val oldNight = tonight.value ?: return@launch
                        oldNight.endTimeMilli = System.currentTimeMillis()
                        update(oldNight)
                }
        }
        private suspend fun update(night: SleepNight) {
                withContext(Dispatchers.IO) {
                        database.update(night)
                }
        }
        fun onClear() {
                viewModelScope.launch {
                        clear()
                        tonight.value = null
                }
        }
        private suspend fun clear() {
                withContext(Dispatchers.IO) {
                        database.clear()
                }
        }






}

