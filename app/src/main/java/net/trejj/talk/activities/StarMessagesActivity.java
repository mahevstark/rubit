/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.trejj.talk.R;
import net.trejj.talk.adapters.StarMessageAdapter;
import net.trejj.talk.model.realms.Message;
import net.trejj.talk.model.realms.StarMessage;
import net.trejj.talk.utils.RealmHelper;

import java.util.ArrayList;
import java.util.List;

public class StarMessagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StarMessageAdapter adapter;
    private List<Message> messageList;

    private List<StarMessage> starMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_messages);

        getStarMessages();

        recyclerView = findViewById(R.id.starMessageRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setHasFixedSize(true);


    }

    private void getMessagesData(String id) {
        Message message = RealmHelper.getInstance().getMessage(id);

        Log.i("starMessages",message.getContent());
        messageList.add(message);
        adapter = new StarMessageAdapter(this,messageList);
        recyclerView.setAdapter(adapter);
    }

    private void getStarMessages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("starMessages");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList = new ArrayList<>();
                starMessages = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    starMessages.add(new StarMessage(dataSnapshot.child("uid").getValue().toString(),
                            dataSnapshot.child("messageId").getValue().toString()));
                }

                for(int i=0;i<starMessages.size();i++){
                    getMessagesData(starMessages.get(i).getMessageId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}