package com.parse.starter;

/**
 * Created by chris on 7/13/17.
 */

public class MessagesDataModel {

    //Message content
    String messageContent;
    //Store the date it was created

    //Store the sendersUserName
    String messageSenderUserName;

    //Store the receiversUserName
    String messageReceiverUserName;
    //Message read or not read
    boolean messageReadStatus;

    //Message ReceiverObjectId
    String getMessageReceiverUserId;

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getMessageSenderUserName() {
        return messageSenderUserName;
    }

    public void setMessageSenderUserName(String messageSenderUserName) {
        this.messageSenderUserName = messageSenderUserName;
    }

    public String getMessageReceiverUserName() {
        return messageReceiverUserName;
    }

    public void setMessageReceiverUserName(String messageReceiverUserName) {
        this.messageReceiverUserName = messageReceiverUserName;
    }

    public boolean isMessageReadStatus() {
        return messageReadStatus;
    }

    public void setMessageReadStatus(boolean messageReadStatus) {
        this.messageReadStatus = messageReadStatus;
    }

    public String getGetMessageReceiverUserId() {
        return getMessageReceiverUserId;
    }

    public void setGetMessageReceiverUserId(String getMessageReceiverUserId) {
        this.getMessageReceiverUserId = getMessageReceiverUserId;
    }

    public MessagesDataModel(String messageContent, String messageSenderUserName, String messageReceiverUserName, boolean messageReadStatus, String getMessageReceiverUserId) {
        this.messageContent = messageContent;
        this.messageSenderUserName = messageSenderUserName;
        this.messageReceiverUserName = messageReceiverUserName;
        this.messageReadStatus = messageReadStatus;
        this.getMessageReceiverUserId = getMessageReceiverUserId;
    }







    //Default constructor
    public MessagesDataModel (){

    }
}
