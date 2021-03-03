package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.TextView
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.EmojiUtils
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import java.lang.Exception


// sent message with type text
class SentTextHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView) {
    private var tvMessageContent: EmojiTextView = itemView.findViewById(R.id.tv_message_content)
    private var edit_text: TextView = itemView.findViewById(R.id.edit_text)

    private var isEdited: String = "false";

    override fun bind(message: Message, user: User) {
        super.bind(message,user)

        val emojiInformation = EmojiUtils.emojiInformation(message.content)
        val res: Int

        res = if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size == 1) {
            R.dimen.emoji_size_single_emoji
        } else if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size > 1) {
            R.dimen.emoji_size_only_emojis
        } else {
            R.dimen.emoji_size_default
        }

//        try {
//            isEdited = message.content.split("/,./")[1]
//            isEdited = "true";
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
//
//        if(isEdited == "true") {
//            edit_text.visibility = View.VISIBLE
//        } else{
//            edit_text.visibility = View.GONE
//        }

        tvMessageContent.setEmojiSizeRes(res, false)
        tvMessageContent.text = message.content
    }

}

