package com.github.okwrtdsh.alarmtest

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import butterknife.bindView
import java.util.*

class MainActivity : AppCompatActivity() {

    val editText: EditText by bindView(R.id.edit_text)
    val timePicker: TimePicker by bindView(R.id.time_picker)
    val buttonSet: Button by bindView(R.id.button_set)
    val buttonCancel: Button by bindView(R.id.button_cancel)
    val relativeLayout: RelativeLayout by bindView(R.id.activity_main)
    var notificationId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker.setIs24HourView(true)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        buttonSet.setOnClickListener {
            if (editText.text.isBlank()) {
                Toast.makeText(applicationContext, "Title is Required!!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    set(Calendar.MINUTE, timePicker.minute)
                    set(Calendar.SECOND, 0)
                }.timeInMillis,
                PendingIntent.getBroadcast(
                    applicationContext,
                    0,
                    Intent(applicationContext, AlarmBroadcastReceiver::class.java).apply {
                        putExtra("notificationId", ++notificationId)
                        putExtra("reminder", editText.text)
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            )
            Toast.makeText(applicationContext, "SET!! ${editText.text}", Toast.LENGTH_SHORT).show()
            reset()
        }

        buttonCancel.setOnClickListener {
            alarmManager.cancel(
                PendingIntent.getBroadcast(
                    applicationContext, 0, Intent(applicationContext, AlarmBroadcastReceiver::class.java), 0))
            Toast.makeText(applicationContext, "CANCEL!!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(relativeLayout.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        relativeLayout.requestFocus()
        return super.onTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        reset()
    }

    private fun reset() {
        timePicker.apply {
            val now = Calendar.getInstance()
            hour = now.get(Calendar.HOUR_OF_DAY)
            minute = now.get(Calendar.MINUTE)
        }
        editText.setText("")
    }
}
