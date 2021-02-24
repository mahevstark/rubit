package net.trejj.talk.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import net.trejj.talk.Function;
import net.trejj.talk.R;
import net.trejj.talk.activities.main.MainActivity;
import net.trejj.talk.model.ContactInfo;
import net.trejj.talk.model.realms.User;
import net.trejj.talk.utils.RealmHelper;
import net.trejj.talk.utils.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/** Created by AwsmCreators * */
public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<ContactInfo> contactList;
    private List<ContactInfo> contactListFiltered;
    private List<User> usersList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone, invite;
        RelativeLayout itemclick;
        CircleImageView image;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.displayName);
            phone = view.findViewById(R.id.phoneNumber);
            invite = view.findViewById(R.id.invite);
            itemclick = view.findViewById(R.id.item_click);
            image = view.findViewById(R.id.image);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    //listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public ContactAdapter(Context context, List<ContactInfo> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.contactListFiltered = contactList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        usersList = RealmHelper.getInstance().getListOfUsers();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_info, parent, false);

        return new MyViewHolder(itemView);
    }

    public ExistsResult doesItExist(ContactInfo contact)
    {
        Boolean exists = false;
        String image = "";
        for(int i =0; i<usersList.size(); i++)
        {
            if(usersList.get(i).getPhone().equals(contact.getNumber()))
            {
                exists = true;
                image = usersList.get(i).getThumbImg();
                break;
            }
        }
        return new ExistsResult(exists, image);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final ContactInfo contact = contactListFiltered.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getNumber());
        ExistsResult exists = doesItExist(contact);

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            holder.invite.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.invite.setTextColor(context.getResources().getColor(R.color.black));
            holder.name.setTextColor(context.getResources().getColor(R.color.white));
            holder.phone.setTextColor(context.getResources().getColor(R.color.white));
            holder.itemclick.setBackgroundColor(context.getResources().getColor(R.color.black));
        }
        if(exists.getExists()) {
            holder.invite.setVisibility(View.GONE);
            GlideApp.with(context).load(exists.getImage()).into(holder.image);
        }else{
            holder.invite.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.drawable.profile_avatar);
        }
        holder.itemclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(contact.getName(),contact.getNumber());
            }
        });
        holder.invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("smsto:"+contact.getNumber());
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", context.getString(R.string.msg_body)+" "+"https://play.google.com/store/apps/details?id=" + context.getPackageName());
                    context.startActivity(it);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = contactList;
                } else {
                    List<ContactInfo> filteredList = new ArrayList<>();
                    for (ContactInfo row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getNumber().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<ContactInfo>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    private void showDialog(String Contactname, String Contactnumber) {
        Dialog callOptionDialog = new Dialog(context);
        callOptionDialog.setContentView(R.layout.call_option_dialog);
        callOptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable
                (Color.TRANSPARENT));
        ImageView call, back, chat;
        TextView name, number, charges;
        call = (ImageView) callOptionDialog.findViewById
                (R.id.call);
        back = (ImageView) callOptionDialog.findViewById(R.id.back);
        chat = callOptionDialog.findViewById(R.id.chat);
        name = callOptionDialog.findViewById(R.id.contact_name);
        number = callOptionDialog.findViewById(R.id.contact_number);
        charges = callOptionDialog.findViewById(R.id.charges);
        name.setText(Contactname);
        number.setText(Contactnumber);
        String callrate = Function.checkCountry(Contactnumber);
        if (Contactnumber.startsWith("+")){
            charges.setText("Call charges: "+callrate+" credits/min");
        }else {
            charges.setText("Call charges: Unknown/min");
        }
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).CallNumber(Contactnumber,Contactname);
                callOptionDialog.dismiss();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOptionDialog.dismiss();
            }
        });


        callOptionDialog.setCancelable(false);
        callOptionDialog.show();
    }
}
final class ExistsResult {
    private final Boolean exists;
    private final String image;

    public ExistsResult(Boolean exists, String image) {
        this.exists = exists;
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public Boolean getExists() {
        return exists;
    }
}