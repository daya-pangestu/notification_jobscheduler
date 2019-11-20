package com.daya.notificationscheduler

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat

class NotificationJobService : JobService() {

    lateinit var mNotifyManager :NotificationManager

    //Notification channel ID
    companion object{

        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    override fun onStartJob(job: JobParameters?): Boolean {



        //define notification manager object
        mNotifyManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        //Create the notification channel
        createNotificationChannel()



        //Set up the notification content intent to launch the app when clicked
        val contentPendingIntent = PendingIntent.getActivity(
            this,0,Intent(this,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT
        )


        val builder = NotificationCompat.Builder(
            this, PRIMARY_CHANNEL_ID
        )
            .setContentTitle("Job Service")
            .setContentText("Your Job ran ti completion")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)

        mNotifyManager.notify(0,builder.build())

        return false
    }




    override fun onStopJob(job: JobParameters?): Boolean {

       return  true
    }


    private fun createNotificationChannel() {
        // Notification channels are only available in OREO and higher.
        // So, add a check on SDK version.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Job Service notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = "Notificaiton from Job Service"
            }

            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }
}