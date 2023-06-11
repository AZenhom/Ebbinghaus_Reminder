package com.ahmedzenhom.ebbinghaus.ui.main_screen

import android.Manifest
import android.Manifest.permission
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ahmedzenhom.ebbinghaus.R
import com.ahmedzenhom.ebbinghaus.ReminderReceiver
import com.ahmedzenhom.ebbinghaus.base.BaseActivity
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsModel
import com.ahmedzenhom.ebbinghaus.databinding.ActivityMainBinding
import com.ahmedzenhom.ebbinghaus.ui.dialogs.AddEventDialog
import com.ahmedzenhom.ebbinghaus.ui.dialogs.InfoDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()
    override val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var adapter: EventsAdapter

    override fun onActivityCreated() {
        initViews()
        initObservers()
    }

    private fun initViews() = with(binding) {
        // RV
        adapter = EventsAdapter { showDeleteEventSheet(it) }
        rvEvents.adapter = adapter
        // Click Listeners
        fabAdd.setOnClickListener { checkNotificationsPermission() }

    }

    private fun initObservers() {
        viewModel.eventsLiveData.observe(this) { adapter.submitList(it) }
    }

    private fun checkNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionDenied =
                ContextCompat.checkSelfPermission(this, permission.POST_NOTIFICATIONS) !=
                        PackageManager.PERMISSION_GRANTED
            if (permissionDenied) {
                showErrorMsg(getString(R.string.notification_permission_error))
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.POST_NOTIFICATIONS),
                    101
                )
                return
            }
        }
        showCreateNewEventDialog()
    }

    private fun showCreateNewEventDialog() {
        var addEventDialog: AddEventDialog? = null
        addEventDialog = AddEventDialog(onConfirm = { eventName, reminderNotes, reminderCount ->
            addEventDialog?.dismiss()
            createNewEvent(eventName, reminderNotes, reminderCount)
        })
        addEventDialog.show(supportFragmentManager, AddEventDialog.TAG)
    }

    private fun showDeleteEventSheet(event: EventModel) {
        var infoDialog: InfoDialog? = null
        infoDialog = InfoDialog(
            context = this,
            imageRes = R.drawable.ic_remove_event,
            message = getString(R.string.are_you_sure_you_want_to_delete_event),
            confirmText = getString(R.string.delete),
            onConfirm = {
                infoDialog?.dismiss()
                deleteEvent(event)
            },
            isCancelable = true
        )
        infoDialog.show(supportFragmentManager, InfoDialog.TAG)
    }

    private fun createNewEvent(title: String, description: String, slotsCount: Int) {
        val event = EventModel(
            id = Random.nextInt(1, 9999999),
            title = title,
            description = description,
            createdAt = Calendar.getInstance().timeInMillis,
        )
        calculateAndSetIntervals(event, slotsCount)
        viewModel.addNewEvent(event).observe(this) {
            if (!it) return@observe
            ReminderReceiver.scheduleReminders(this, event)
            showSuccessMsg(getString(R.string.event_added_successfully))
        }
    }

    private fun deleteEvent(event: EventModel) {
        viewModel.deleteEvent(event).observe(this) {
            if (!it) return@observe
            ReminderReceiver.cancelReminders(this, event)
            showSuccessMsg(getString(R.string.event_deleted_successfully))
        }
    }

    private fun calculateAndSetIntervals(event: EventModel, intervalsCount: Int) {
        val slotsList = mutableListOf<EventSlotsModel>()
        val baseInterval = TimeUnit.HOURS.toMillis(1)
        for (i in 0 until intervalsCount) {
            val slot = EventSlotsModel(
                id = Random.nextInt(1, 9999999),
                order = i,
                eventId = event.id,
                slotTime = event.createdAt + (baseInterval * (2.0.pow(i.toDouble())).toLong()),
            )
            slotsList.add(slot)
        }
        event.slots = slotsList
    }

}