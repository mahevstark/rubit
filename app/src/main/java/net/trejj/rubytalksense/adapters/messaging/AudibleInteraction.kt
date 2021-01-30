package net.trejj.rubytalksense.adapters.messaging

import net.trejj.rubytalksense.model.realms.Message

interface AudibleInteraction {
    fun onSeek(message:Message,progress:Int,max:Int)
}