/*
 * Created by Devlomi on 2021
 */

package net.trejj.talk.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.trejj.talk.R;
import net.trejj.talk.model.realms.Message;
import net.trejj.talk.utils.TimeHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StarMessageAdapter extends RecyclerView.Adapter<StarMessageAdapter.ViewHolder> {

    private Context context;
    private List<Message> messageList;

    public StarMessageAdapter(Context context, List<Message> messageList){
        this.context = context;
        this.messageList = messageList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.star_message_item, parent, false);
        return new ViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Message message = messageList.get(position);

        holder.messageDate.setText(message.getTime());

        if(message.getType()==3) {
            holder.imageView.setVisibility(View.GONE);
            holder.message.setVisibility(View.VISIBLE);
            holder.message.setText(message.getContent());
        }else if(message.getType()==2){
            holder.message.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(message.getContent()).into(holder.imageView);
        }

//        Date date = new Date(message.getTimestamp());
//        DateFormat f1 = new SimpleDateFormat("yyyy/MM/dd");

        holder.date.setText(TimeHelper.getDate(Long.parseLong(message.getTimestamp())));

        getSenderData(message.getFromId(), holder);
    }

    private void getSenderData(String fromId, ViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(fromId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    holder.userName.setText(snapshot.child("name").getValue().toString());
                    Glide.with(context).load(snapshot.child("photo").getValue().toString()).into(holder.userImage);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName, date, message, messageDate;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            userName = itemView.findViewById(R.id.username);
            date = itemView.findViewById(R.id.date);
            message = itemView.findViewById(R.id.tv_message_content);
            messageDate = itemView.findViewById(R.id.tv_time);
            imageView = itemView.findViewById(R.id.img_msg);

        }
    }
}
