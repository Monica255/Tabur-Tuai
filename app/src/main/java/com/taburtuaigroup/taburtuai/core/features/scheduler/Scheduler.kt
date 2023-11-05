package com.taburtuaigroup.taburtuai.core.features.scheduler

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.CallSuper
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.taburtuaigroup.taburtuai.MyApplication
import com.taburtuaigroup.taburtuai.R
import com.taburtuaigroup.taburtuai.core.data.Resource
import com.taburtuaigroup.taburtuai.core.domain.model.Mscheduler
import com.taburtuaigroup.taburtuai.core.domain.usecase.SmartFarmingUseCase
import com.taburtuaigroup.taburtuai.core.util.TextFormater
import com.taburtuaigroup.taburtuai.core.util.ToastUtil
import com.taburtuaigroup.taburtuai.ui.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject


private val SINGLE_EXECUTOR = Executors.newSingleThreadExecutor()

fun executeThread(f: () -> Unit) {
    SINGLE_EXECUTOR.execute(f)
}

//abstract class HiltBroadcastReceiver : BroadcastReceiver() {
//    @CallSuper
//    override fun onReceive(context: Context, intent: Intent) {
//    }
//}

@AndroidEntryPoint
class Scheduler : BroadcastReceiver() {
    @Inject
    lateinit var smartFarmingUseCase: SmartFarmingUseCase
    override fun onReceive(context: Context, intent: Intent) {
        executeThread {
            val data = intent.getParcelableExtra<Mscheduler>(EXTRA_DATA)
            data?.let {mScheduler->
                CoroutineScope(Dispatchers.Main).launch {
                    smartFarmingUseCase.updateDeviceState(mScheduler.id_kebun, mScheduler.id_device, mScheduler.action)
                        .collect {
                            when (it) {
                                is Resource.Error -> {
                                    showNotification(context, mScheduler, false)
                                }
                                is Resource.Success -> {
                                    showNotification(context, mScheduler, true)
                                }
                            }
                        }
                }
            }
        }
    }

    companion object {
        const val NOTIFICATION_CHANNEL_NAME = "SmartFarming Channel"
        const val NOTIFICATION_CHANNEL_ID = "notify-schedule"
        const val EXTRA_DATA = "extra data"
    }

    fun setScheduler(content: List<Mscheduler>) {
        Log.d("TAG","QQQQQQQQQQQQQ "+content[0].toString())
        val alarmManager = MyApplication.applicationContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(MyApplication.applicationContext(), Scheduler::class.java)
        for (content in content) {
            intent.putExtra(EXTRA_DATA, content)

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, content.hour)
            calendar.set(Calendar.MINUTE, content.minute)
            calendar.set(Calendar.SECOND, 0)


            val pendingIntent =
                PendingIntent.getBroadcast(
                    MyApplication.applicationContext(),
                    content.id_scheduler,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    fun cancelAlarm(context: Context, alarmID: Int) {
        val alarmManager = MyApplication.applicationContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(MyApplication.applicationContext(), Scheduler::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(
                MyApplication.applicationContext(),
                alarmID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)

    }

    fun cancelAllAlarm(context: Context, allAlarm: List<Mscheduler>) {
        Log.d("TAG", "cancel " + allAlarm)
        val alarmManager = MyApplication.applicationContext()
            .getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(MyApplication.applicationContext(), Scheduler::class.java)
        for (i in allAlarm) {
            val pendingIntent =
                PendingIntent.getBroadcast(
                    MyApplication.applicationContext(),
                    i.id_scheduler,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
    }

    fun showNotification(
        context: Context,
        content: Mscheduler,
        isSuccess: Boolean = false
    ) {
        val notificationStyle = NotificationCompat.InboxStyle()
        val action = if (content.action == 0) {
            "mematikan"
        } else if (content.action == 1) {
            "menyalakan"
        } else ""

        val timeString =
            if (isSuccess) context.resources.getString(R.string.notification_message_format) else context.resources.getString(
                R.string.notification_message_format_failed
            )

        val data = String.format(
            timeString,
            TextFormater.toClockFormat(
                content.hour,
                content.minute
            ),
            action,
            content.id_device,
            content.id_kebun,
            content.id_petani
        )
        notificationStyle.addLine(data)
        val num = System.currentTimeMillis().toInt()
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("Smart Farming")
                .setStyle(notificationStyle)
                .setContentIntent(getPendingIntent(context,num))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notification.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(num, notification.build())
    }

    @SuppressLint("WrongConstant")
    private fun getPendingIntent(context: Context,num:Int): PendingIntent? {
        val intent = Intent(context, HomeActivity::class.java)
        return TaskStackBuilder.create(MyApplication.applicationContext()).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(num, PendingIntent.FLAG_IMMUTABLE)
        }
//        return TaskStackBuilder.create(context).run {
//            addNextIntentWithParentStack(intent)
//            PendingIntent.getActivity(
//                MyApplication.applicationContext(),
//                num,
//                intent,
//                Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
//            )
//        }
    }
}