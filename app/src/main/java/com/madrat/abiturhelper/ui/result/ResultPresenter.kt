package com.madrat.abiturhelper.ui.result

import android.os.Bundle

class ResultPresenter (private var rv: ResultVP.View, private val arguments: Bundle) : ResultVP.Presenter {

    override fun returnString(bundle: Bundle?, key: String?):String? {
        //return arguments.getInt(key).toString()
        //mathsValue.text = bundle?.getInt("maths").toString()
        return bundle?.getInt(key).toString()
    }

    override fun returnSum():String? {
        val maths = arguments.getInt("maths")
        val russian = arguments.getInt("russian")
        val physics = arguments.getInt("physics")
        val computerScience = arguments.getInt("computerScience")
        val socialScience = arguments.getInt("socialScience")
        return (maths + russian + physics + computerScience + socialScience).toString()
    }
}
