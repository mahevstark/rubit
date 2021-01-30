package net.trejj.rubytalksense.adapters.messaging.holders

import android.content.Context
import android.view.View
import net.trejj.rubytalksense.R
import net.trejj.rubytalksense.adapters.messaging.holders.base.BaseReceivedHolder
import net.trejj.rubytalksense.model.realms.Message
import net.trejj.rubytalksense.model.realms.User
import com.vanniktech.emoji.EmojiTextView

// received message with type text
class ReceivedTextHolder(context: Context, itemView: View) : BaseReceivedHolder(context,itemView) {

    private var tvMessageContent: EmojiTextView = itemView.findViewById(R.id.tv_message_content)

    override fun bind(message: Message,user: User) {
        super.bind(message,user)
        tvMessageContent.text = message.content
    }


}