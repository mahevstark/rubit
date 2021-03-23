package net.trejj.talk.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import net.trejj.talk.Base
import net.trejj.talk.activities.main.MainViewModel
import net.trejj.talk.interfaces.FragmentCallback
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment(),Base {
    @JvmField
    var fragmentCallback: FragmentCallback? = null
    open var adView: AdView? = null

    abstract fun showAds(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override val disposables = CompositeDisposable()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentCallback = context as FragmentCallback
        } catch (castException: ClassCastException) {
            /** The activity does not implement the listener.  */
        }
    }

    fun adViewInitialized(mAdView: AdView?) {
        adView = mAdView
        val adListener: AdListener = object : AdListener() {
            override fun onAdFailedToLoad(i: Int) {
                super.onAdFailedToLoad(i)
                adView!!.visibility = View.GONE
                if (fragmentCallback != null) fragmentCallback!!.addMarginToFab(false)
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                adView!!.visibility = View.VISIBLE
                if (fragmentCallback != null) fragmentCallback!!.addMarginToFab(true)
            }
        }
        adView!!.adListener = adListener
    }

//    val isAdShowing: Boolean = false
    val isAdShowing: Boolean
        get() = adView != null &&
                adView!!.visibility == View.VISIBLE

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (adView != null && showAds()) adView!!.loadAd(AdRequest.Builder().build())
    }

    open fun onQueryTextChange(newText: String?) {}
    open fun onSearchClose() {}
}