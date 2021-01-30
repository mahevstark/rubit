package net.trejj.rubytalksense.utils;

import android.app.job.JobInfo;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;

import net.trejj.rubytalksense.model.constants.PendingGroupTypes;
import net.trejj.rubytalksense.model.realms.Message;
import net.trejj.rubytalksense.model.realms.PendingGroupJob;
import net.trejj.rubytalksense.model.realms.UnUpdatedStat;
import net.trejj.rubytalksense.model.realms.UnUpdatedVoiceMessageStat;
import net.trejj.rubytalksense.utils.JobSchedulerSingleton;
import net.trejj.rubytalksense.utils.RealmHelper;
import net.trejj.rubytalksense.utils.ServiceHelper;

import io.realm.RealmResults;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UnProcessedJobs {

    public static void process(Context context) {


        RealmResults<Message> unProcessedNetworkRequests = net.trejj.rubytalksense.utils.RealmHelper.getInstance().getUnProcessedNetworkRequests();
        for (Message unProcessedNetworkRequest : unProcessedNetworkRequests) {
            if (!doesJobExists(unProcessedNetworkRequest.getMessageId(), false)) {
                net.trejj.rubytalksense.utils.ServiceHelper.startNetworkRequest(context, unProcessedNetworkRequest.getMessageId(), unProcessedNetworkRequest.getChatId());

            }
        }

        for (UnUpdatedVoiceMessageStat unUpdatedVoiceMessageStat : net.trejj.rubytalksense.utils.RealmHelper.getInstance().getUnUpdatedVoiceMessageStat()) {
            if (!doesJobExists(unUpdatedVoiceMessageStat.getMessageId(), true)) {
                net.trejj.rubytalksense.utils.ServiceHelper.startUpdateVoiceMessageStatRequest(context, unUpdatedVoiceMessageStat.getMessageId(), null, unUpdatedVoiceMessageStat.getMyUid());
            }
        }

        for (UnUpdatedStat unUpdatedStat : net.trejj.rubytalksense.utils.RealmHelper.getInstance().getUnUpdateMessageStat()) {

            if (!doesJobExists(unUpdatedStat.getMessageId(), false)) {
                net.trejj.rubytalksense.utils.ServiceHelper.startUpdateMessageStatRequest(context, unUpdatedStat.getMessageId(), unUpdatedStat.getMyUid(), null, unUpdatedStat.getStatToBeUpdated());
            }

        }

        for (PendingGroupJob pendingGroupJob : net.trejj.rubytalksense.utils.RealmHelper.getInstance().getPendingGroupCreationJobs()) {
            String groupId = pendingGroupJob.getGroupId();
            if (!doesJobExists(groupId, false)) {
                if (pendingGroupJob.getType() == PendingGroupTypes.CHANGE_EVENT) {
                    net.trejj.rubytalksense.utils.ServiceHelper.updateGroupInfo(context, pendingGroupJob.getGroupId(), pendingGroupJob.getGroupEvent());
                } else {
                    ServiceHelper.fetchAndCreateGroup(context, groupId);
                }
            }
        }

    }

    private static boolean doesJobExists(String id, boolean isVoiceMessage) {
        int jobId = RealmHelper.getInstance().getJobId(id, isVoiceMessage);
        if (jobId == -1)
            return false;
        for (JobInfo jobInfo : JobSchedulerSingleton.getInstance().getAllPendingJobs()) {
            if (jobInfo.getId() == jobId)
                return true;
        }
        return false;
    }

}
