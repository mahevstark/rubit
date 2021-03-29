package net.trejj.talk.adapters.messaging.holders

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import net.trejj.talk.R
import net.trejj.talk.adapters.messaging.AudibleBase
import net.trejj.talk.adapters.messaging.AudibleInteraction
import net.trejj.talk.adapters.messaging.holders.base.BaseSentHolder
import net.trejj.talk.common.extensions.setHidden
import net.trejj.talk.model.AudibleState
import net.trejj.talk.model.constants.DownloadUploadStat
import net.trejj.talk.model.constants.MessageType
import net.trejj.talk.model.realms.Message
import net.trejj.talk.model.realms.User
import net.trejj.talk.utils.AdapterHelper
import net.trejj.talk.utils.ListUtil

import de.hdodenhof.circleimageview.CircleImageView

class SentVoiceMessageHolder(context: Context, itemView: View, private val myThumbImg: String) : BaseSentHolder(context, itemView), AudibleBase {

    var playBtn: ImageView = itemView.findViewById<View>(R.id.voice_play_btn) as ImageView
    var seekBar: SeekBar = itemView.findViewById<View>(R.id.voice_seekbar) as SeekBar
    var circleImg: CircleImageView = itemView.findViewById<View>(R.id.voice_circle_img) as CircleImageView
    var tvDuration: TextView = itemView.findViewById<View>(R.id.tv_duration) as TextView
    private val voiceMessageStat: ImageView = itemView.findViewById(R.id.voice_message_stat)

    override var audibleState: LiveData<Map<String, AudibleState>>? = null
    override var audibleInteraction: AudibleInteraction? = null


    override fun bind(message: Message, user: User, starMessage: ArrayList<String>) {
        super.bind(message, user,starMessage)
        //set initial values
        seekBar.progress = 0
        playBtn.setImageResource(AdapterHelper.getPlayIcon(false))
        loadUserPhoto(user, message.fromId, MessageType.isSentType(message.type), circleImg)
        tvDuration.text = message.mediaDuration


        voiceMessageStat.setImageResource(AdapterHelper.getVoiceMessageIcon(message.isVoiceMessageSeen))
        playBtn.setHidden(message.downloadUploadStat != DownloadUploadStat.SUCCESS, true)

        lifecycleOwner?.let {


            audibleState?.observe(it, Observer { audioRecyclerStateMap ->
                if (audioRecyclerStateMap.containsKey(message.messageId)) {
                    audioRecyclerStateMap[message.messageId]?.let { mAudioRecyclerState ->


                        if (mAudioRecyclerState.currentDuration != null)
                            tvDuration.text = mAudioRecyclerState.currentDuration

                        if (mAudioRecyclerState.progress != -1) {
                            seekBar.progress = mAudioRecyclerState.progress
                        }

                        if (mAudioRecyclerState.max != -1) {
                            val max = mAudioRecyclerState.max
                            seekBar.max = max
                        }


                        playBtn.setImageResource(AdapterHelper.getPlayIcon(mAudioRecyclerState.isPlaying))

                    }
                } else {
                    tvDuration.text = message.mediaDuration
                }
            })
        }

        playBtn.setOnClickListener {
            interaction?.onContainerViewClick(adapterPosition, itemView, message)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    audibleInteraction?.onSeek(message, progress, seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun loadUserPhoto(user: User, fromId: String, isSentType: Boolean, imageView: ImageView) {

        if (isSentType) {
            Glide.with(context).load(myThumbImg).into(imageView)
        }
        //if it's a group load the user image
        else if (user.isGroupBool && user.group.users != null) {
            val mUser = ListUtil.getUserById(fromId, user.group.users)
            if (mUser != null && mUser.thumbImg != null) {
                Glide.with(context).load(mUser.thumbImg).into(imageView)
            }
        } else {
            if (user.thumbImg != null) Glide.with(context).load(user.thumbImg).into(imageView)
        }
    }


}

