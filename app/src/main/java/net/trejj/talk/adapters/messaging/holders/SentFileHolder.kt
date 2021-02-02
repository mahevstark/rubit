package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.common.extensions.setHidden
import net.trejj.talk.model.constants.DownloadUploadStat
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import net.trejj.talk.utils.Util


class SentFileHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView) {

    private val tvFileSize: TextView = itemView.findViewById(R.id.tv_file_size)
    private val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
    private val tvFileExtension: TextView = itemView.findViewById(R.id.tv_file_extension)


    private val fileIcon: ImageView = itemView.findViewById(R.id.file_icon)
    override fun bind(message: Message,user: User) {
        super.bind(message,user)
        val fileExtension = Util.getFileExtensionFromPath(message.metadata).toUpperCase()
        tvFileExtension.text = fileExtension
        //set file name
        tvFileName.text = message.metadata

        //file size
        tvFileSize.text = message.fileSize

        fileIcon.setHidden(message.downloadUploadStat != DownloadUploadStat.SUCCESS, true)

    }


}
