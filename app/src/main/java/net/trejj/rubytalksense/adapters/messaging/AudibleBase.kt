package net.trejj.rubytalksense.adapters.messaging

import androidx.lifecycle.LiveData
import net.trejj.rubytalksense.model.AudibleState

interface AudibleBase {
    var audibleState: LiveData<Map<String, AudibleState>>?
    var audibleInteraction:AudibleInteraction?
}