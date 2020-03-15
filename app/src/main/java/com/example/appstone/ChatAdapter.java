package com.example.appstone;

import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;


    public MessageAdapter (List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText, receiverMessageText, receiverId, receiverTime;


        public MessageViewHolder(@NonNull View itemView)
        {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverId= itemView.findViewById(R.id.receiver_id);
            receiverTime=itemView.findViewById(R.id.receiver_time);
        }
    }




    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_layout, viewGroup, false);

        mAuth=FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, int i)
    {
        String messageSenderId = mAuth.getCurrentUser().getEmail();
        Messages messages = userMessagesList.get(i);
        String fromUserId= messages.getFrom();
        String fromMessageType = messages.getMessage();

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverTime.setVisibility(View.GONE);
        messageViewHolder.receiverId.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);

        if(fromMessageType!=null) {
            if (messageSenderId.equalsIgnoreCase(fromUserId)) {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);

                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n" + messages.getTimeStamp());
            }
            else{
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverTime.setVisibility(View.VISIBLE);
                messageViewHolder.receiverId.setVisibility(View.VISIBLE);

                messageViewHolder.receiverMessageText.setText(messages.getMessage()+"");
                messageViewHolder.receiverId.setText(messages.getFrom()+"");
                messageViewHolder.receiverTime.setText(messages.getTimeStamp()+"");

            }

        }


    }


    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }

}