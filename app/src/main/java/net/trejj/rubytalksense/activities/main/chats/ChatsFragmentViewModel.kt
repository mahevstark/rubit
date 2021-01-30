package net.trejj.rubytalksense.activities.main.chats

import net.trejj.rubytalksense.common.DisposableViewModel
import net.trejj.rubytalksense.model.realms.User
import net.trejj.rubytalksense.utils.TimeHelper
import net.trejj.rubytalksense.utils.network.FireManager
import io.reactivex.rxkotlin.addTo
import java.util.*

class ChatsFragmentViewModel : DisposableViewModel() {
    private val currentDownloads = mutableListOf<String>()


    fun fetchUserImage(pos: Int, user: User) {
        if (user.isBroadcastBool)return
        if (TimeHelper.canFetchUserImage(Date().time, user.lastTimeFetchedImage) && !currentDownloads.contains(user.uid)) {
            currentDownloads.add(user.uid)
            FireManager.checkAndDownloadUserThumbImg(user).subscribe({image ->

            }, { throwable ->

            }).addTo(disposables)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

}