package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import net.trejj.talk.R
import net.trejj.talk.activities.ContactDetailsActivity
import net.trejj.talk.adapters.messaging.ContactHolderBase
import net.trejj.talk.adapters.messaging.ContactHolderInteraction
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import net.trejj.talk.utils.IntentUtils

class SentContactHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView),ContactHolderBase {

    private val tvContactName: TextView = itemView.findViewById(R.id.tv_contact_name)
    private val btnMessageContact: Button = itemView.findViewById(R.id.btn_message_contact)

    override var contactHolderInteraction: ContactHolderInteraction? = null

    override fun bind(message: Message,user: User, starMessage: ArrayList<String>) {
        super.bind(message,user,starMessage)
        //set contact name
        tvContactName.text = message.content


        //send a message to this contact if installed this app
        btnMessageContact.setOnClickListener {
            contactHolderInteraction?.onMessageClick(message.contact)
        }

    }



}

