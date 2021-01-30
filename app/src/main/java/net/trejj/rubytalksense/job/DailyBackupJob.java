package net.trejj.rubytalksense.job;

import androidx.annotation.NonNull;

import net.trejj.rubytalksense.job.JobIds;
import net.trejj.rubytalksense.utils.RealmBackupRestore;
import com.evernote.android.job.DailyJob;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

public class DailyBackupJob extends DailyJob {
    @NonNull
    @Override
    protected DailyJobResult onRunDailyJob(@NonNull Params params) {
        new RealmBackupRestore(null).backup();
        return DailyJobResult.SUCCESS;
    }

    public static void schedule() {
        //this will schedule a job to backup messages everyday between 2,3 AM
        DailyJob.schedule(new JobRequest.Builder(JobIds.JOB_TAG_BACKUP_MESSAGES), TimeUnit.HOURS.toMillis(2), TimeUnit.HOURS.toMillis(3));
    }
}
