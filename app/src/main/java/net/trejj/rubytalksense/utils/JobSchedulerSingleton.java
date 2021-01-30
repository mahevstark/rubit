package net.trejj.rubytalksense.utils;

import android.app.job.JobScheduler;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import net.trejj.rubytalksense.utils.MyApp;

public class JobSchedulerSingleton {
    private static JobScheduler jobScheduler;

    private JobSchedulerSingleton() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static JobScheduler getInstance(){
        if (jobScheduler == null){
            jobScheduler = (JobScheduler) MyApp.context().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        return jobScheduler;
    }
}
