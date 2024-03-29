package com.madrat.abiturhelper.interfaces.fragments

import android.content.Context
import android.os.Bundle
import com.madrat.abiturhelper.model.*
import com.madrat.abiturhelper.model.faculties.*

interface WorkWithSpecialtiesMVP {
    interface View {
        fun setupMVP()
        fun toActionId(actionId: Int)

        /*fun showFaculties(faculties: ArrayList<Faculty>)
        fun onFacultyClicked(faculty: Faculty, position: Int)
        fun moveToSpecialties(position: Int, titleId: Int)*/
    }
    interface Presenter {
        //Первый этап
        fun generateBachelorsAndSpecialtiesLists(context: Context)
        fun grabSpecialties(context: Context, path: String): ArrayList<Specialty>
        fun grabStudents(context: Context, path: String): ArrayList<Student>
        fun checkTextForBeingEmpty(text: String): Int
        fun divideSpecialtiesByEducationLevel(list: ArrayList<Specialty>): ArrayList<Specialty>?
        fun divideSpecialtiesByFaculty(list: ArrayList<Specialty>)
        fun divideStudentsByAdmissions(list: ArrayList<Student>)
        //Второй этап
        fun generateScoreTypedListsAndCalculateAvailableFacultyPlaces()
        fun returnStudentsSeparatedByScoreType(): ScoreTypes
        fun returnListOfFaculties(): ArrayList<Faculty>
        fun withdrawPhysicsStudents(bachelors: ArrayList<Student>): ArrayList<Student>
        fun withdrawComputerScienceStudents(bachelors: ArrayList<Student>): ArrayList<Student>
        fun withdrawSocialScienceStudents(bachelors: ArrayList<Student>): ArrayList<Student>
        fun withdrawStudentsWithPartAndFullData(bachelors: ArrayList<Student>): ArrayList<Student>
        fun calculateAvailableFacultyPlaces(name: String, list: ArrayList<Specialty>?): Faculty

        //Третий этап

        fun separateStudentsBySpecialties()
        // УНТИ
        fun checkForUNTI()
        fun separateUNTI(unti: UNTI): ArrayList<ArrayList<Student>>
        fun separateForATP(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForKTO(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForMASH(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForMiTM(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForMHT(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForPTMK(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForTMO(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateForUTS(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        // ФЭУ
        fun checkForFEU()
        fun separateFEU(feu: FEU): ArrayList<ArrayList<Student>>
        fun separateBI(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separatePI(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateSC(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateTD(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateEB(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateEK(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        // ФИТ
        fun checkForFIT()
        fun separateFIT(fit: FIT): ArrayList<ArrayList<Student>>
        fun separateIASB(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateIB(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateIBAS(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateIVT(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateINN(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateIST(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateMOA(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separatePRI(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separatePRO(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        // МТФ
        fun checkForMTF()
        fun separateMTF(mtf: MTF): ArrayList<ArrayList<Student>>
        fun separateMASH(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateSIM(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateTB(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateUK(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        // УНИТ
        fun checkForUNIT()
        fun separateUNIT(unit: UNIT): ArrayList<ArrayList<Student>>
        fun separateNTTK(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateNTTS(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separatePM(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separatePSJD(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateTTP(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateETTK(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        // ФЭЭ
        fun checkForFEE()
        fun separateFEE(fee: FEE): ArrayList<ArrayList<Student>>
        fun separateRAD(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateTIT(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateEIN(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateEIE(list: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun separateEM(list: ArrayList<Student>): ArrayList<ArrayList<Student>>

        // Четвёртый этап
        // Нахождение минимального балла для каждой из специальностей

        fun checkSpecialtiesForMinimalScore(context: Context)
        fun checkUNTIForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun checkFEUForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun checkFITForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun checkMTFForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun checkUNITForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun checkFEEForMinimalScore(context: Context, position: Int): ArrayList<Specialty>?
        fun getSpecialtiesListByPosition(pos: Int): ArrayList<Specialty>?

        fun returnFacultyList(): ArrayList<Faculty>?
        fun returnFaculties(): Faculties?
        fun returnFacultyBundle(context: Context, position: Int, titleId: Int): Bundle
        fun returnUNTI(): ArrayList<ArrayList<Student>>?
        fun returnFEU(): ArrayList<ArrayList<Student>>?
        fun returnFIT(): ArrayList<ArrayList<Student>>?
        fun returnMTF(): ArrayList<ArrayList<Student>>?
        fun returnUNIT(): ArrayList<ArrayList<Student>>?
        fun returnFEE(): ArrayList<ArrayList<Student>>?
    }
    interface Repository {

    }
}
