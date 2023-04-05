package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object {
        // allows clients to access the methods for creating or getting the database
        // without instantiating the class...
        // since the only purpose of this class is to provide us with a database,
        // there is no reason to ever instantiate it ...

        @Volatile   // make sure the value of INSTANCE is always up to date (the same) to all execution threads.
                    // INSTANCE will never be cached, all writes/reads will be done to/from the main memory.
                    // one thread changing -> changes will be visible to all other threads immediately.
        private var INSTANCE: SleepDatabase? = null     // will keep reference to the db once we have one
                                                        // this will avoid repeatedly opening connections to the db !

        fun getInstance(context: Context) : SleepDatabase {
            // multiple threads can potentially ask for the db instance at the same time !
            // synchronized: only one thread of execution at a time can enter this block of code...
            // to make sure the db only gets initialized once.
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_history_database"
                    ).fallbackToDestructiveMigration()
                        .build()  // // we will wipe and rebuild the db now, instead of migration.
                    INSTANCE = instance
                }
                return instance
            }
        }


    }
}
