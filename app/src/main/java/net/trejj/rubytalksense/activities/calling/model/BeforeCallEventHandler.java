/*
 * Created by Devlomi on 2020
 */

package net.trejj.rubytalksense.activities.calling.model;

import net.trejj.rubytalksense.activities.calling.model.AGEventHandler;

import io.agora.rtc.IRtcEngineEventHandler;

public interface BeforeCallEventHandler extends AGEventHandler {
    void onLastmileQuality(int quality);

    void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result);
}
