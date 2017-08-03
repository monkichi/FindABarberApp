package com.parse.starter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.findabarberapp.R;

import java.util.List;

/**
 * Created by chris on 7/14/17.
 */

public class MessagesListAdapter extends ArrayAdapter<MessagesDataModel> {

    Context appContext;
    List<MessagesDataModel> messagesInfoList;
    int itemLayoutId;

    public MessagesListAdapter(Context appContext, List<MessagesDataModel> followerInfoList, int itemLayoutId) {
        super(appContext, itemLayoutId, followerInfoList);
        this.appContext = appContext;
        this.messagesInfoList = followerInfoList;
        this.itemLayoutId = itemLayoutId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MessagesDataModel messageInfo;

        View view = convertView;
        MessageListItem viewHolder;

        //check if view is not already inflated
        if (view == null){
            //Get inflater from system service
            LayoutInflater inflater =(LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //inflate view layout from inflater
            view = inflater.inflate(itemLayoutId,parent,false);
            //instantiaate all the view widgets
            viewHolder = new MessageListItem();
            viewHolder.messageUserNameTextView = (TextView) view.findViewById(R.id.messageUserName);
            viewHolder.messageReadStatusCheckBox = (CheckBox) view.findViewById(R.id.messageReadCheckBox);

            view.setTag(viewHolder);
        }
        else{
            viewHolder = (MessageListItem) view.getTag();
        }
            messageInfo = messagesInfoList.get(position);
            viewHolder.messageUserNameTextView.setText(messageInfo.getMessageSenderUserName());
            if(messageInfo.isMessageReadStatus() == false){
                viewHolder.messageReadStatusCheckBox.setChecked(false);
                viewHolder.messageReadStatusCheckBox.setKeepScreenOn(true);
            }
            else{
                viewHolder.messageReadStatusCheckBox.setChecked(true);

            }


        return view;
    }


    private class MessageListItem {
        TextView messageUserNameTextView;
        CheckBox messageReadStatusCheckBox;


    }




}
