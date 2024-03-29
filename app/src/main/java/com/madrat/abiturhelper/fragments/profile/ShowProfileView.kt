package com.madrat.abiturhelper.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.madrat.abiturhelper.R
import com.madrat.abiturhelper.interfaces.fragments.profile.ShowProfileMVP
import com.madrat.abiturhelper.presenters.fragments.profile.ShowProfilePresenter
import com.madrat.abiturhelper.util.showSnack
import kotlinx.android.synthetic.main.fragment_show_profile.*

class ShowProfileView: Fragment(), ShowProfileMVP.View {
    private var showProfilePresenter: ShowProfilePresenter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupMVP()
        setupScoreFields()
        setupSpecialtiesFields()

        // Булевы значения для ФИО
        var isLastNameEditable: Boolean? = true
        var isFirstNameEditable: Boolean? = true
        var isPatronymicEditable: Boolean? = true
        // Булевы значения для баллов
        var isMathsEditable: Boolean? = true
        var isRussianEditable: Boolean? = true
        var isPhysicsEditable: Boolean? = true
        var isComputerScienceEditable: Boolean? = true
        var isSocialScienceEditable: Boolean? = true
        var isAdditionalScoreEditable: Boolean? = true

        profileLastNameEditValue.setOnClickListener {
            isLastNameEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isLastNameEditable, profileLastNameValue,
                    profileLastNameEditValue
            )
        }
        profileFirstNameEditValue.setOnClickListener {
            isFirstNameEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isFirstNameEditable, profileFirstNameValue,
                    profileFirstNameEditValue
            )
        }
        profilePatronymicEditValue.setOnClickListener {
            isPatronymicEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isPatronymicEditable, profilePatronymicValue,
                    profilePatronymicEditValue
            )
        }
        profileMathsEditValue.setOnClickListener {
            isMathsEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isMathsEditable, profileMathsValue,
                    profileMathsEditValue)
        }
        profileRussianEditValue.setOnClickListener {
            isRussianEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isRussianEditable, profileRussianValue,
                    profileRussianEditValue)
        }
        profilePhysicsEditValue.setOnClickListener {
            isPhysicsEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isPhysicsEditable, profilePhysicsValue,
                    profilePhysicsEditValue)
        }
        profileComputerScienceEditValue.setOnClickListener {
            isComputerScienceEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isComputerScienceEditable, profileComputerScienceValue,
                    profileComputerScienceEditValue)
        }
        profileSocialScienceEditValue.setOnClickListener {
            isSocialScienceEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isSocialScienceEditable, profileSocialScienceValue,
                    profileSocialScienceEditValue)
        }
        profileAdditionalScoreEditValue.setOnClickListener {
            isAdditionalScoreEditable = showProfilePresenter?.checkFieldForBeingEditable(
                    isAdditionalScoreEditable, profileAdditionalScoreValue,
                    profileAdditionalScoreEditValue)
        }

        profileUpdateScores.setOnClickListener {v->
            showProfilePresenter?.updateScores(profileMathsValue.text.toString(),
                    profileRussianValue.text.toString(),
                    profilePhysicsValue.text.toString(),
                    profileComputerScienceValue.text.toString(),
                    profileSocialScienceValue.text.toString(),
                    profileAdditionalScoreValue.text.toString())
            v.showSnack(R.string.profileUpdateScoresMessage)
        }
        profileShowFinalList.setOnClickListener {
            val bundle = showProfilePresenter?.returnBundleWithListID(300)
            toActionId(bundle, R.id.action_profile_to_showFittingSpecialties)
        }
        profileApplySelectSpecialtiesForGraduation.setOnClickListener {
            toActionId(null, R.id.action_profile_to_selectSpecialtiesForCalculating)
        }
        profileAddToListsCalculatePositions.setOnClickListener {
            toActionId(null, R.id.action_profile_to_show_current_list)
        }
        profileChanceShowResults.setOnClickListener {
            toActionId(null, R.id.action_profile_to_showResults)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.profileTitle)
        return inflater.inflate(R.layout.fragment_show_profile, container, false)
    }
    override fun onDestroyView() {
        showProfilePresenter = null
        super.onDestroyView()
    }

    override fun setupMVP() {
        showProfilePresenter = ShowProfilePresenter(this)
    }
    override fun setupScoreFields() {
        val fullName = showProfilePresenter?.returnFullName()
        val score = showProfilePresenter?.returnCheckedScore()

        fullName?.let {
            profileLastNameValue.setText(fullName.lastName)
            profileFirstNameValue.setText(fullName.firstName)
            profilePatronymicValue.setText(fullName.patronymic)
        }
        score?.let {
            profileMathsValue.setText(score.maths.toString())
            profileRussianValue.setText(score.russian.toString())
            profilePhysicsValue.setText(score.physics.toString())
            profileComputerScienceValue.setText(score.computerScience.toString())
            profileSocialScienceValue.setText(score.socialScience.toString())
            profileAdditionalScoreValue.setText(score.additionalScore.toString())
        }
    }
    override fun setupSpecialtiesFields() {
        val amountOfFinalSpecialties = showProfilePresenter?.returnAmountOfFinalSpecialties()
        profileFinalListOfSpecialtiesAmountValue.setText(amountOfFinalSpecialties.toString())

        val amountOfSpecialtiesWithChance = showProfilePresenter?.returnAmountOfSpecialtiesWithChance()
        profileChanceAmountValue.setText(amountOfSpecialtiesWithChance.toString())

        val amountOfGraduatedSpecialties = showProfilePresenter?.returnAmountOfGraduatedSpecialties()
        profileApplyAmountValue.setText(amountOfGraduatedSpecialties.toString())
    }
    override fun toActionId(bundle: Bundle?, actionId: Int) {
        view?.let { Navigation.findNavController(it).navigate(actionId, bundle) }
    }
}