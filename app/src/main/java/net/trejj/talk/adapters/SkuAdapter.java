package net.trejj.talk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.SkuDetails;

import net.trejj.talk.R;

import java.util.List;

import static net.trejj.talk.activities.EarnCreditsActivity.BIG_PACK_CREDITS;
import static net.trejj.talk.activities.EarnCreditsActivity.BIG_PACK_DESC;
import static net.trejj.talk.activities.EarnCreditsActivity.BIG_PACK_ID;
import static net.trejj.talk.activities.EarnCreditsActivity.MEDIUM_PACK_CREDITS;
import static net.trejj.talk.activities.EarnCreditsActivity.MEDIUM_PACK_DESC;
import static net.trejj.talk.activities.EarnCreditsActivity.MEDIUM_PACK_ID;
import static net.trejj.talk.activities.EarnCreditsActivity.SMALL_PACK_CREDITS;
import static net.trejj.talk.activities.EarnCreditsActivity.SMALL_PACK_DESC;
import static net.trejj.talk.activities.EarnCreditsActivity.SMALL_PACK_ID;


public class SkuAdapter extends RecyclerView.Adapter<SkuAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(SkuDetails item);
    }

    private final List<SkuDetails> items;
    private final OnItemClickListener listener;

    public SkuAdapter(List<SkuDetails> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credits, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener,position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, price, desc;
        private Button buy;

        public ViewHolder(View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.desc);
            name = (TextView) itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            buy = itemView.findViewById(R.id.buy);
        }

        public void bind(final SkuDetails item, final OnItemClickListener listener,int position) {

            if (position==0){
                name.setText(SMALL_PACK_CREDITS+" Credits");
                desc.setText(SMALL_PACK_DESC);
            }else if (position==1){
                name.setText(MEDIUM_PACK_CREDITS+" Credits");
                desc.setText(MEDIUM_PACK_DESC);
            }else if (position==2){
                name.setText(BIG_PACK_CREDITS+" Credits");
                desc.setText(BIG_PACK_DESC);
            }

            price.setText("Price "+item.getPrice());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //listener.onItemClick(item);
                }
            });
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}