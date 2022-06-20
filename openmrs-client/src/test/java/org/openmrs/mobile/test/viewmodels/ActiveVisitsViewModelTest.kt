package org.openmrs.mobile.test.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.openmrs.android_sdk.library.dao.VisitDAO
import com.openmrs.android_sdk.library.databases.entities.LocationEntity
import com.openmrs.android_sdk.library.models.Visit
import com.openmrs.android_sdk.library.models.VisitType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.Before
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.openmrs.mobile.activities.activevisits.ActiveVisitsViewModel
import org.openmrs.mobile.test.ACUnitTestBaseRx
import rx.Observable
import java.util.ArrayList

@RunWith(JUnit4::class)
class ActiveVisitsViewModelTest : ACUnitTestBaseRx() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var visitDAO: VisitDAO

    lateinit var visitList: List<Visit>

    lateinit var viewModel: ActiveVisitsViewModel

    @Before
    override fun setUp(){
        super.setUp()
        MockitoAnnotations.initMocks(this)
        visitList = createVisitList()
        viewModel = ActiveVisitsViewModel(visitDAO)
    }

    @Test
    fun getActiveVisits_success(){
        Mockito.`when`(visitDAO.activeVisits).thenReturn(Observable.just(visitList))

        viewModel.fetchActiveVisits()

        val actualVisitList = viewModel.activeVisits.value
        val actualLoading = viewModel.loading.value
        val actualError = viewModel.error.value

        assertIterableEquals(visitList, actualVisitList)
        assertFalse(actualLoading!!)
        assertNull(actualError)
    }

    @Test
    fun getActiveVisits_error(){
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(visitDAO.activeVisits).thenReturn(Observable.error(throwable))

        viewModel.fetchActiveVisits()

        val actualVisitList = viewModel.activeVisits.value
        val actualLoading = viewModel.loading.value
        val actualError = viewModel.error.value

        assertNull(actualVisitList)
        assertFalse(actualLoading!!)
        assertEquals(errorMsg, actualError)
    }

    @Test
    fun getActiveVisitsWithQuery_success(){
        val visit = visitList[0]
        val filteredVisits = listOf(visit)
        Mockito.`when`(visitDAO.activeVisits).thenReturn(Observable.just(filteredVisits))

        viewModel.fetchActiveVisits(visit.display!!)

        val actualVisitList = viewModel.activeVisits.value
        val actualLoading = viewModel.loading.value
        val actualError = viewModel.error.value

        assertIterableEquals(filteredVisits, actualVisitList)
        assertFalse(actualLoading!!)
        assertNull(actualError)
    }

    @Test
    fun getActiveVisitsWithQuery_noMatchingVisits(){
        Mockito.`when`(visitDAO.activeVisits).thenReturn(Observable.just(emptyList()))

        viewModel.fetchActiveVisits("Visit99")

        val actualVisitList = viewModel.activeVisits.value
        val actualLoading = viewModel.loading.value
        val actualError = viewModel.error.value

        assertIterableEquals(emptyList<Visit>(), actualVisitList)
        assertFalse(actualLoading!!)
        assertNull(actualError)
    }

    @Test
    fun getActiveVisitsWithQuery_error(){
        val errorMsg = "Error message!"
        val throwable = Throwable(errorMsg)
        Mockito.`when`(visitDAO.activeVisits).thenReturn(Observable.error(throwable))

        viewModel.fetchActiveVisits("visit1")

        val actualVisitList = viewModel.activeVisits.value
        val actualLoading = viewModel.loading.value
        val actualError = viewModel.error.value

        assertNull(actualVisitList)
        assertFalse(actualLoading!!)
        assertEquals(errorMsg, actualError)
    }

    private fun createVisitList(): List<Visit> {
        val list: MutableList<Visit> = ArrayList()
        list.add(createVisit("visit1"))
        list.add(createVisit("visit2"))
        return list
    }

    private fun createVisit(display: String): Visit {
        val visit = Visit()
        visit.location = LocationEntity(display)
        visit.visitType = VisitType(display)
        visit.patient = createPatient(1L)
        return visit
    }
}