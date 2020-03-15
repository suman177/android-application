package com.example.appstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import static com.example.appstone.PaginationListener.PAGE_START;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference RootRef=database.getReference();
    GoogleSignInClient mGoogleSignInClient;


    private String  messageSenderID;

    private Button SendMessageButton;

    private EditText MessageInputText;

    private List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;

    //PAGINATION

//    private int currentPage = PAGE_START;
//    private boolean isLastPage = false;
//    private int totalPage = 10;
//    private boolean isLoading = false;
//    int itemCount = 0;



    Boolean isScrolling=false;

    int currentItem,totalItems,scrolledOutItems;


    //TextView textView= findViewById(R.id.view);

    private String saveCurrentTime, saveCurrentDate;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth=FirebaseAuth.getInstance();

        messageSenderID = mAuth.getCurrentUser().getUid();

        IntializeControllers();

        SendMessageButton=findViewById(R.id.send_message_btn);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendMessage();
            }
        });





    }

    //THIS METHOD WILL INITIALIZE ALL THE CONTROLLERS AND ALSO SET TIME AND DATE
    private void IntializeControllers()
    {

        MessageInputText =  findViewById(R.id.input_message);
        messageAdapter = new MessageAdapter(messagesList);


        userMessagesList = findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("hh-mm-sss a");
        saveCurrentTime = currentTime.format(calendar.getTime());




    }
    //METHOD FOR GOOGLE SIGN OUT
    private void signOut(){
                mGoogleSignInClient.signOut();
                Toast.makeText(ChatActivity.this,"You are Logged Out",Toast.LENGTH_SHORT).show();
                finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.drop_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //FIRST METHOD TO LOAD WHEN THE ACTIVITY INITIALIZES
    @Override
    protected void onStart() {
        super.onStart();


        RootRef.child("Chat").child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages=dataSnapshot.getValue(Messages.class);
                messages.setKey(dataSnapshot.getKey());
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

        //to send text message
    private void SendMessage()
    {
        final String messageText = MessageInputText.getText().toString();



        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message..."+messageSenderID, Toast.LENGTH_LONG).show();
        }
        else
        {
            String messageSenderRef = "Chat/"+ "Messages/";
            String userEmail= mAuth.getCurrentUser().getEmail();

            DatabaseReference userMessageKeyRef = RootRef.child("users")
                    .child(messageSenderID).push();


            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("from", userEmail);
            messageTextBody.put("timeStamp", saveCurrentDate+"T"+saveCurrentTime);

            String messagePushID = userMessageKeyRef.getKey();

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef+"/"+messagePushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }


}
