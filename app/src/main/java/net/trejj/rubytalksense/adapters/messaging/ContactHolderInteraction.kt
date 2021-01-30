package net.trejj.rubytalksense.adapters.messaging

import net.trejj.rubytalksense.model.realms.RealmContact

interface ContactHolderInteraction {
    fun onMessageClick(contact:RealmContact)
    fun onAddContactClick(contact:RealmContact)
}