/*
 * Created by Devlomi on 2020
 */

package net.trejj.rubytalksense.activities.calling.model;

public class EngineConfig {
    public int mUid;

    public String mChannel;

    public void reset() {
        mChannel = null;
    }

    public EngineConfig() {
    }
}
