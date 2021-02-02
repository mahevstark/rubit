package net.trejj.talk.adapters.messaging

import androidx.lifecycle.LiveData
import net.trejj.talk.model.AudibleState

interface AudibleBase {
    var audibleState: LiveData<Map<String, AudibleState>>?
    var audibleInteraction:AudibleInteraction?
}