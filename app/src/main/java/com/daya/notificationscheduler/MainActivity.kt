package com.daya.notificationscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.view.View
import android.widget.SeekBar
import android.widget.Toast


class MainActivity : AppCompatActivity() {

    private  var mScheduler: JobScheduler? = null

    companion object{
        const val JOB_ID = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, i: Int, b: Boolean) {
                if (i > 0){
                    seekBarProgress.text = " $i +  s"
                }else {
                    seekBarProgress.text = "Not Set"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    fun scheduleJob(v : View) {
        mScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler

        val selectedNetworkId :Int = networkOptions.checkedRadioButtonId

        val selectedNetworkOption: Int = when (selectedNetworkId) {
            R.id.anyNetwork -> JobInfo.NETWORK_TYPE_ANY
            R.id.wifiNetwork -> JobInfo.NETWORK_TYPE_UNMETERED

            else -> JobInfo.NETWORK_TYPE_NONE
        }

        val seekbarInteger :Int = seekBar.progress
        val seekbarSet :Boolean = seekbarInteger > 0

        val serviceName = ComponentName(packageName,
            NotificationJobService::class.java.name
            )

        val builder = JobInfo.Builder(JOB_ID, serviceName)
            .setRequiredNetworkType(selectedNetworkId)
            .setRequiresDeviceIdle(idleSwitch.isChecked)
            .setRequiresCharging(chargingSwitch.isChecked)
        if (seekbarSet) {
            builder.setOverrideDeadline((seekbarInteger * 1000).toLong())
        }

        val constrainSet = selectedNetworkOption != JobInfo.NETWORK_TYPE_NONE
                || chargingSwitch.isChecked || idleSwitch.isChecked
                ||seekbarSet

        if (constrainSet) {
            val myJobInfo = builder.build()
            mScheduler!!.schedule(myJobInfo)

            Toast.makeText(
                this, "Job Scheduled, job will run when " +
                        "the constraints are met.", Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                this, "Please set at least one constraint",
                Toast.LENGTH_SHORT
            ).show()

        }

    }

    fun cancelJobs() {
        if (mScheduler != null) {
            mScheduler!!.cancelAll()
            mScheduler = null
            Toast.makeText(this, "Jobs cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
