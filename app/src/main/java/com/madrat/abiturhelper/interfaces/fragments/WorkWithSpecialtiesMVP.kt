package com.madrat.abiturhelper.interfaces.fragments

import android.content.Context
import android.os.Bundle
import com.madrat.abiturhelper.model.*
import com.madrat.abiturhelper.model.faculties.*
import org.apache.commons.csv.CSVParser
import java.io.InputStream

interface WorkWithSpecialtiesMVP {
    interface View {

        /*fun showFaculties(faculties: ArrayList<Faculty>)
        fun onFacultyClicked(faculty: Faculty, position: Int)
        fun moveToSpecialties(position: Int, titleId: Int)*/
        fun onToCurrentListClicked()

        fun onToResultClicked()
    }
    interface Presenter {
        // Первый этап: считывание данных
        fun generateBachelorsAndSpecialtiesLists(inputStreamToSpecialties: InputStream,
                                                 inputStreamToStudents: InputStream)
        fun getInstanceOfCSVParser(inputStream: InputStream): CSVParser
        fun grabSpecialties(csvParser: CSVParser): ArrayList<Specialty>
        fun grabStudents(csvParser: CSVParser): ArrayList<Student>
        fun checkTextForBeingEmpty(text: String): Int

        // Второй этап: Фильтрация значений
        fun getOnlyNeededValues(listOfSpecialties: ArrayList<Specialty>, listOfStudents: ArrayList<Student>)
        fun filterListOfSpecialtiesByEducationLevel(list: ArrayList<Specialty>): ArrayList<Specialty>?
        fun filterListOfStudentsByAdmissions(list: ArrayList<Student>): ArrayList<Student>
        fun removeValuesWithoutScoreFromListOfStudents(listOfBachelors: ArrayList<Student>): ArrayList<Student>

        // Третий этап: Разбивка данных
        fun categorizeValues(filteredListOfSpecialties: ArrayList<Specialty>,
                             filteredListOfStudents: ArrayList<Student>)
        fun formListOfFacultiesFromListOfSpecialties(listOfSpecialties: ArrayList<Specialty>)
                : ArrayList<ArrayList<Specialty>>
        //fun returnStudentsSeparatedByScoreType(listOfStudents: ArrayList<Student>): ArrayList<ArrayList<Student>>
        fun withdrawStudentsWithSpecificScore(bachelors: ArrayList<Student>, typeOfScoreId: Int): ArrayList<Student>

        // Четвертый этап
        fun separateStudentsBySpecialties(listOfFacultySpecialties: ArrayList<ArrayList<Specialty>>,
                                          filteredListOfStudents: ArrayList<Student>)

        fun checkForSpecialties(listOfStudents: ArrayList<Student>,
                                arrayOfSpecialties: Array<String>): ArrayList<Student>
        /*fun returnListOfStudentsForChosenSpecialty(listOfStudents: ArrayList<Student>,
                                                   arrayOfSpecialties: Array<String>): ArrayList<Student>*/
        // УНТИ
        fun checkForUNTI(listOfStudents: ArrayList<Student>): ArrayList<ArrayList<Student>>

        fun separateUNTI(unti: UNTI): ArrayList<ArrayList<Student>>
        // ФЭУ
        /*fun checkForFEU(scoreTypes: ScoreTypes)
        fun separateFEU(feu: FEU): ArrayList<ArrayList<Student>>
        // ФИТ
        fun checkForFIT(scoreTypes: ScoreTypes)
        fun separateFIT(fit: FIT): ArrayList<ArrayList<Student>>
        // МТФ
        fun checkForMTF(scoreTypes: ScoreTypes)
        fun separateMTF(mtf: MTF): ArrayList<ArrayList<Student>>
        // УНИТ
        fun checkForUNIT(scoreTypes: ScoreTypes)
        fun separateUNIT(unit: UNIT): ArrayList<ArrayList<Student>>
        // ФЭЭ
        fun checkForFEE(scoreTypes: ScoreTypes)
        fun separateFEE(fee: FEE): ArrayList<ArrayList<Student>>*/
        // Четвёртый этап
        // Нахождение минимального балла для каждой из специальностей

        fun checkSpecialtiesForMinimalScore(context: Context)
        fun checkFacultyForMinimalScore(context: Context, facultyId: Int)
                : ArrayList<Specialty>?
        fun getListOfFacultySpecialtiesByFacultyId(facultyId: Int)
                : ArrayList<Specialty>?

        fun returnFacultyList(): ArrayList<Faculty>?
        fun returnFaculties(): Faculties?
        fun returnFacultyBundle(context: Context, position: Int, titleId: Int): Bundle
        fun separateSpecialties(listOfStudents: ArrayList<Student>, arrayOfSpecialties: Array<String>)
                : ArrayList<ArrayList<Student>>

        fun getListOfFacultyStudentsByFacultyId(facultyId: Int): ArrayList<ArrayList<Student>>?
    }
}
