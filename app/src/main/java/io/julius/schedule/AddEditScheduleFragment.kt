package io.julius.schedule


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.julius.schedule.data.model.Schedule
import io.julius.schedule.viewmodel.ScheduleViewModel
import kotlinx.android.synthetic.main.fragment_add_edit_schedule.*
import java.util.*

class AddEditScheduleFragment : Fragment() {

    lateinit var scheduleViewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProviders.of(activity!!).get(ScheduleViewModel::class.java)

        scheduleViewModel.scheduleViewContract.observe(this, Observer {
            it.getContentIfNotHandled()?.let { info ->
                when (info) {
                    is ScheduleViewContract.MessageDisplay -> {
                        // Display message
                        Toast.makeText(context, info.message, Toast.LENGTH_SHORT).show()
                    }

                    is ScheduleViewContract.SaveSuccess -> {
                        // Close fragment
                        findNavController().popBackStack()
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val safeArgs: AddEditScheduleFragmentArgs by navArgs()

        val schedule: Schedule? = safeArgs.schedule

        val currentDateInMillis: Long

        if (schedule == null) {
            currentDateInMillis = safeArgs.dateInMillis
            field_schedule_description.setText(safeArgs.scheduleDescription)
        } else {
            currentDateInMillis = schedule.timeInMillis
            field_schedule_description.setText(schedule.description)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = schedule.timeInMillis

            if (Build.VERSION.SDK_INT >= 23) {
                time_picker.hour = calendar.get(Calendar.HOUR)
                time_picker.minute = calendar.get(Calendar.MINUTE)
            } else {
                time_picker.currentHour = calendar.get(Calendar.HOUR)
                time_picker.currentMinute = calendar.get(Calendar.MINUTE)
            }
        }

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        button_done.setOnClickListener {
            layout_description_field_wrapper.isErrorEnabled = false

            if (field_schedule_description.text.toString().trim().isEmpty()) {
                layout_description_field_wrapper.error = "Please enter a description for your schedule"
            } else {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = currentDateInMillis

                var hour: Int
                val minute: Int

                // Variable to specify period of day.
                var period = Calendar.AM

                if (Build.VERSION.SDK_INT >= 23) {
                    hour = time_picker.hour
                    minute = time_picker.minute
                } else {
                    hour = time_picker.currentHour
                    minute = time_picker.currentMinute
                }

                Log.e("SCHEDULE", "HOUR : $hour     MINUTE : $minute")

                when {
                    hour == 0 -> {
                        hour = 12
                        period = Calendar.AM
                    }

                    hour > 12 -> {
                        hour -= 12
                        period = Calendar.PM
                    }

                    else -> {
                        hour = 12
                        period = Calendar.PM
                    }
                }

                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.AM_PM, period)

                if (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
                    // Time is in the past, show message
                    Toast.makeText(
                        context!!,
                        "You don't happen to have a time machine laying around do you?",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (schedule == null) {
                    // New schedule, call viewmodel to add
                    scheduleViewModel.saveSchedule(field_schedule_description.text.toString().trim(), calendar)
                } else {
                    // Existing schedule, call viewmodel to edit
                    scheduleViewModel.updateSchedule(
                        schedule,
                        field_schedule_description.text.toString().trim(),
                        calendar
                    )
                }
            }
        }
    }


}
