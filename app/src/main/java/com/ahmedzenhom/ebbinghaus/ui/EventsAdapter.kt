package com.ahmedzenhom.ebbinghaus.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ahmedzenhom.ebbinghaus.R
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsModel
import com.ahmedzenhom.ebbinghaus.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.*


class EventsAdapter constructor(
    private val onItemRemove: ((model: EventModel) -> Unit)? = null,
) : ListAdapter<EventModel, EventsAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: EventModel) {
            with(binding) {
                // Data
                tvEventTitle.text = item.title
                tvDate.text =
                    tvDate.context.getString(R.string.added_text, getFormattedDate(item.createdAt))
                tvDescription.text = item.description
                item.slots.forEach { flEventSLots.addView(inflateSlotView(it)) }
                // Click Listeners
                view2.setOnClickListener {
                    if (grExpanded.visibility == View.VISIBLE)
                        animateCollapse()
                    else
                        animateExpand()
                }

            }
        }

        private fun inflateSlotView(slotModel: EventSlotsModel): View {
            return LayoutInflater.from(itemView.context)
                .inflate(R.layout.item_event_slot, binding.root, false).apply {
                    findViewById<TextView>(R.id.tvEventSlot).text =
                        getFormattedDate(slotModel.slotTime)
                }
        }

        private fun animateExpand() = with(binding) {
            val rotate = RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 300
            rotate.fillAfter = true
            ivArrow.animation = rotate
            grExpanded.visibility = View.VISIBLE
        }

        private fun animateCollapse() = with(binding) {
            val rotate = RotateAnimation(180f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
            rotate.duration = 300
            rotate.fillAfter = true
            ivArrow.animation = rotate
            grExpanded.visibility = View.GONE
        }

        fun getFormattedDate(date: Long) =
            SimpleDateFormat("EEE mm-MM-yyyy hh:mm a", Locale.US).format(Date(date))
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventModel>() {
            override fun areItemsTheSame(
                oldItem: EventModel,
                newItem: EventModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: EventModel,
                newItem: EventModel
            ): Boolean =
                oldItem.id == newItem.id &&
                        oldItem.description == newItem.description &&
                        oldItem.createdAt == newItem.createdAt &&
                        areEventSlotTheSame(oldItem.slots, newItem.slots)

        }

        private fun areEventSlotTheSame(
            oldItems: List<EventSlotsModel>,
            newItems: List<EventSlotsModel>
        ): Boolean {
            if (oldItems.size != newItems.size)
                return false
            for (i in oldItems.indices)
                if (oldItems[i].order != newItems[i].order ||
                    oldItems[i].eventId != newItems[i].eventId ||
                    oldItems[i].slotTime != newItems[i].slotTime ||
                    oldItems[i].reminded != newItems[i].reminded
                ) return false
            return true
        }
    }

}