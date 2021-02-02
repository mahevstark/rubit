package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseHolder
import net.trejj.talk.model.realms.GroupEvent
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User

 class GroupEventHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvGroupEvent: TextView = itemView.findViewById(R.id.tv_group_event)

     fun bind(message: Message,user: User){
         tvGroupEvent.text = GroupEvent.extractString(message.content, user.group.users)
     }


}