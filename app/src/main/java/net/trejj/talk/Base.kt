package net.trejj.talk

import io.reactivex.disposables.CompositeDisposable

interface Base {
    val disposables:CompositeDisposable
}