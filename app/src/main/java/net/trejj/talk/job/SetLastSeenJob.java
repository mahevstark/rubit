package net.trejj.talk.job;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import net.trejj.talk.job.BaseJob;
import net.trejj.talk.job.JobIds;
import net.trejj.talk.model.constants.LastSeenStates;
import net.trejj.talk.utils.JobSchedulerSingleton;
import net.trejj.talk.utils.MyApp;
import net.trejj.talk.utils.SharedPreferencesManager;

import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SetLastSeenJob extends BaseJob {

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        int lastSeenState = SharedPreferencesManager.getLastSeenState();

        if (MyApp.isBaseActivityVisible() && lastSeenState != LastSeenStates.ONLINE) {
            disposables.add(fireManager.setOnlineStatus().subscribe(()->{
                jobFinished(jobParameters,false);
            }));
        } else if (!MyApp.isBaseActivityVisible() && lastSeenState != LastSeenStates.LAST_SEEN) {
            disposables.add(fireManager.setLastSeen().subscribe(()->{
                jobFinished(jobParameters,false);
            },throwable -> {

            }));
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        disposables.dispose();
        return false;
    }

    public static void schedule(Context context) {
        ComponentName component = new ComponentName(context, SetLastSeenJob.class);

        JobInfo.Builder builder = new JobInfo.Builder(JobIds.JOB_ID_SET_LAST_SEEN, component)
                .setPersisted(true)
                .setPeriodic(TimeUnit.MINUTES.toMillis(5))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);


        JobSchedulerSingleton.getInstance().schedule(builder.build());
    }

}
