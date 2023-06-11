package com.ahmedzenhom.ebbinghaus.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ahmedzenhom.ebbinghaus.R
import com.ahmedzenhom.ebbinghaus.databinding.DialogAddEventLayoutBinding

class AddEventDialog constructor(
    private val onConfirm: ((eventName: String, reminderNotes: String, reminderCount: Int) -> Unit)? = null,
    private val onCancel: (() -> Unit)? = null,
) : DialogFragment() {

    companion object {
        const val TAG = "AddEventDialog"
    }

    private var _binding: DialogAddEventLayoutBinding? = null
    private val binding get() = _binding!!

    private var reminderCount: Int = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppDialogStyle)
        super.setCancelable(isCancelable ?: false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEventLayoutBinding.inflate(inflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        refreshReminderCountText()
        with(binding) {
            btnReminderCountMinus.setOnClickListener {
                if (reminderCount > 1) {
                    reminderCount--
                    refreshReminderCountText()
                }
            }
            btnReminderCountPlus.setOnClickListener {
                if (reminderCount < 15) {
                    reminderCount++
                    refreshReminderCountText()
                }
            }
            btnConfirm.setOnClickListener {
                val eventName = etEventName.text.toString().trim()
                val reminderNotes = etReminderNotes.text.toString().trim()
                if (eventName.isNotEmpty() && reminderNotes.isNotEmpty())
                    onConfirm?.invoke(eventName, reminderNotes, reminderCount)
            }
            btnCancel.setOnClickListener {
                if (onCancel == null)
                    dismiss()
                else
                    onCancel.invoke()
            }
        }
    }

    private fun refreshReminderCountText() {
        binding.tvReminderCount.text = reminderCount.toString()
    }
}