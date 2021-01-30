package net.trejj.rubytalksense.adapters.messaging.holders.base

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import net.trejj.rubytalksense.R
import net.trejj.rubytalksense.model.realms.Message
import net.trejj.rubytalksense.model.realms.QuotedMessage
import net.trejj.rubytalksense.model.realms.User
import net.trejj.rubytalksense.utils.AdapterHelper
import net.trejj.rubytalksense.utils.MessageTypeHelper

open class BaseSentHolder(context: Context, itemView: View) : BaseHolder(context,itemView) {

    var messageStatImg:ImageView? = itemView.findViewById(R.id.message_stat_img)


    override fun bind(message: Message, user: User) {
        super.bind(message, user)


        //imgStat (received or read)
        messageStatImg?.setImageResource(AdapterHelper.getMessageStatDrawable(message.messageStat))


    }




}

