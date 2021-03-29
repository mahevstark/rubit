package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.model.constants.DownloadUploadStat
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User


class SentVideoMessageHolder(context: Context, itemView: View) : BaseSentHolder(context,itemView) {

    private val thumbImg: ImageView = itemView.findViewById(R.id.thumb_img)
    private val btnPlayVideo: ImageButton = itemView.findViewById(R.id.btn_play_video)

    private val tvMediaDuration: TextView = itemView.findViewById(R.id.tv_media_duration)

    override fun bind(message: Message, user: User, starMessage: ArrayList<String>) {
        super.bind(message,user,starMessage)
        if (message.downloadUploadStat != DownloadUploadStat.SUCCESS) {
            tvMediaDuration.visibility = View.GONE
        } else {
            tvMediaDuration.visibility = View.VISIBLE
            tvMediaDuration.text = message.mediaDuration
        }



        Glide.with(context).load(message.videoThumb).into(thumbImg)

        btnPlayVideo?.setOnClickListener { interaction?.onContainerViewClick(adapterPosition, itemView, message) }

    }


}
