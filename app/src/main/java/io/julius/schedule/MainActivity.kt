package io.julius.schedule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.julius.schedule.data.ScheduleRepositoryImpl
import io.julius.schedule.data.cache.AppDatabase
import io.julius.schedule.data.cache.LocalDataSource
import io.julius.schedule.viewmodel.ScheduleViewModel
import io.julius.schedule.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create view model with activity scope so that hosted fragment will have access to the same instance
        ViewModelProviders.of(
            this, ViewModelFactory(
                ScheduleRepositoryImpl(LocalDataSource(AppDatabase.getInstance(applicationContext).appDao())),
                applicationContext
            )
        ).get(ScheduleViewModel::class.java)
    }
}
