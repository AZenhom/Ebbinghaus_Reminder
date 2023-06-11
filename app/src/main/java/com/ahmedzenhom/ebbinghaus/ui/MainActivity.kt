package com.ahmedzenhom.ebbinghaus.ui

import androidx.activity.viewModels
import com.ahmedzenhom.ebbinghaus.base.BaseActivity
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsModel
import com.ahmedzenhom.ebbinghaus.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val viewModel: MainViewModel by viewModels()
    override val binding by viewBinding(ActivityMainBinding::inflate)

    private lateinit var adapter: EventsAdapter

    override fun onActivityCreated() {
        initViews()
    }

    private fun initViews() = with(binding) {
        adapter = EventsAdapter()
        rvEvents.adapter = adapter
        adapter.submitList(getFakeEvents())
    }

    private fun getFakeEvents() = listOf(
        EventModel(
            1,
            "Event 1",
            "This is event no. one",
            1686427437133L,
            listOf(
                EventSlotsModel(
                    id = 1,
                    order = 1,
                    eventId = 1,
                    1686427737133,
                    false
                ),
                EventSlotsModel(
                    id = 2,
                    order = 2,
                    eventId = 1,
                    1686427797133,
                    false
                ),
                EventSlotsModel(
                    id = 3,
                    order = 3,
                    eventId = 1,
                    1686448797133,
                    false
                ),

                )
        ),
        EventModel(
            2,
            "Event 2",
            "This is event no. two",
            1686427437133L,
            listOf(
                EventSlotsModel(
                    id = 4,
                    order = 1,
                    eventId = 2,
                    1686427737133,
                    false
                ),
                EventSlotsModel(
                    id = 5,
                    order = 2,
                    eventId = 2,
                    1686427797133,
                    false
                ),
                EventSlotsModel(
                    id = 6,
                    order = 3,
                    eventId = 2,
                    1686448797133,
                    false
                ),

                )
        )
    )

}