package com.madrat.abiturhelper.presenters.fragments

import android.os.Bundle
import com.madrat.abiturhelper.interfaces.fragments.ProfileMVP
import com.madrat.abiturhelper.model.Score
import com.madrat.abiturhelper.util.MyApplication

class ShowProfilePresenter(private var pv: ProfileMVP.View) : ProfileMVP.Presenter {
    var myApplication = MyApplication.instance

    override fun updateScores(maths: String, russian: String, physics: String, computerScience: String,
                              socialScience: String, additionalScore: String) {
        val checkedMaths = checkTextForBeingEmpty(maths)
        val checkedRussian = checkTextForBeingEmpty(russian)
        val checkedPhysics = checkTextForBeingEmpty(physics)
        val checkedComputerScience = checkTextForBeingEmpty(computerScience)
        val checkedSocialScience = checkTextForBeingEmpty(socialScience)
        val checkedAdditionalScore = checkTextForBeingEmpty(additionalScore)

        val score = Score(checkedMaths, checkedRussian, checkedPhysics,
                checkedComputerScience, checkedSocialScience)

        myApplication.saveScore(score)
        myApplication.saveAdditionalScore(checkedAdditionalScore)
    }
    override fun checkTextForBeingEmpty(text: String?): Int {
        return if (text == null || text == "") {
            0
        } else text.toInt()
    }
    fun checkIntForBeingEmpty(value: Int?): Int {
        return value ?: 0
    }

    override fun returnScore() = myApplication.returnScore()
    fun returnCheckedScore(): Score {
        val score = returnScore()

        val checkedMaths = checkIntForBeingEmpty(score?.maths)
        val checkedRussian = checkIntForBeingEmpty(score?.russian)
        val checkedPhysics = checkIntForBeingEmpty(score?.physics)
        val checkedComputerScience = checkIntForBeingEmpty(score?.computerScience)
        val checkedSocialScience = checkIntForBeingEmpty(score?.socialScience)

        return Score(checkedMaths, checkedRussian, checkedPhysics,
                checkedComputerScience, checkedSocialScience)
    }
    override fun returnAdditionalScore() = myApplication.returnAdditionalScore()
    override fun returnBundleWithListID(listId: Int): Bundle {
        val bundle = Bundle()
        bundle.putInt("listId", listId)

        return bundle
    }
}