/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.trejj.talk.services.FCMRegistrationService;
import net.trejj.talk.services.MyFCMService;

public class OnBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                Intent fcmIntent = new Intent(context, FCMRegistrationService.class);
                context.startService(fcmIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Intent fcmIntent = new Intent(context, MyFCMService.class);
                context.startService(fcmIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
