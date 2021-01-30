package net.trejj.rubytalksense.utils;

import android.content.Context;
import android.content.Intent;
import android.os.PersistableBundle;

import net.trejj.rubytalksense.job.NetworkJobService;
import net.trejj.rubytalksense.job.SaveTokenJob;
import net.trejj.rubytalksense.model.constants.MessageType;
import net.trejj.rubytalksense.model.realms.GroupEvent;
import net.trejj.rubytalksense.model.realms.Message;
import net.trejj.rubytalksense.model.realms.User;
import net.trejj.rubytalksense.services.AudioService;
import net.trejj.rubytalksense.services.FCMRegistrationService;
import net.trejj.rubytalksense.services.NetworkService;
import net.trejj.rubytalksense.utils.IntentUtils;
import net.trejj.rubytalksense.utils.MessageCreator;
import net.trejj.rubytalksense.utils.PBundleUtil;
import net.trejj.rubytalksense.utils.RealmHelper;
import net.trejj.rubytalksense.utils.Util;

/**
 * Created by Devlomi on 09/01/2018.
 */

//this will manage service starts and put extras
public class ServiceHelper {

    //this will fire a Network request (download or upload request or event a Firebase database operation)
    public static void startNetworkRequest(Context context, String messageId, String chatId) {
        String action = net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_NETWORK_REQUEST;

        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            PersistableBundle bundle = new net.trejj.rubytalksense.utils.PBundleUtil.Builder(messageId).action(action).messageId(messageId).chatId(chatId).build().getBundle();

            NetworkJobService.schedule(context, messageId, bundle);

        } else {
            Intent intent = new Intent(context, NetworkService.class);
            //set the action to identify the type
            intent.setAction(action);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MESSAGE_ID, messageId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_CHAT_ID, chatId);
            context.startService(intent);
        }
    }


    //this will update the received message stat and set it as Received or read in Firebase database
    public static void startUpdateMessageStatRequest(Context context, String messageId, String myUid, String chatId, int statToBeUpdated) {
        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            PersistableBundle bundle = new net.trejj.rubytalksense.utils.PBundleUtil.Builder(messageId).action(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_MESSAGE_STATE)
                    .messageId(messageId)
                    .myUid(myUid)
                    .chatId(chatId)
                    .stat(statToBeUpdated)
                    .build()
                    .getBundle();

            NetworkJobService.schedule(context, messageId, bundle);
        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_MESSAGE_STATE);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MESSAGE_ID, messageId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_CHAT_ID, chatId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MY_UID, myUid);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_STAT, statToBeUpdated);
            context.startService(intent);
        }
    }

    //this will update the received voice message stat and set it as seen in Firebase database
    public static void startUpdateVoiceMessageStatRequest(Context context, String messageId, String chatId, String myUid) {
        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            PersistableBundle bundle = new net.trejj.rubytalksense.utils.PBundleUtil.Builder(messageId)
                    .action(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_VOICE_MESSAGE_STATE)
                    .messageId(messageId)
                    .myUid(myUid)
                    .chatId(chatId)
                    .build()
                    .getBundle();

            NetworkJobService.schedule(context, messageId, bundle);


        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_VOICE_MESSAGE_STATE);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MESSAGE_ID, messageId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MY_UID, myUid);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_CHAT_ID, chatId);
            context.startService(intent);
        }
    }


    public static void fetchAndCreateGroup(Context context, String groupId) {
        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {

            PersistableBundle bundle = new net.trejj.rubytalksense.utils.PBundleUtil.Builder(groupId)
                    .action(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_FETCH_AND_CREATE_GROUP)
                    .groupId(groupId)
                    .build()
                    .getBundle();

            NetworkJobService.schedule(context, groupId, bundle);
        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_FETCH_AND_CREATE_GROUP);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_GROUP_ID, groupId);
            context.startService(intent);
        }
    }


    public static void setCallEnded(Context context, String callId, String otherUid, Boolean isIncoming) {

        String action = net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_SET_CALL_ENDED;

        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            PersistableBundle bundle = new PersistableBundle();

            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.CALL_ID, callId);
            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.ACTION_TYPE, action);
            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.OTHER_UID, otherUid);
            bundle.putBoolean(net.trejj.rubytalksense.utils.IntentUtils.IS_INCOMING, isIncoming);
            NetworkJobService.schedule(context, callId, bundle);

        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(action);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.CALL_ID, callId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.OTHER_UID, otherUid);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.IS_INCOMING, isIncoming);
            context.startService(intent);
        }

    }

    public static void setCallDeclinedForGroup(Context context, String callId,String groupId) {

        String action = net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_SET_CALL_DECLINED_FOR_GROUP;

        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            PersistableBundle bundle = new PersistableBundle();

            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.CALL_ID, callId);
            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.ACTION_TYPE, action);
            bundle.putString(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_GROUP_ID, groupId);
            NetworkJobService.schedule(context, callId, bundle);

        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(action);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.CALL_ID, callId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_GROUP_ID, groupId);

            context.startService(intent);
        }

    }

    public static void updateGroupInfo(Context context, String groupId, GroupEvent groupEvent) {
        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {

            PersistableBundle bundle = new net.trejj.rubytalksense.utils.PBundleUtil.Builder(groupId).action(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_GROUP).groupId(groupId).groupEvent(groupEvent).build().getBundle();
            NetworkJobService.schedule(context, groupId, bundle);

        } else {
            Intent intent = new Intent(context, NetworkService.class);
            intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_UPDATE_GROUP);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_GROUP_ID, groupId);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_GROUP_EVENT, groupEvent);
            context.startService(intent);
        }
    }


    public static void saveToken(Context context, String token) {
        if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {
            SaveTokenJob.schedule(context, token);
        } else {
            Intent intent = new Intent(context, FCMRegistrationService.class);
            intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.FCM_TOKEN, token);
            context.startService(intent);
        }
    }

    public static void handleReply(Context context, String uid, String chatId, String text) {
        User user = RealmHelper.getInstance().getUser(uid);
        if (user != null) {
            Message message = new MessageCreator.Builder(user, MessageType.SENT_TEXT).text(text).build();
            String messageId = message.getMessageId();
            String action = net.trejj.rubytalksense.utils.IntentUtils.INTENT_ACTION_HANDLE_REPLY;
            if (net.trejj.rubytalksense.utils.Util.isOreoOrAbove()) {

                PersistableBundle bundle = new PBundleUtil.Builder(messageId).action(action)
                        .messageId(messageId)
                        .chatId(chatId)
                        .build()
                        .getBundle();

                NetworkJobService.schedule(context, messageId, bundle);
            } else {
                Intent intent = new Intent(context, NetworkService.class);
                intent.setAction(action);
                intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_MESSAGE_ID, messageId);
                intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.EXTRA_CHAT_ID, chatId);
                context.startService(intent);
            }
        }

    }

    public static void playAudio(Context context, String id, String url, int pos, int progress) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.ACTION_START_PLAY);
        intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.ID, id)
                .putExtra(net.trejj.rubytalksense.utils.IntentUtils.URL, url)
                .putExtra(net.trejj.rubytalksense.utils.IntentUtils.POS, pos)
                .putExtra(net.trejj.rubytalksense.utils.IntentUtils.PROGRESS, progress);

        if (Util.isOreoOrAbove())
            context.startForegroundService(intent);
        else
            context.startService(intent);
    }

    public static void seekTo(Context context, String id, int progress) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.ACTION_SEEK_TO);
        intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.ID, id);
        intent.putExtra(net.trejj.rubytalksense.utils.IntentUtils.PROGRESS, progress);
        context.startService(intent);
    }


    public static void stopAudio(Context context) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(net.trejj.rubytalksense.utils.IntentUtils.ACTION_STOP_AUDIO);

        context.startService(intent);

    }

    public static void headsetStateChanged(Context context, int currentHeadsetState) {
        Intent intent = new Intent(context, AudioService.class).putExtra(IntentUtils.EXTRA_HEADSETSTATE_CHANGED, currentHeadsetState);
        context.startService(intent);

    }
}



