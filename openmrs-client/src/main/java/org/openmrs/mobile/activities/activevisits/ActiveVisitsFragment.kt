/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.mobile.activities.activevisits

import android.os.Bundle
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import org.openmrs.mobile.R
import org.openmrs.mobile.activities.BaseFragment
import org.openmrs.mobile.databinding.FragmentActiveVisitsBinding

@AndroidEntryPoint
class ActiveVisitsFragment : BaseFragment() {
    private var _binding: FragmentActiveVisitsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ActiveVisitsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentActiveVisitsBinding.inflate(inflater, container, false)

        val linearLayoutManager = LinearLayoutManager(this.activity)
        with(binding) {
            visitsRecyclerView.setHasFixedSize(true)
            visitsRecyclerView.layoutManager = linearLayoutManager
            visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(requireContext(), ArrayList())

            setupObservers()
            displayActiveVisits()

            swipeLayout.setOnRefreshListener {
                displayActiveVisits()
                swipeLayout.isRefreshing = false
            }
        }
        return binding.root
    }

    private fun setupObservers() {
        with(binding) {
            viewModel.activeVisits.observe(viewLifecycleOwner, Observer { visits ->
                if (visits.isEmpty()) {
                    visitsRecyclerView.visibility = View.GONE
                    showEmptyListText()
                } else {
                    visitsRecyclerView.adapter = ActiveVisitsRecyclerViewAdapter(requireContext(), visits)
                    visitsRecyclerView.visibility = View.VISIBLE
                    hideEmptyListText()
                }
            })
            viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
                if (isLoading) {
                    progressBar.visibility = View.VISIBLE
                    visitsRecyclerView.visibility = View.GONE
                } else progressBar.visibility = View.GONE
            })
            viewModel.error.observe(viewLifecycleOwner, Observer { error ->
                if (error != null) showEmptyListText()
            })
        }
    }

    fun displayActiveVisits() {
        viewModel.fetchActiveVisits()
    }

    fun displayActiveVisits(query: String) {
        viewModel.fetchActiveVisits(query)
    }

    private fun showEmptyListText() {
        binding.emptyVisitsListViewLabel.visibility = View.VISIBLE
        binding.emptyVisitsListViewLabel.text = getString(R.string.search_visits_no_results)
    }

    private fun hideEmptyListText() {
        binding.emptyVisitsListViewLabel.visibility = View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.find_visits_menu, menu)

        val findVisitView = menu.findItem(R.id.actionSearchLocalVisits).actionView as SearchView

        findVisitView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                findVisitView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                if (query.isNotEmpty()) displayActiveVisits(query)
                else displayActiveVisits()

                return true
            }
        })
    }

    companion object {
        fun newInstance(): ActiveVisitsFragment {
            return ActiveVisitsFragment()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}