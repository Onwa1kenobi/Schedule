package io.julius.schedule


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.text.DateFormatSymbols
import java.util.*


class ScheduleFragment : Fragment() {

    var appBarTitle = setupToolbarTitle(
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
        Calendar.getInstance().get(Calendar.MONTH),
        Calendar.getInstance().get(Calendar.YEAR)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            Navigation.findNavController(activity!!, R.id.navigation_host_fragment)
                .navigate(R.id.action_scheduleFragment_to_addEditScheduleFragment)
        }

        toolbar.setOnTouchListener { v, event ->
            true
        }

        appBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = appBarTitle
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    //careful there should a space between double quote otherwise it wont work
                    isShow = false
                }
            }
        })

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = calendarView.date

        setupToolbarTitle(
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )

        calendarView.setOnDateChangeListener { viewCalendar, year, month, dayOfMonth ->
            if (isDateInThePast(dayOfMonth, month, year)) {
                fab.hide()
            } else {
                fab.show()
            }
            setupToolbarTitle(dayOfMonth, month, year)
        }
    }

    private fun setupToolbarTitle(dayOfMonth: Int, month: Int, year: Int): String {
        val calendar = Calendar.getInstance()
        appBarTitle = if (calendar.get(Calendar.YEAR) != year) {
            "${DateFormatSymbols().months[month]} $dayOfMonth, $year"
        } else {
            "${DateFormatSymbols().months[month]} $dayOfMonth"
        }

        return appBarTitle
    }

    private fun isDateInThePast(dayOfMonth: Int, month: Int, year: Int): Boolean {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.YEAR, year)

        val reference = Calendar.getInstance()

        if (
            calendar.get(Calendar.DAY_OF_MONTH) == reference.get(Calendar.DAY_OF_MONTH) &&
            calendar.get(Calendar.MONTH) == reference.get(Calendar.MONTH) &&
            calendar.get(Calendar.YEAR) == reference.get(Calendar.YEAR)
        ) {
            // Same day, return false
            return false
        }

        return calendar.timeInMillis < reference.timeInMillis
    }
}
