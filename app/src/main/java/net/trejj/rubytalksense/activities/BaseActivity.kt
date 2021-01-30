package net.trejj.rubytalksense.activities

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import net.trejj.rubytalksense.Base
import net.trejj.rubytalksense.extensions.observeChildEvent
import net.trejj.rubytalksense.extensions.setValueRx
import net.trejj.rubytalksense.extensions.toMap
import net.trejj.rubytalksense.model.constants.DBConstants
import net.trejj.rubytalksense.utils.*
import net.trejj.rubytalksense.utils.network.FireManager
import net.trejj.rubytalksense.utils.update.UpdateChecker
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import net.trejj.rubytalksense.services.CallingService
import net.trejj.rubytalksense.services.SinchService


abstract class BaseActivity : AppCompatActivity(), Base, ServiceConnection {
    override val disposables = CompositeDisposable()
    abstract fun enablePresence(): Boolean
    private var presenceUtil: PresenceUtil? = null
    val fireManager = FireManager()
    private lateinit var newMessageHandler: NewMessageHandler

    //used to clean up like dismissing dialogs
    open fun goingToUpdateActivity() {}

    private var needsUpdate = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        needsUpdate = UpdateChecker(this).needsUpdate()
        if (!needsUpdate) {

            if (enablePresence())
                presenceUtil = PresenceUtil()

            newMessageHandler = NewMessageHandler(this, fireManager, disposables)
            //if user is coming from an old version, then delete the already received messages from his db
            if (SharedPreferencesManager.isDeletedUnfetchedMessage()) {
                attachNewMessageListener()
                attachDeletedMessageListener()
                attachNewGroupListener()
                attachNewCallsListener()
            }
        }
    }


    override fun onStart() {
        super.onStart()
        if (needsUpdate) {
            startUpdateActivity()
        }


    }
    override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
        if (SinchService::class.java.getName() == componentName.className) {
            mSinchServiceInterface = iBinder as SinchService.SinchServiceInterface
            onSinchConnected()
        }
    }

    abstract fun onSinchConnected()

    override fun onServiceDisconnected(componentName: ComponentName) {
        if (SinchService::class.java.getName() == componentName.className) {
            mSinchServiceInterface = null
            onSinchDisconnected()
        }
    }

    abstract fun onSinchDisconnected()

    fun startUpdateActivity() {
        goingToUpdateActivity()
        startActivity(Intent(this, UpdateActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    private fun attachNewGroupListener() {
        FireConstants.newGroups.child(FireManager.uid).observeChildEvent().subscribe( { snap ->
            val dataSnapshot = snap.value

            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.GROUP_ID).value as? String)?.let { groupId ->
                    newMessageHandler.handleNewGroup(dataSnapshot.toMap())

                    deleteNewGroupEvent(groupId).subscribe().addTo(disposables)

                }
            }


        },{error ->}).addTo(disposables)
    }

    private fun attachDeletedMessageListener() {
        FireConstants.deletedMessages.child(FireManager.uid).observeChildEvent().subscribe( { snap ->
            val dataSnapshot = snap.value

            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.MESSAGE_ID).value as? String)?.let { messageId ->
                    newMessageHandler.handleDeletedMessage(dataSnapshot.toMap())

                    deleteDeletedMessage(messageId).subscribe().addTo(disposables)

                }
            }


        },{error ->}).addTo(disposables)
    }


    private fun attachNewMessageListener() {
        FireConstants.userMessages.child(FireManager.uid).observeChildEvent().subscribe( { snap ->
            val dataSnapshot = snap.value
            if (dataSnapshot.value != null) {
                (dataSnapshot.child(DBConstants.MESSAGE_ID).value as? String)?.let { messageId ->
                    val phone = dataSnapshot.child(DBConstants.PHONE).value as? String ?: ""
                    val message = MessageMapper.mapToMessage(dataSnapshot)

                    newMessageHandler.handleNewMessage(phone, message)

                    deleteMessage(messageId).subscribe().addTo(disposables)
                }

            }
        },{error ->}).addTo(disposables)
    }

    private fun attachNewCallsListener() {
        FireConstants.userCalls.child(FireManager.uid).observeChildEvent().subscribe( { snap ->
            val dataSnapshot = snap.value

            CallMapper.mapToFireCall(dataSnapshot)?.let { fireCall ->


                newMessageHandler.handleNewCall(fireCall)

                deleteNewCall(fireCall.callId).subscribe().addTo(disposables)


            }
        },{error ->}).addTo(disposables)
    }

    private fun deleteMessage(messageId: String): Completable {
        return FireConstants.userMessages.child(FireManager.uid).child(messageId).setValueRx(null)
    }

    private fun deleteDeletedMessage(messageId: String): Completable {
        return FireConstants.deletedMessages.child(FireManager.uid).child(messageId).setValueRx(null)
    }

    private fun deleteNewGroupEvent(groupId: String): Completable {
        return FireConstants.newGroups.child(FireManager.uid).child(groupId).setValueRx(null)
    }

    private fun deleteNewCall(callId: String): Completable {
        return FireConstants.userCalls.child(FireManager.uid).child(callId).setValueRx(null)
    }


    override fun onResume() {
        super.onResume()
        if (enablePresence()) {
            presenceUtil?.onResume()
            MyApp.baseActivityResumed()
        }

        (this.application as? MyApp)?.let { application ->
            if (application.isHasMovedToForeground && SharedPreferencesManager.isFingerprintLockEnabled()) {

                val lastActive = SharedPreferencesManager.getLastActive()
                val lockAfter = SharedPreferencesManager.getLockAfter()


                if (lockAfter == 0 || TimeHelper.isTimePassedByMinutes(System.currentTimeMillis(), lastActive, lockAfter))
                    startActivity(Intent(this, LockscreenActivity::class.java))


            }
        }

    }


    override fun onPause() {
        super.onPause()
        if (enablePresence()) {
            presenceUtil?.onPause()
            MyApp.baseActivityPaused()
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
        presenceUtil?.onDestroy()

    }
    protected open fun permissionsAvailable(permissions: Array<String>): Boolean {
        var granted = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission!!) != PackageManager.PERMISSION_GRANTED) {
                granted = false
                break
            }
        }
        return granted
    }

    private var mSinchServiceInterface: SinchService.SinchServiceInterface? = null
    protected open fun getSinchServiceInterface(): SinchService.SinchServiceInterface? {
        return mSinchServiceInterface
    }

}