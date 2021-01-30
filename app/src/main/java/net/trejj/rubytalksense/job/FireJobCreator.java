package net.trejj.rubytalksense.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.trejj.rubytalksense.job.DailyBackupJob;
import net.trejj.rubytalksense.job.DeleteStatusJob;
import net.trejj.rubytalksense.job.JobIds;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class FireJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {

//            case JobIds.JOB_TAG_SYNC_CONTACTS:
//                return new SyncContactsDailyJob();

            case net.trejj.rubytalksense.job.JobIds.JOB_TAG_DELETE_STATUS:
                return new DeleteStatusJob();


            case JobIds.JOB_TAG_BACKUP_MESSAGES:
                return new DailyBackupJob();

        }
        return null;
    }
}
