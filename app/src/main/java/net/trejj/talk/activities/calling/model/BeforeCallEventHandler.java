/*
 * Created by Devlomi on 2020
 */

package net.trejj.talk.activities.calling.model;

import net.trejj.talk.activities.calling.model.AGEventHandler;

import io.agora.rtc.IRtcEngineEventHandler;

public interface BeforeCallEventHandler extends AGEventHandler {
    void onLastmileQuality(int quality);

    void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result);
}
