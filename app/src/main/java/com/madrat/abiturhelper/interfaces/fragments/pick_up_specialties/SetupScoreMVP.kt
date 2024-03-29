package com.madrat.abiturhelper.interfaces.fragments.pick_up_specialties

import android.content.Context

interface SetupScoreMVP {
    interface View {
        fun setupMVP()
        fun moveToWorkWithSpecialties(view: android.view.View)
        fun checkForFIO(context: Context): Boolean
    }

    interface Presenter {
        fun saveFullName(lastName: String, firstName: String, patronymic: String)
        fun saveScore(maths: String, russian: String, physics: String, computerScience: String,
                      socialScience: String, additionalScore: String?)
        fun checkTextForBeingEmpty(text: String): Int

        fun returnIntFromString(text: String): Int
    }
}
