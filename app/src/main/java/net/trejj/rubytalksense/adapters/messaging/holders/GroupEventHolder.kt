package net.trejj.rubytalksense.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.trejj.rubytalksense.R
import net.trejj.rubytalksense.adapters.messaging.holders.base.BaseHolder
import net.trejj.rubytalksense.model.realms.GroupEvent
import net.trejj.rubytalksense.model.realms.Message
import net.trejj.rubytalksense.model.realms.User

 class GroupEventHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvGroupEvent: TextView = itemView.findViewById(R.id.tv_group_event)

     fun bind(message: Message,user: User){
         tvGroupEvent.text = GroupEvent.extractString(message.content, user.group.users)
     }


}