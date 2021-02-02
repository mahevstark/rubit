package net.trejj.talk.adapters.messaging

import net.trejj.talk.model.realms.RealmContact

interface ContactHolderInteraction {
    fun onMessageClick(contact:RealmContact)
    fun onAddContactClick(contact:RealmContact)
}