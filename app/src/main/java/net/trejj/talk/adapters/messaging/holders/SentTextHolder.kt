package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import com.vanniktech.emoji.EmojiTextView
import com.vanniktech.emoji.EmojiUtils
import net.trejj.talk.model.realms.StarMessage


// sent message with type text
class SentTextHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView) {
    private var tvMessageContent: EmojiTextView = itemView.findViewById(R.id.tv_message_content)

    override fun bind(message: Message, user: User, starMessage: ArrayList<String>) {
        super.bind(message,user,starMessage)

        val emojiInformation = EmojiUtils.emojiInformation(message.content)
        val res: Int

        res = if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size == 1) {
            R.dimen.emoji_size_single_emoji
        } else if (emojiInformation.isOnlyEmojis && emojiInformation.emojis.size > 1) {
            R.dimen.emoji_size_only_emojis
        } else {
            R.dimen.emoji_size_default
        }

        tvMessageContent.setEmojiSizeRes(res, false)
        tvMessageContent.text = message.content
    }

}

