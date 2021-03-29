/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk.model.realms;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StarMessage {
    public StarMessage() {
    }

    private String uid;
    private String messageId;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public StarMessage(String uid, String messageId) {
        this.uid = uid;
        this.messageId = messageId;
    }
}
