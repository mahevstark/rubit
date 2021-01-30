package net.trejj.rubytalksense.adapters.messaging

import android.view.View
import net.trejj.rubytalksense.model.realms.Message

interface Interaction {
    fun onContainerViewClick(pos:Int,itemView:View,message:Message)
    fun onItemViewClick(pos:Int,itemView:View,message:Message)
    fun onLongClick(pos:Int,itemView:View,message:Message)
    fun onProgressButtonClick(pos:Int,itemView:View,message:Message)
    fun onQuotedMessageClick(pos:Int,itemView:View,message:Message)

}