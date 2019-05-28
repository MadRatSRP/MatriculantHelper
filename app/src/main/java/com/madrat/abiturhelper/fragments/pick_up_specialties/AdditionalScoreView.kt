package com.madrat.abiturhelper.fragments.pick_up_specialties

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.madrat.abiturhelper.R
import com.madrat.abiturhelper.presenters.fragments.AdditionalScorePresenter
import com.madrat.abiturhelper.interfaces.fragments.AdditionalScoreMVP
import kotlinx.android.synthetic.main.fragment_score_additional.*
import kotlinx.android.synthetic.main.fragment_score_additional.view.*

class AdditionalScoreView : Fragment(), AdditionalScoreMVP.View {
    companion object { val instance = AdditionalScoreView() }

    private lateinit var additionalScorePresenter: AdditionalScorePresenter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupMVP()

        showSpecialtiesScreen.setOnClickListener { view->

            var additionalScore = 0
            additionalScoreSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    additionalScore = parent?.getItemAtPosition(position).toString().toInt()
                }

            }

            additionalScorePresenter.saveAdditionalScore(additionalScore)
            Navigation.findNavController(view).navigate(R.id.action_setupAdditional_to_pickUpSpecialtiesView)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.additionalScoreTitle)
        val view = inflater.inflate(R.layout.fragment_score_additional,
                                container, false)

        val spinnerItems = resources.getStringArray(R.array.additionalScoreSpinnerEntries)
        val adapter = ArrayAdapter(context, R.layout.custom_spinner, spinnerItems)
        adapter.setDropDownViewResource(R.layout.custom_spinner)

        view.additionalScoreSpinner.adapter = adapter
        return view
    }

    override fun setupMVP() {
        additionalScorePresenter = AdditionalScorePresenter(this)
    }
}