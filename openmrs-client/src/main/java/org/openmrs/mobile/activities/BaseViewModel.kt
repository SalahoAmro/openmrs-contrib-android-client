package org.openmrs.mobile.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rx.Subscription
import rx.subscriptions.CompositeSubscription

abstract class BaseViewModel : ViewModel() {

    protected val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    protected val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private var mSubscription: CompositeSubscription = CompositeSubscription()

    protected fun addSubscription(subscription: Subscription){
        mSubscription.add(subscription)
    }

    override fun onCleared() {
        mSubscription.clear()
        super.onCleared()
    }
}