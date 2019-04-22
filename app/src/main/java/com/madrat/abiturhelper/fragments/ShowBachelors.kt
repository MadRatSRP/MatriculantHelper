package com.madrat.abiturhelper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.madrat.abiturhelper.R
import com.madrat.abiturhelper.adapter.BachelorsAdapter
import com.madrat.abiturhelper.adapter.SpecialtiesAdapter
import com.madrat.abiturhelper.model.Specialty
import com.madrat.abiturhelper.model.Student
import com.madrat.abiturhelper.util.linearManager
import kotlinx.android.synthetic.main.fragment_show_bachelors.view.*
import kotlinx.android.synthetic.main.fragment_specialties.*

class ShowBachelors: Fragment() {
    private var adapter: BachelorsAdapter? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val title = arguments?.getString("title")

        (activity as AppCompatActivity).supportActionBar?.title = title
        val view = inflater.inflate(R.layout.fragment_show_bachelors, container, false)

        //val bundle = Bundle()
        @Suppress("UNCHECKED_CAST")
        val list = arguments?.getSerializable("array") as? ArrayList<Student> ?: return null
        //val array = bundle.getSerializable("array")

        adapter = BachelorsAdapter()
        //adapter = SpecialtiesAdapter{specialty: Specialty, position: Int -> onSpecialtyClicked(specialty, position)}
        view.bachelorsRecyclerView.adapter = adapter

        view.bachelorsRecyclerView.linearManager()

        showSpecialties(list)

        return view
    }

    fun showSpecialties(students: ArrayList<Student>) {
        adapter?.updateBachelorsList(students)
        specialtiesRecyclerView?.adapter = adapter
    }
}