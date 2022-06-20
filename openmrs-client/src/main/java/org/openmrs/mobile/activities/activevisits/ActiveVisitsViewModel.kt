package org.openmrs.mobile.activities.activevisits

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.models.Visit
import dagger.hilt.android.lifecycle.HiltViewModel
import org.openmrs.mobile.activities.BaseViewModel
import org.openmrs.mobile.utilities.FilterUtil
import rx.android.schedulers.AndroidSchedulers
import javax.inject.Inject

@HiltViewModel
class ActiveVisitsViewModel @Inject constructor(private val visitDAO: VisitDAO) : BaseViewModel() {
    private val _activeVisits: MutableLiveData<List<Visit>> = MutableLiveData()
    val activeVisits: LiveData<List<Visit>> get() = _activeVisits

    fun fetchActiveVisits() {
        _loading.value = true
        addSubscription(visitDAO.activeVisits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit> -> setContentState(visits) },
                        { setErrorState(it.message) }
                ))
    }

    fun fetchActiveVisits(query: String) {
        _loading.value = true
        addSubscription(visitDAO.activeVisits
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { visits: List<Visit> ->
                            val filteredVisits = FilterUtil.getPatientsWithActiveVisitsFilteredByQuery(visits, query)
                            setContentState(filteredVisits)
                        },
                        { setErrorState(it.message) }
                ))
    }

    private fun setContentState(items: List<Visit>) {
        _activeVisits.value = items
        _error.value = null
        _loading.value = false
    }

    private fun setErrorState(message: String?) {
        _activeVisits.value = null
        _error.value = message
        _loading.value = false
    }
}