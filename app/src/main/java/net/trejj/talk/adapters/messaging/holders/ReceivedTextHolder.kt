package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseReceivedHolder
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import com.vanniktech.emoji.EmojiTextView

// received message with type text
class ReceivedTextHolder(context: Context, itemView: View) : BaseReceivedHolder(context,itemView) {

    private var tvMessageContent: EmojiTextView = itemView.findViewById(R.id.tv_message_content)
    private var edit_text: TextView = itemView.findViewById(R.id.edit_text)

    override fun bind(message: Message,user: User) {
        super.bind(message,user)
//        if(message.isEdited) {
//            edit_text.visibility = View.VISIBLE
//        } else{
//            edit_text.visibility = View.GONE
//        }
        tvMessageContent.text = message.content
    }


}