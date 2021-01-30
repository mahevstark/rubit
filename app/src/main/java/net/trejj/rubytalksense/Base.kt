package net.trejj.rubytalksense

import io.reactivex.disposables.CompositeDisposable

interface Base {
    val disposables:CompositeDisposable
}