package net.trejj.talk.adapters.messaging.holders.base

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.trejj.talk.R
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.QuotedMessage
import net.trejj.talk.model.realms.User
import net.trejj.talk.utils.AdapterHelper
import net.trejj.talk.utils.MessageTypeHelper

open class BaseSentHolder(context: Context, itemView: View) : BaseHolder(context,itemView) {

    var messageStatImg:ImageView? = itemView.findViewById(R.id.message_stat_img)


    override fun bind(message: Message, user: User) {
        super.bind(message, user)


        //imgStat (received or read)
        messageStatImg?.setImageResource(AdapterHelper.getMessageStatDrawable(message.messageStat))


    }




}

