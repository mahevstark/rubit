package net.trejj.talk.activities.main

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.droidninja.imageeditengine.ImageEditor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sinch.android.rtc.SinchError
import kotlinx.android.synthetic.main.callscreen.*
import net.trejj.talk.Function
import net.trejj.talk.R
import net.trejj.talk.activities.*
import net.trejj.talk.activities.settings.SettingsActivity
import net.trejj.talk.adapters.ViewPagerAdapter
import net.trejj.talk.common.ViewModelFactory
import net.trejj.talk.common.extensions.findFragmentByTagForViewPager
import net.trejj.talk.events.ExitUpdateActivityEvent
import net.trejj.talk.fragments.BaseFragment
import net.trejj.talk.interfaces.FragmentCallback
import net.trejj.talk.interfaces.StatusFragmentCallbacks
import net.trejj.talk.job.DailyBackupJob
import net.trejj.talk.job.SaveTokenJob
import net.trejj.talk.job.SetLastSeenJob
import net.trejj.talk.model.realms.User
import net.trejj.talk.services.FCMRegistrationService
import net.trejj.talk.services.InternetConnectedListener
import net.trejj.talk.services.NetworkService
import net.trejj.talk.services.SinchService
import net.trejj.talk.utils.*
import net.trejj.talk.utils.network.FireManager
import net.trejj.talk.views.dialogs.IgnoreBatteryDialog
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity(), FabRotationAnimation.RotateAnimationListener, FragmentCallback, StatusFragmentCallbacks, SinchService.StartFailedListener {
    private var isInSearchMode = false

    var permissionsSinch = arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.READ_PHONE_STATE)

    private var previousPoints : Double = 0.0
    private lateinit var toolbar: Toolbar
    private lateinit var tvSelectedChatCount: TextView

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout


    private var users: List<User>? = null
    private var fireListener: FireListener? = null
    private var adapter: ViewPagerAdapter? = null
    private lateinit var rotationAnimation: FabRotationAnimation
    private var root: CoordinatorLayout? = null

    private var currentPage = 0

    private lateinit var viewModel: MainViewModel

    private var ignoreBatteryDialog: IgnoreBatteryDialog? = null


    override fun enablePresence(): Boolean {
        return true
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()

        viewModel = ViewModelProvider(this, ViewModelFactory(this.application)).get(MainViewModel::class.java)


        setSupportActionBar(toolbar)

        rotationAnimation = FabRotationAnimation(this)

        fireListener = FireListener()
        startServices()

        users = RealmHelper.getInstance().listOfUsers

        fab.setOnClickListener {
            when (currentPage) {
                1 -> startActivity(Intent(this@MainActivity, NewChatActivity::class.java))
                2 -> startCamera()

                3 -> startActivity(Intent(this@MainActivity, NewCallActivity::class.java))
            }
        }
//        start();

        textStatusFab.setOnClickListener { startActivityForResult(Intent(this, TextStatusActivity::class.java), REQUEST_CODE_TEXT_STATUS) }


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            //onSwipe or tab change
            override fun onPageSelected(position: Int) {
                currentPage = position
                if (isInSearchMode)
                    exitSearchMode()

                when (position) {


                    //add margin to fab when tab is changed only if ads are shown
                    //animate fab with rotation animation also
                    1 -> {
                        getFragmentByPosition(1)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(baseFragment.isVisible && baseFragment.isAdShowing)
                        }

                        animateFab(R.drawable.ic_chat)
                    }
                    2 -> {
                        getFragmentByPosition(2)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(baseFragment.isVisible && baseFragment.isAdShowing)
                        }
                        animateFab(R.drawable.ic_photo_camera)
                    }

                    3 -> {

                        getFragmentByPosition(3)?.let { fragment ->
                            val baseFragment = fragment as BaseFragment
                            addMarginToFab(baseFragment.isVisible && baseFragment.isAdShowing)
                        }
                        animateFab(R.drawable.ic_phone)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {


            }
        })

        //revert status fab to starting position
        textStatusFab.addOnHideAnimationListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                textStatusFab.animate().y(fab.y).start()

            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        //save app ver if it's not saved before
        if (!SharedPreferencesManager.isAppVersionSaved()) {
            FireConstants.usersRef.child(FireManager.uid).child("ver").setValue(AppVerUtil.getAppVersion(this)).addOnSuccessListener { SharedPreferencesManager.setAppVersionSaved(true) }
        }


        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        retrivePoints(user!!.uid)

//        if (!SharedPreferencesManager.hasAgreedToPrivacyPolicy()) {
//            showPrivacyAlertDialog()
//        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            try {
//                val pkg = packageName
//                val pm = getSystemService(PowerManager::class.java)
//                if (!pm.isIgnoringBatteryOptimizations(pkg) && !SharedPreferencesManager.isDoNotShowBatteryOptimizationAgain()) {
//                    showBatteryOptimizationDialog()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        }

        viewModel.deleteOldMessagesIfNeeded()
        viewModel.checkForUpdate().subscribe({ needsUpdate ->
            if (needsUpdate) {
                startUpdateActivity()
            } else {
                EventBus.getDefault().post(ExitUpdateActivityEvent())
            }
        }, {

        })
        val time: String = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val preferences: SharedPreferences = getSharedPreferences("net.trejj.talk", Context.MODE_PRIVATE)
        val currentTime: String = time.substring(6,8)



        val diff: Int = Integer.parseInt(currentTime) - Integer.parseInt(preferences.getString("currentTime","0"))

        if(diff>3){
            val editor: SharedPreferences.Editor = preferences.edit()
            editor.putBoolean("isCallInProgress",false)
            editor.apply()
        }
    }

    override fun onSinchConnected() {
        TODO("Not yet implemented")
    }

    override fun onSinchDisconnected() {
        TODO("Not yet implemented")
    }

    override fun goingToUpdateActivity() {
        ignoreBatteryDialog?.dismiss()
        super.goingToUpdateActivity()
    }


    //for users who updated the app
    private fun showPrivacyAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setPositiveButton(R.string.agree_and_continue) { dialog, which ->
            SharedPreferencesManager.setAgreedToPrivacyPolicy(true)
        }

        alertDialog.setNegativeButton(R.string.cancel) { dialog, which ->
            finish()
        }

        alertDialog.show()
    }

    private fun showBatteryOptimizationDialog() {

        ignoreBatteryDialog = IgnoreBatteryDialog(this)
        ignoreBatteryDialog?.setOnDialogClickListener(object : IgnoreBatteryDialog.OnDialogClickListener {

            override fun onCancelClick(checkBoxChecked: Boolean) {
                SharedPreferencesManager.setDoNotShowBatteryOptimizationAgain(checkBoxChecked)
            }

            override fun onOk() {
                try {
                    val intent = Intent()
                    intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "could not open Battery Optimization Settings", Toast.LENGTH_SHORT).show();
                }

            }

        })
        ignoreBatteryDialog?.show()
    }


    //start CameraActivity
    private fun startCamera() {

        val intent = Intent(this, CameraActivity::class.java)
        intent.putExtra(IntentUtils.CAMERA_VIEW_SHOW_PICK_IMAGE_BUTTON, true)
        intent.putExtra(IntentUtils.IS_STATUS, true)
        startActivityForResult(intent, CAMERA_REQUEST)


    }

    //animate FAB with rotation animation
    @SuppressLint("RestrictedApi")
    private fun animateFab(drawable: Int) {
        val animation = rotationAnimation.start(drawable)
        fab.startAnimation(animation)
    }

    private fun animateTextStatusFab() {
        val show = viewPager.currentItem == 2
//        if (show) {
//            textStatusFab.show()
//            textStatusFab.animate().y(fab.top - DpUtil.toPixel(70f, this)).start()
//        } else {
//            textStatusFab.hide()
//            textStatusFab.layoutParams = fab.layoutParams
//        }
        if (show) {
            textStatusFab.show()
            fab.show()
            textStatusFab.animate().y(fab.top - DpUtil.toPixel(70f, this)).start()
        } else if(viewPager.currentItem == 0){
            textStatusFab.hide()
            fab.hide()
        } else {
            textStatusFab.hide()
            fab.show()
            textStatusFab.layoutParams = fab.layoutParams
        }
    }


    override fun fetchStatuses() {
        users?.let {
            viewModel.fetchStatuses(it)
        }
    }


    private fun startServices() {
        if (!Util.isOreoOrAbove()) {
            startService(Intent(this, NetworkService::class.java))
            startService(Intent(this, InternetConnectedListener::class.java))
            startService(Intent(this, FCMRegistrationService::class.java))

        } else {
            if (!SharedPreferencesManager.isTokenSaved())
                SaveTokenJob.schedule(this, null)

            SetLastSeenJob.schedule(this)
            UnProcessedJobs.process(this)
        }

        //sync contacts for the first time
        if (!SharedPreferencesManager.isContactSynced()) {
            syncContacts()
        } else {
            //sync contacts every day if needed
            if (SharedPreferencesManager.needsSyncContacts()) {
                syncContacts()
            }
        }

        //schedule daily job to backup messages
        DailyBackupJob.schedule()


    }

    private fun syncContacts() {
        disposables.add(ContactUtils.syncContacts().subscribe({

        }, { throwable ->

        }))
    }


    private fun init() {
        fab = findViewById(R.id.open_new_chat_fab)
        toolbar = findViewById(R.id.toolbar)
        tvSelectedChatCount = findViewById(R.id.tv_selected_chat)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        textStatusFab = findViewById(R.id.text_status_fab)
        root = findViewById(R.id.root)

        initTabLayout()

        //prefix for a bug in older APIs
        fab.bringToFront()
    }

    private fun initTabLayout() {
        tabLayout.setupWithViewPager(viewPager)
        adapter = ViewPagerAdapter(supportFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = adapter
        viewPager.offscreenPageLimit = 1
        setTabsTitles(4)
    }


    override fun onPause() {
        super.onPause()
        ignoreBatteryDialog?.dismiss()
        fireListener?.cleanup()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val menuItem = menu.findItem(R.id.search_item)
        searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                return false
            }

            //submit search for the current active fragment
            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.onQueryTextChange(newText)
                return false
            }

        })
        //revert back to original adapter
        searchView.setOnCloseListener {
            exitSearchMode()
            true
        }

        menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            //exit search mode on searchClosed
            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                exitSearchMode()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.settings_item -> settingsItemClicked()

            R.id.search_item -> searchItemClicked()

            R.id.new_group_item -> createGroupClicked()


            R.id.invite_item -> startActivity(IntentUtils.getShareAppIntent(this@MainActivity))

            R.id.buyCredit -> {
                val intent = Intent(this@MainActivity, EarnCreditsActivity::class.java)
                intent.putExtra(IntentUtils.IS_BROADCAST, true)
                startActivity(intent)
            }

            R.id.new_broadcast_item -> {
                val intent = Intent(this@MainActivity, NewGroupActivity::class.java)
                intent.putExtra(IntentUtils.IS_BROADCAST, true)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun createGroupClicked() {
        startActivity(Intent(this, NewGroupActivity::class.java))
    }

    private fun searchItemClicked() {
        isInSearchMode = true
    }


    private fun settingsItemClicked() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


    override fun onBackPressed() {
        if (isInSearchMode)
            exitSearchMode()
        else {
            if (viewPager.currentItem != CHATS_TAB_INDEX) {
                viewPager.setCurrentItem(CHATS_TAB_INDEX, true)
            } else {
                super.onBackPressed()
            }
        }

    }


    fun exitSearchMode() {
        isInSearchMode = false
    }


    private fun setTabsTitles(tabsSize: Int) {
        for (i in 0 until tabsSize) {
            when (i) {

                0 -> tabLayout.getTabAt(i)?.setText("Phone\nCall");

                1 -> tabLayout.getTabAt(i)?.setText(R.string.chats)

                2 -> tabLayout.getTabAt(i)?.setText(R.string.status)

                3 -> tabLayout.getTabAt(i)?.setText(R.string.calls)
            }
        }

    }


    override fun onRotationAnimationEnd(drawable: Int) {
        fab?.setImageResource(drawable)
        animateTextStatusFab()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST || requestCode == ImageEditor.RC_IMAGE_EDITOR || requestCode == REQUEST_CODE_TEXT_STATUS) {
            viewModel.onActivityResult(requestCode, resultCode, data)

        }

    }


    override fun addMarginToFab(isAdShowing: Boolean) {
        val layoutParams = fab.layoutParams as CoordinatorLayout.LayoutParams
        val v = if (isAdShowing) DpUtil.toPixel(95f, this) else resources.getDimensionPixelSize(R.dimen.fab_margin).toFloat()


        layoutParams.bottomMargin = v.toInt()

        fab.layoutParams = layoutParams

        fab.clearAnimation()
        fab.animation?.cancel()

        animateTextStatusFab()

    }


    override fun openCamera() {
        startCamera()
    }

    override fun startTheActionMode(callback: ActionMode.Callback) {
        startActionMode(callback)
    }

    private fun getFragmentByPosition(position: Int): Fragment? {
        return viewPager.currentItem?.let { supportFragmentManager.findFragmentByTagForViewPager(position, it) }
    }

    fun CallNumber(number: String, callername: String) {
        if (permissionsAvailable(permissionsSinch)) {
            if (number.length < 6) {
                Toast.makeText(this, "Please Enter Valid Number To Call", Toast.LENGTH_LONG).show()
                return
            } else if (!number.contains("+")) {
                Toast.makeText(this, "Please put country code before number", Toast.LENGTH_SHORT).show()
                return
            }
            if (isCallEnabled) {
                val serviceIntent = Intent(this, SinchService::class.java)
                serviceIntent.putExtra(IntentUtils.START_SINCH, true)
                startService(serviceIntent)
                if (previousPoints != null && previousPoints >= java.lang.Double.valueOf(
                                Function.checkCountry(number))) {
//                    val call = getSinchServiceInterface()!!.callPhoneNumber(number)
//                    val callId = call.callId

                    val callScreen = Intent(this@MainActivity, CallScreenActivity::class.java)
//                    callScreen.putExtra(CallingService.callId, callId)
                    callScreen.putExtra("number", number)
                    callScreen.putExtra("callername", callername)
                    val preferences: SharedPreferences = this.getSharedPreferences("net.trejj.talk", Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = preferences.edit()
                    editor.putString("number",number)
                    editor.putString("callername",callername)
                    editor.apply()
                    startActivity(callScreen)
                } else {

                    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                    alertDialog.setTitle("Minimum of " + java.lang.Double.valueOf(Function.checkCountry
                    (number)) + " Credits Required")
                    alertDialog.setMessage("Sorry, your account balance is very Low, please recharge your account.")
                    alertDialog.setPositiveButton(
                            "Buy Credits"
                    ) { _, _ ->
                        startActivity(Intent(this@MainActivity, EarnCreditsActivity::class.java))
                    }
                    alertDialog.setNegativeButton(
                            "No"
                    ) { _, _ -> }
                    val alert: AlertDialog = alertDialog.create()
                    alert.setCanceledOnTouchOutside(false)
                    alert.show()
                }
                //callButtonClicked();
            } else {
                Toast.makeText(this, "Call is not ready", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, permissionsSinch, 69)
        }
    }

    private lateinit var database: DatabaseReference
    fun retrivePoints(uid : String){
        database = Firebase.database.reference.child("users").child(uid)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    previousPoints = snapshot.child("credits").value.toString().toDouble()
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }

        database.addValueEventListener(postListener)
    }


    companion object {
        lateinit var fab: FloatingActionButton
        lateinit var textStatusFab: FloatingActionButton
        lateinit var searchView: SearchView
        @JvmField
        var isCallEnabled: Boolean = true
        const val CAMERA_REQUEST = 9514
        const val REQUEST_CODE_TEXT_STATUS = 9145
        private const val CHATS_TAB_INDEX = 0

    }

    override fun onStartFailed(error: SinchError?) {
    }

    override fun onStarted() {

    }


}