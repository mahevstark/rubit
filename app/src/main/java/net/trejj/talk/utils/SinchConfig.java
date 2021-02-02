package net.trejj.talk.utils;

import android.content.Context;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;

import net.trejj.talk.R;
import net.trejj.talk.utils.network.FireManager;

public class SinchConfig {
    private static final String DEBUG_ENVIRONMENT = "sandbox.sinch.com";
    private static final String RELEASE_ENVIRONMENT = "clientapi.sinch.com";

    public static SinchClient getSinchClient(Context context) {
        return Sinch.getSinchClientBuilder()
                .context(context.getApplicationContext())
                .userId(FireManager.getUid())
                .applicationKey("4486eb1e-c7d7-4e3d-a488-eea5ca438194")
                .applicationSecret("FKpXpf+xEUOZCYhFfvgvbA==")
                .environmentHost(RELEASE_ENVIRONMENT)
                .build();
    }
}
