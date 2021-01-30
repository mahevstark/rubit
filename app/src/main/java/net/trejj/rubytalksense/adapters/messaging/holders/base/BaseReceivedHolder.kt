package net.trejj.rubytalksense.adapters.messaging.holders.base

import android.content.Context
import android.view.View
import android.widget.TextView
import net.trejj.rubytalksense.R
import net.trejj.rubytalksense.model.realms.Message
import net.trejj.rubytalksense.model.realms.User
import net.trejj.rubytalksense.utils.ListUtil

// received message holders
open class BaseReceivedHolder(context: Context, itemView: View) : BaseHolder(context, itemView) {
    var userName: TextView? = itemView.findViewById(R.id.tv_username_group)



    override fun bind(message: Message, user: User) {
        super.bind(message, user)

        if (user.isGroupBool && userName != null) {
            userName?.visibility = View.VISIBLE
            val fromId = message.fromId
            val userById = ListUtil.getUserById(fromId, user.getGroup().getUsers())
            if (userById != null) {
                val name = userById.userName
                if (name != null) userName?.text = name
            } else {
                userName?.text = message.fromPhone
            }
        }

    }



}
