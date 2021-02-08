/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk;

public class Api {
    private String action;
    private String transId;

    public Api() {
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getAction() {
        return action;
    }

    public String getTransId() {
        return transId;
    }
}
