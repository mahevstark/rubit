package net.trejj.talk.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.trejj.talk.job.DailyBackupJob;
import net.trejj.talk.job.DeleteStatusJob;
import net.trejj.talk.job.JobIds;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class FireJobCreator implements JobCreator {
    @Nullable
    @Override
    public Job create(@NonNull String tag) {
        switch (tag) {

//            case JobIds.JOB_TAG_SYNC_CONTACTS:
//                return new SyncContactsDailyJob();

            case JobIds.JOB_TAG_DELETE_STATUS:
                return new DeleteStatusJob();


            case JobIds.JOB_TAG_BACKUP_MESSAGES:
                return new DailyBackupJob();

        }
        return null;
    }
}