/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk.model.realms;

import io.realm.annotations.PrimaryKey;

public class StarMessage {

    @PrimaryKey
    private String messageId;

    private String senderUid;
    private String receiverUid;
    private String content;
    private String timeStamp;
    private String chatId;

    public StarMessage(String messageId, String senderUid, String receiverUid, String content, String timeStamp, String chatId) {
        this.messageId = messageId;
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.content = content;
        this.timeStamp = timeStamp;
        this.chatId = chatId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
