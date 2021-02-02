package net.trejj.talk.adapters.messaging

import net.trejj.talk.model.realms.Message

interface AudibleInteraction {
    fun onSeek(message:Message,progress:Int,max:Int)
}