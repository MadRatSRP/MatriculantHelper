package com.madrat.abiturhelper.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.madrat.abiturhelper.R
import com.madrat.abiturhelper.adapter.FacultiesAdapter
import com.madrat.abiturhelper.interfaces.fragments.PickUpSpecialtiesMVP
import com.madrat.abiturhelper.model.*
import com.madrat.abiturhelper.model.faculties.Unti
import com.madrat.abiturhelper.model.faculties.unti.*
import com.madrat.abiturhelper.util.MyApplication
import com.madrat.abiturhelper.util.linearManager
import com.madrat.abiturhelper.util.showLog
import com.madrat.abiturhelper.util.stringAndSerializable
import kotlinx.android.synthetic.main.fragment_pick_up_specialties.*
import kotlinx.android.synthetic.main.fragment_pick_up_specialties.view.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader

class PickUpSpecialtiesView
    : Fragment(), PickUpSpecialtiesMVP.View{
    private var adapter: FacultiesAdapter? = null

    private val myApplication = MyApplication.instance

    private var facultyList = ArrayList<Faculty>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        pickUpSpecialtiesRecyclerView.linearManager()

        /*Первый шаг - разбить список специальностей по факультетам,
          выделить из списка студентов тех, кто собирается поступать на бакалавриат*/
        generateBachelorsAndSpecialtiesLists()

        /*Второй шаг - разбить список поступающих по типу баллов
          и высчитать свободные баллы для факультетов*/
        generateScoreTypedListsAndCalculateAvailableFacultyPlaces()

        showFaculties(facultyList)

        /*Третий шаг - */
        separateStudentsBySpecialties()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.pickUpSpecialtiesTitle)
        val view = inflater.inflate(R.layout.fragment_pick_up_specialties, container, false)

        adapter = FacultiesAdapter{ faculty: Faculty, position: Int -> onFacultyClicked(faculty, position)}
        view.pickUpSpecialtiesRecyclerView.adapter = adapter

        return view
    }

    /*Первый этап*/
    override fun generateBachelorsAndSpecialtiesLists() {
        val specialties = grabSpecialties("specialties.csv")
        val students = grabStudents("abiturs.csv")


        val bachelorsAndSpecialists = divideSpecialtiesByEducationLevel(specialties)

        divideSpecialtiesByFaculty(bachelorsAndSpecialists)


        divideStudentsByAdmissions(students)
        showLog("Первый этап завершён")
    }
    override fun grabSpecialties(path: String): ArrayList<Specialty> {
        val specialtiesList = ArrayList<Specialty>()
        val file = context?.assets?.open(path)
        val bufferedReader = BufferedReader(InputStreamReader(file, "Windows-1251"))

        val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT
               .withFirstRecordAsHeader()
               .withDelimiter(';')
               .withIgnoreHeaderCase()
               .withTrim())

        for (csvRecord in csvParser) {
            val shortName = csvRecord.get("СокращенноеНаименование")
            val fullName = csvRecord.get("ПолноеНаименование")
            val specialty = csvRecord.get("Специализация")
            val profileTerm = csvRecord.get("ПрофильныйПредмет")
            val educationForm = csvRecord.get("ФормаОбучения")
            val educationLevel = csvRecord.get("УровеньПодготовки")
            val graduationReason = csvRecord.get("ОснованиеПоступления")
            val receptionFeatures = csvRecord.get("ОсобенностиПриема")
            val faculty = csvRecord.get("Факультет")
            val entriesAmount = csvRecord.get("КоличествоМест")
            val enrolledAmount = csvRecord.get("Зачислено")

            specialtiesList.add(Specialty(shortName, fullName, specialty, profileTerm, educationForm,
                    educationLevel, graduationReason, receptionFeatures, faculty,
                    entriesAmount.toInt(), enrolledAmount.toInt()))
        }
        showLog("Всего специальностей: ${specialtiesList.size}")
        return specialtiesList
    }
    override fun grabStudents(path: String): ArrayList<Student> {
        val studentsList = ArrayList<Student>()
        val file = context?.assets?.open(path)
        val bufferedReader = BufferedReader(InputStreamReader(file, "Windows-1251"))

        val csvParser = CSVParser(bufferedReader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withDelimiter(';')
                .withIgnoreHeaderCase()
                .withTrim())

        for (csvRecord in csvParser) {
            val studentId: String = csvRecord.get("id")
            val lastName: String = csvRecord.get("Фамилия")
            val firstName: String = csvRecord.get("Имя")
            val patronymic: String = csvRecord.get("Отчество") //?
            val documentsDate: String = csvRecord.get("Дата получения документов")
            val getWay: String = csvRecord.get("Способ получения")
            val status: String = csvRecord.get("Статус")
            val cancelReason: String = csvRecord.get("Причина отклонения")  //?
            val admissions: String = csvRecord.get("Приемная кампания")
            val category: String = csvRecord.get("Категория")
            val specialtyFirst: String = csvRecord.get("Направление1")
            val specialtySecond: String = csvRecord.get("Направление2")
            val specialtyThird: String = csvRecord.get("Направление3")
            val russian: String = csvRecord.get("Русский язык")
            val maths: String = csvRecord.get("Математика")
            val physics: String = csvRecord.get("Физика")
            val computerScience: String = csvRecord.get("Информатика")
            val socialScience: String = csvRecord.get("Обществознание")
            val additionalScore: String = csvRecord.get("Индивидуальные достижения")
            val isCertificateAvailable: String = csvRecord.get("Наличие подлинника аттестата")
            val isChargeAvailable: String = csvRecord.get("Наличие заявления о приеме на Направление1")
            val priority: String = csvRecord.get("Приоритет")

            studentsList.add(Student(studentId, lastName, firstName, patronymic, documentsDate, getWay,
                    status, cancelReason, admissions, category, specialtyFirst, specialtySecond, specialtyThird,
                    russian.toIntOrNull(), maths.toIntOrNull(), physics.toIntOrNull(), computerScience.toIntOrNull(),
                    socialScience.toIntOrNull(), additionalScore.toIntOrNull(), isCertificateAvailable.toBoolean(),
                    isChargeAvailable.toBoolean(), priority.toIntOrNull()))
        }
        showLog("Подавших документы: ${studentsList.size}")
        return studentsList
    }
    override fun divideSpecialtiesByEducationLevel(list: ArrayList<Specialty>): ArrayList<Specialty> {
        val bachelorsAndSpecialists = ArrayList<Specialty>()

        for (i in 0 until list.size) {
            when (list[i].educationLevel) {
                "Академический бакалавр" ->
                    bachelorsAndSpecialists.add(list[i])
                "Специалист" ->
                    bachelorsAndSpecialists.add(list[i])
            }
        }
        showLog("Специальностей, ведущих набор на бакалавриат и специалитет: ${bachelorsAndSpecialists.size}")
        return bachelorsAndSpecialists
    }
    override fun divideSpecialtiesByFaculty(list: ArrayList<Specialty>) {
        val untiList = ArrayList<Specialty>()
        val feuList = ArrayList<Specialty>()
        val fitList = ArrayList<Specialty>()
        val mtfList = ArrayList<Specialty>()
        val unitList = ArrayList<Specialty>()
        val feeList = ArrayList<Specialty>()

        for (i in 0 until list.size) {
            when(list[i].faculty) {
                "Учебно-научный технологический институт" ->
                    untiList.add(list[i])
                "Факультет экономики и управления" ->
                    feuList.add(list[i])
                "Факультет информационных технологий" ->
                    fitList.add(list[i])
                "Механико-технологический факультет" ->
                    mtfList.add(list[i])
                "Учебно-научный институт транспорта" ->
                    unitList.add(list[i])
                "Факультет энергетики и электроники" ->
                    feeList.add(list[i])
            }
        }
        myApplication.saveFaculties(Faculties(untiList, feuList, fitList, mtfList, unitList, feeList))
    }
    override fun divideStudentsByAdmissions(list: ArrayList<Student>) {
        val bachelors = ArrayList<Student>()

        for (i in 0 until list.size) {
            if (list[i].admissions == "бак")
                bachelors.add(list[i])
        }

        showLog("Бакалавры: ${bachelors.size}")

        myApplication.saveBachelors(bachelors)
    }

    /*Второй этап*/
    override fun generateScoreTypedListsAndCalculateAvailableFacultyPlaces() {
        val bachelors = myApplication.returnBachelors()
        showLog("Бакалавры: " + bachelors?.size.toString())

        val new_bachelors = ArrayList<Student>()


        bachelors?.let {
            for (i in 0 until it.size) {
                if ((it[i].maths != null && it[i].maths != 0)
                        && (it[i].russian != null && it[i].russian != 0))  {
                    new_bachelors.add(it[i])
                }
            }
            showLog(new_bachelors.size.toString())
        }


        val scoreTypes = ScoreTypes(
            withdrawPhysicsStudents(new_bachelors),
            withdrawComputerScienceStudents(new_bachelors),
            withdrawSocialScienceStudents(new_bachelors),
            withdrawStudentsWithoutData(new_bachelors),
            withdrawStudentsWithPartAndFullData(new_bachelors)
        )

        myApplication.saveScoreTypes(scoreTypes)

        val faculties = myApplication.returnFaculties()

        facultyList.clear()

        calculateAvailableFacultyPlaces("УНТИ", faculties?.untiList)
        calculateAvailableFacultyPlaces("ФЭУ", faculties?.feuList)
        calculateAvailableFacultyPlaces("ФИТ", faculties?.fitList)
        calculateAvailableFacultyPlaces("МТФ", faculties?.mtfList)
        calculateAvailableFacultyPlaces("УНИТ", faculties?.unitList)
        calculateAvailableFacultyPlaces("ФЭЭ", faculties?.feeList)

        println("Второй этап завершён")
    }
    override fun withdrawPhysicsStudents(bachelors: ArrayList<Student>): ArrayList<Student> {
        val physicsStudents = ArrayList<Student>()

        for (i in 0 until bachelors.size) {
            if (bachelors[i].physics != null && bachelors[i].computerScience == null
                            && bachelors[i].socialScience == null) {
                physicsStudents.add(bachelors[i])
            }
        }
        return physicsStudents
    }
    override fun withdrawComputerScienceStudents(bachelors: ArrayList<Student>): ArrayList<Student> {
        val computerScienceStudents = ArrayList<Student>()

        for (i in 0 until bachelors.size) {
            if (bachelors[i].physics == null && bachelors[i].computerScience != null
                            && bachelors[i].socialScience == null) {
                computerScienceStudents.add(bachelors[i])
            }
        }
        return computerScienceStudents
    }
    override fun withdrawSocialScienceStudents(bachelors: ArrayList<Student>): ArrayList<Student> {
        val socialScienceStudents = ArrayList<Student>()

        for (i in 0 until bachelors.size) {
            if (bachelors[i].physics == null && bachelors[i].computerScience == null
                    && bachelors[i].socialScience != null) {
                socialScienceStudents.add(bachelors[i])
            }
        }
        return socialScienceStudents
    }
    override fun withdrawStudentsWithPartAndFullData(bachelors: ArrayList<Student>): ArrayList<Student> {
        val partAndAllDataStudents = ArrayList<Student>()

        for (i in 0 until bachelors.size) {
            if (!(bachelors[i].physics != null && bachelors[i].computerScience == null && bachelors[i].socialScience == null)
                    && !(bachelors[i].physics == null && bachelors[i].computerScience != null
                    && bachelors[i].socialScience == null) && !(bachelors[i].physics == null
                    && bachelors[i].computerScience == null && bachelors[i].socialScience != null)) {
                partAndAllDataStudents.add(bachelors[i])
            }
        }
        return partAndAllDataStudents
    }
    override fun withdrawStudentsWithoutData(bachelors: ArrayList<Student>): ArrayList<Student> {
        val noOrNotEnoughDataStudents = ArrayList<Student>()

        for (i in 0 until bachelors.size) {
            if (!(bachelors[i].maths != null && bachelors[i].russian != null)) {
                noOrNotEnoughDataStudents.add(bachelors[i])
            }
        }
        return noOrNotEnoughDataStudents
    }
    override fun calculateAvailableFacultyPlaces(name: String, list: ArrayList<Specialty>?) {
        var total = 0
        var free = 0

        list?.let {
            for (i in 0 until it.size) {
                total += it[i].entriesTotal
                free += it[i].entriesFree
            }
        }

        showLog("Для $name - мест всего $total, мест свободно $free")
        facultyList.add(Faculty(name, total, free))
    }

    /*Третий этап*/
    override fun separateStudentsBySpecialties() {
        checkForUnti()
        println("Третий этап завершён")
    }
    override fun checkForUnti() {
        val scoreTypes = myApplication.returnScoreTypes()
        val atp = ArrayList<Student>()
        val kto = ArrayList<Student>()
        val mash = ArrayList<Student>()
        val mitm = ArrayList<Student>()
        val mht = ArrayList<Student>()
        val ptmk = ArrayList<Student>()
        val tmo = ArrayList<Student>()
        val uts = ArrayList<Student>()

        fun checkForATP(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "АТП_заочн_бюдж" || list[i].specialtyFirst == "АТП_заочн_льгот"
                        || list[i].specialtyFirst == "АТП_заочн_плат" || list[i].specialtyFirst == "АТП_очн_бюдж"
                        || list[i].specialtyFirst == "АТП_очн_льгот" || list[i].specialtyFirst == "АТП_очн_плат"
                        || list[i].specialtyFirst == "АТП_очн_целевое") || (list[i].specialtySecond == "АТП_заочн_бюдж"
                        || list[i].specialtySecond == "АТП_заочн_льгот" || list[i].specialtySecond == "АТП_заочн_плат"
                        || list[i].specialtySecond == "АТП_очн_бюдж" || list[i].specialtySecond == "АТП_очн_льгот"
                        || list[i].specialtySecond == "АТП_очн_плат" || list[i].specialtySecond == "АТП_очн_целевое")
                        || (list[i].specialtyThird == "АТП_заочн_бюдж" || list[i].specialtyThird == "АТП_заочн_льгот"
                        || list[i].specialtyThird == "АТП_заочн_плат" || list[i].specialtyThird == "АТП_очн_бюдж"
                        || list[i].specialtyThird == "АТП_очн_льгот" || list[i].specialtyThird == "АТП_очн_плат"
                        || list[i].specialtyThird == "АТП_очн_целевое")) {
                    atp.add(list[i])
                }
            }
        }
        fun checkForKTO(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "КТО(АТиКМ)_очн_бюдж" || list[i].specialtyFirst == "КТО(АТиКМ)_очн_льгот"
                        || list[i].specialtyFirst == "КТО(АТиКМ)_очн_плат" || list[i].specialtyFirst == "КТО(ТМ)_очн_бюдж"
                        || list[i].specialtyFirst == "КТО(ТМ)_очн_льгот" || list[i].specialtyFirst == "КТО(ТМ)_очн_плат"
                        || list[i].specialtyFirst == "КТО(ТМ)_очн_целевое" || list[i].specialtyFirst == "КТО_веч_бюдж"
                        || list[i].specialtyFirst == "КТО_веч_льгот" || list[i].specialtyFirst == "КТО_веч_плат")
                        || (list[i].specialtySecond == "КТО(АТиКМ)_очн_бюдж" || list[i].specialtySecond == "КТО(АТиКМ)_очн_льгот"
                        || list[i].specialtySecond == "КТО(АТиКМ)_очн_плат" || list[i].specialtySecond == "КТО(ТМ)_очн_бюдж"
                        || list[i].specialtySecond == "КТО(ТМ)_очн_льгот" || list[i].specialtySecond == "КТО(ТМ)_очн_плат"
                        || list[i].specialtySecond == "КТО(ТМ)_очн_целевое" || list[i].specialtySecond == "КТО_веч_бюдж"
                        || list[i].specialtySecond == "КТО_веч_льгот" || list[i].specialtySecond == "КТО_веч_плат")
                        || (list[i].specialtyThird == "КТО(АТиКМ)_очн_бюдж" || list[i].specialtyThird == "КТО(АТиКМ)_очн_льгот"
                        || list[i].specialtyThird == "КТО(АТиКМ)_очн_плат" || list[i].specialtyThird == "КТО(ТМ)_очн_бюдж"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_льгот" || list[i].specialtyThird == "КТО(ТМ)_очн_плат"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_целевое" || list[i].specialtyThird == "КТО_веч_бюдж"
                        || list[i].specialtyThird == "КТО_веч_льгот" || list[i].specialtyThird == "КТО_веч_плат")) {
                    kto.add(list[i])
                }
            }
        }
        fun checkForMASH(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "МАШ(ТМ)_заочн_бюдж" || list[i].specialtyFirst == "МАШ(ТМ)_заочн_льгот"
                        || list[i].specialtyFirst == "МАШ(ТМ)_заочн_плат") || (list[i].specialtySecond == "МАШ(ТМ)_заочн_бюдж"
                        || list[i].specialtySecond == "МАШ(ТМ)_заочн_льгот" || list[i].specialtySecond == "МАШ(ТМ)_заочн_плат")
                        || (list[i].specialtyThird == "МАШ(ТМ)_заочн_бюдж" || list[i].specialtyThird == "МАШ(ТМ)_заочн_льгот"
                        || list[i].specialtyThird == "МАШ(ТМ)_заочн_плат")) {
                    mash.add(list[i])
                }
            }
        }
        fun checkForMiTM(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "МиТМ_очн_бюдж" || list[i].specialtyFirst == "МиТМ_очн_льгот"
                        || list[i].specialtyFirst == "МиТМ_очн_плат") || (list[i].specialtySecond == "МиТМ_очн_бюдж"
                        || list[i].specialtySecond == "МиТМ_очн_льгот" || list[i].specialtySecond == "МиТМ_очн_плат")
                        || (list[i].specialtyThird == "МиТМ_очн_бюдж" || list[i].specialtyThird == "МиТМ_очн_льгот"
                        || list[i].specialtyThird == "МиТМ_очн_плат")) {
                    mitm.add(list[i])
                }
            }
        }
        fun checkForMHT(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "МХТ_очн_бюдж" || list[i].specialtyFirst == "МХТ_очн_льгот"
                        || list[i].specialtyFirst == "МХТ_очн_плат") || (list[i].specialtySecond == "МХТ_очн_бюдж"
                        || list[i].specialtySecond == "МХТ_очн_льгот" || list[i].specialtySecond == "МХТ_очн_плат")
                        || (list[i].specialtyThird == "МХТ_очн_бюдж" || list[i].specialtyThird == "МХТ_очн_льгот"
                        || list[i].specialtyThird == "МХТ_очн_плат")) {
                    mht.add(list[i])
                }
            }
        }
        fun checkForPTMK(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПТМК_заочн_плат" || list[i].specialtyFirst == "ПТМК_очн_бюдж"
                        || list[i].specialtyFirst == "ПТМК_очн_льгот" || list[i].specialtyFirst == "ПТМК_очн_плат")
                        || (list[i].specialtySecond == "ПТМК_заочн_плат" || list[i].specialtySecond == "ПТМК_очн_бюдж"
                        || list[i].specialtySecond == "ПТМК_очн_льгот" || list[i].specialtySecond == "ПТМК_очн_плат")
                        || (list[i].specialtyThird == "ПТМК_заочн_плат" || list[i].specialtyThird == "ПТМК_очн_бюдж"
                        || list[i].specialtyThird == "ПТМК_очн_льгот" || list[i].specialtyThird == "ПТМК_очн_плат")) {
                    ptmk.add(list[i])
                }
            }
        }
        fun checkFotTMO(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_бюдж" || list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_льгот"
                        || list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_плат" || list[i].specialtyFirst == "ТМО_очн_бюдж"
                        || list[i].specialtyFirst == "ТМО_очн_льгот" || list[i].specialtyFirst == "ТМО_очн_плат"
                        || list[i].specialtyFirst == "ТМО_очн_целевое") || (list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_бюдж"
                        || list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_льгот" || list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_плат"
                        || list[i].specialtySecond == "ТМО_очн_бюдж" || list[i].specialtySecond == "ТМО_очн_льгот"
                        || list[i].specialtySecond == "ТМО_очн_плат" || list[i].specialtySecond == "ТМО_очн_целевое")
                        || (list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_бюдж" || list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_льгот"
                        || list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_плат" || list[i].specialtyThird == "ТМО_очн_бюдж"
                        || list[i].specialtyThird == "ТМО_очн_льгот" || list[i].specialtyThird == "ТМО_очн_плат"
                        || list[i].specialtyThird == "ТМО_очн_целевое")) {
                    tmo.add(list[i])
                }
            }
        }
        fun checkForUTS(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "УТС_очн_бюдж" || list[i].specialtyFirst == "УТС_очн_льгот"
                        || list[i].specialtyFirst == "УТС_очн_плат" || list[i].specialtyFirst == "УТС_очн_целевое")
                        || (list[i].specialtySecond == "УТС_очн_бюдж" || list[i].specialtySecond == "УТС_очн_льгот"
                        || list[i].specialtySecond == "УТС_очн_плат" || list[i].specialtySecond == "УТС_очн_целевое")
                        || (list[i].specialtyThird == "УТС_очн_бюдж" || list[i].specialtyThird == "УТС_очн_льгот"
                        || list[i].specialtyThird == "УТС_очн_плат" || list[i].specialtyThird == "УТС_очн_целевое")) {
                    uts.add(list[i])
                }
            }
        }

        fun separateATP(list: ArrayList<Student>): ATP {
            val zaochnBudg = ArrayList<Student>()
            val zaochnLgot = ArrayList<Student>()
            val zaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "АТП_заочн_бюдж" || list[i].specialtySecond == "АТП_заочн_бюдж"
                        || list[i].specialtyThird == "АТП_заочн_бюдж")
                    zaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "АТП_заочн_льгот" || list[i].specialtySecond == "АТП_заочн_льгот"
                        || list[i].specialtyThird == "АТП_заочн_льгот")
                    zaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "АТП_заочн_плат" || list[i].specialtySecond == "АТП_заочн_плат"
                        || list[i].specialtyThird == "АТП_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "АТП_очн_бюдж" || list[i].specialtySecond == "АТП_очн_бюдж"
                        || list[i].specialtyThird == "АТП_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "АТП_очн_льгот" || list[i].specialtySecond == "АТП_очн_льгот"
                        || list[i].specialtyThird == "АТП_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "АТП_очн_плат" || list[i].specialtySecond == "АТП_очн_плат"
                        || list[i].specialtyThird == "АТП_очн_плат")
                    ochnPlat.add(list[i])
                else if(list[i].specialtyFirst == "АТП_очн_целевое" || list[i].specialtySecond == "АТП_очн_целевое"
                        || list[i].specialtyThird == "АТП_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            showLog("АТП с размером ${list.size} был разбит на ЗаочноеБюджет - ${zaochnBudg.size}, ЗаочноеЛьгот - ${zaochnLgot.size}, " +
                    "ЗаочноеПлат - ${zaochnPlat.size},\nОчноеБюджет - ${ochnBudg.size}, ОчноеЛьгот -  ${ochnLgot.size}, " +
                    "ОчноеПлат - ${ochnPlat.size}, ОчноеЦелевое -  ${ochnCelevoe.size}")
            return ATP(zaochnBudg, zaochnLgot, zaochnPlat, ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
        }
        fun separateKTO(list: ArrayList<Student>): KTO {
            val atkmOchnBudg = ArrayList<Student>()
            val atkmOchnLgot = ArrayList<Student>()
            val atkmOchnPlat = ArrayList<Student>()
            val tmOchnBudg = ArrayList<Student>()
            val tmOchnLgot = ArrayList<Student>()
            val tmOchnPlat = ArrayList<Student>()
            val tmOchnCelevoe = ArrayList<Student>()
            val vechBudg = ArrayList<Student>()
            val vechLgot = ArrayList<Student>()
            val vechPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "КТО(АТиКМ)_очн_бюдж" || list[i].specialtySecond == "КТО(АТиКМ)_очн_бюдж"
                        || list[i].specialtyThird == "КТО(АТиКМ)_очн_бюдж")
                    atkmOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "КТО(АТиКМ)_очн_льгот" || list[i].specialtySecond == "КТО(АТиКМ)_очн_льгот"
                        || list[i].specialtyThird == "КТО(АТиКМ)_очн_льгот")
                    atkmOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "КТО(АТиКМ)_очн_плат" || list[i].specialtySecond == "КТО(АТиКМ)_очн_плат"
                        || list[i].specialtyThird == "КТО(АТиКМ)_очн_плат")
                    atkmOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "КТО(ТМ)_очн_бюдж" || list[i].specialtySecond == "КТО(ТМ)_очн_бюдж"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_бюдж")
                    tmOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "КТО(ТМ)_очн_льгот" || list[i].specialtySecond == "КТО(ТМ)_очн_льгот"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_льгот")
                    tmOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "КТО(ТМ)_очн_плат" || list[i].specialtySecond == "КТО(ТМ)_очн_плат"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_плат")
                    tmOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "КТО(ТМ)_очн_целевое" || list[i].specialtySecond == "КТО(ТМ)_очн_целевое"
                        || list[i].specialtyThird == "КТО(ТМ)_очн_целевое")
                    tmOchnCelevoe.add(list[i])
                else if (list[i].specialtyFirst == "КТО_веч_бюдж" || list[i].specialtySecond == "КТО_веч_бюдж"
                        || list[i].specialtyThird == "КТО_веч_бюдж")
                    vechBudg.add(list[i])
                else if (list[i].specialtyFirst == "КТО_веч_льгот" || list[i].specialtySecond == "КТО_веч_льгот"
                        || list[i].specialtyThird == "КТО_веч_льгот")
                    vechLgot.add(list[i])
                else if (list[i].specialtyFirst == "КТО_веч_плат" || list[i].specialtySecond == "КТО_веч_плат"
                        || list[i].specialtyThird == "КТО_веч_плат")
                    vechPlat.add(list[i])
            }
            showLog("КТО с размером ${list.size} был разбит на АТиКМ_ОчноеБюджет - ${atkmOchnBudg.size}, " +
                    "АТиКМ_ОчноеЛьготное - ${atkmOchnLgot.size}, АТиКМ_ОчноеПлатное - ${atkmOchnPlat.size}," +
                    "\nТМ_ОчноеБюджет - ${tmOchnBudg.size}, ТМ_ОчноеЛьготное -  ${tmOchnLgot.size}, " +
                    "ТМ_ОчноеПлатное - ${tmOchnPlat.size}, ТМ_ОчноеЦелевое -  ${tmOchnCelevoe.size}, " +
                    "ВечернееБюджет - ${vechBudg.size}, ВечернееЛьготное - ${vechLgot.size}, " +
                    "ВечернееПлатное - ${vechPlat.size}")
            return KTO(atkmOchnBudg, atkmOchnLgot, atkmOchnPlat, tmOchnBudg, tmOchnLgot,
                    tmOchnPlat, tmOchnCelevoe, vechBudg, vechLgot, vechPlat)
        }
        fun separateMASH(list: ArrayList<Student>): MASH {
            val tmZaochnBudg = ArrayList<Student>()
            val tmZaochnLgot = ArrayList<Student>()
            val tmZaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "МАШ(ТМ)_заочн_бюдж" || list[i].specialtySecond == "МАШ(ТМ)_заочн_бюдж"
                        || list[i].specialtyThird == "МАШ(ТМ)_заочн_бюдж")
                    tmZaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(ТМ)_заочн_льгот" || list[i].specialtySecond == "МАШ(ТМ)_заочн_льгот"
                        || list[i].specialtyThird == "МАШ(ТМ)_заочн_льгот")
                    tmZaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(ТМ)_заочн_плат" || list[i].specialtySecond == "МАШ(ТМ)_заочн_плат"
                        || list[i].specialtyThird == "МАШ(ТМ)_заочн_плат")
                    tmZaochnPlat.add(list[i])
            }
            showLog("МАШ с размером ${list.size} был разбит на ТМ_ЗаочноеБюджет - ${tmZaochnBudg.size}, " +
                    "ТМ_ЗаочноеЛьготное - ${tmZaochnLgot.size}, ТМ_ЗаочноеПлатное - ${tmZaochnPlat.size}")
            return MASH(tmZaochnBudg, tmZaochnLgot, tmZaochnPlat)
        }
        fun separateMiTM(list: ArrayList<Student>): MiTM {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "МиТМ_очн_бюдж" || list[i].specialtySecond == "МиТМ_очн_бюдж"
                        || list[i].specialtyThird == "МиТМ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МиТМ_очн_льгот" || list[i].specialtySecond == "МиТМ_очн_льгот"
                        || list[i].specialtyThird == "МиТМ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МиТМ_очн_плат" || list[i].specialtySecond == "МиТМ_очн_плат"
                        || list[i].specialtyThird == "МиТМ_очн_плат")
                    ochnPlat.add(list[i])
            }
            showLog("МиТМ с размером ${list.size} был разбит на ОчноеБюджет - ${ochnBudg.size}, " +
                    "ОчноеЛьготное - ${ochnLgot.size}, ОчноеПлатное - ${ochnPlat.size}")
            return MiTM(ochnBudg, ochnLgot, ochnPlat)
        }
        fun separateMHT(list: ArrayList<Student>): MHT {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "МХТ_очн_бюдж" || list[i].specialtySecond == "МХТ_очн_бюдж"
                        || list[i].specialtyThird == "МХТ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МХТ_очн_льгот" || list[i].specialtySecond == "МХТ_очн_льгот"
                        || list[i].specialtyThird == "МХТ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МХТ_очн_плат" || list[i].specialtySecond == "МХТ_очн_плат"
                        || list[i].specialtyThird == "МХТ_очн_плат")
                    ochnPlat.add(list[i])
            }
            showLog("МХТ с размером ${list.size} был разбит на ОчноеБюджет - ${ochnBudg.size}, " +
                    "ОчноеЛьготное - ${ochnLgot.size}, ОчноеПлатное - ${ochnPlat.size}")
            return MHT(ochnBudg, ochnLgot, ochnPlat)
        }
        fun separatePTMK(list: ArrayList<Student>): PTMK {
            val zaochnBudg = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПТМК_заочн_плат" || list[i].specialtySecond == "ПТМК_заочн_плат"
                        || list[i].specialtyThird == "ПТМК_заочн_плат")
                    zaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПТМК_очн_бюдж" || list[i].specialtySecond == "ПТМК_очн_бюдж"
                        || list[i].specialtyThird == "ПТМК_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПТМК_очн_льгот" || list[i].specialtySecond == "ПТМК_очн_льгот"
                        || list[i].specialtyThird == "ПТМК_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПТМК_очн_плат" || list[i].specialtySecond == "ПТМК_очн_плат"
                        || list[i].specialtyThird == "ПТМК_очн_плат")
                    ochnPlat.add(list[i])
            }
            showLog("ПТМК с размером ${list.size} был разбит на ЗаочноеБюджет - ${zaochnBudg.size}, " +
                    "ОчноеБюджет - ${ochnBudg.size}, ОчноеЛьготное - ${ochnLgot.size}, " +
                    "ОчноеПлатное - ${ochnPlat.size}")
            return PTMK(zaochnBudg, ochnBudg, ochnLgot, ochnPlat)
        }
        fun separateTMO(list: ArrayList<Student>): TMO {
            val oipmZaochnBudg = ArrayList<Student>()
            val oipmZaochnLgot = ArrayList<Student>()
            val oipmZaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_бюдж" || list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_бюдж"
                        || list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_бюдж")
                    oipmZaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_льгот" || list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_льгот"
                        || list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_льгот")
                    oipmZaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТМО(ОИиПМ)_заочн_плат" || list[i].specialtySecond == "ТМО(ОИиПМ)_заочн_плат"
                        || list[i].specialtyThird == "ТМО(ОИиПМ)_заочн_плат")
                    oipmZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТМО_очн_бюдж" || list[i].specialtySecond == "ТМО_очн_бюдж"
                        || list[i].specialtyThird == "ТМО_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТМО_очн_льгот" || list[i].specialtySecond == "ТМО_очн_льгот"
                        || list[i].specialtyThird == "ТМО_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТМО_очн_плат" || list[i].specialtySecond == "ТМО_очн_плат"
                        || list[i].specialtyThird == "ТМО_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТМО_очн_целевое" || list[i].specialtySecond == "ТМО_очн_целевое"
                        || list[i].specialtyThird == "ТМО_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            showLog("ТМО с размером ${list.size} был разбит на ОИиПМ_ЗаочноеБюджет - ${oipmZaochnBudg.size}, " +
                    "ОИиПМ_ЗаочноеЛьготное - ${oipmZaochnLgot.size}, ОИиПМ_ЗаочноеПлатное - ${oipmZaochnPlat.size}, " +
                    "\nОчноеБюджет - ${ochnBudg.size}, ОчноеЛьготное - ${ochnLgot.size}, ОчноеПлатное - ${ochnPlat.size}, " +
                    "ОчноеЦелевое - ${ochnCelevoe.size}")
            return TMO(oipmZaochnBudg, oipmZaochnLgot, oipmZaochnPlat, ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
        }
        fun separateUTS(list: ArrayList<Student>): UTS {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "УТС_очн_бюдж" || list[i].specialtySecond == "УТС_очн_бюдж"
                        || list[i].specialtyThird == "УТС_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "УТС_очн_льгот" || list[i].specialtySecond == "УТС_очн_льгот"
                        || list[i].specialtyThird == "УТС_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "УТС_очн_плат" || list[i].specialtySecond == "УТС_очн_плат"
                        || list[i].specialtyThird == "УТС_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "УТС_очн_целевое" || list[i].specialtySecond == "УТС_очн_целевое"
                        || list[i].specialtyThird == "УТС_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            showLog("УТС с размером ${list.size} был разбит на ОчноеБюджет - ${ochnBudg.size}, " +
                    "ОчноеЛьготное - ${ochnLgot.size}, ОчноеПлатное - ${ochnPlat.size}, ОчноеЦелевое - ${ochnCelevoe.size}")
            return UTS(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
        }

        scoreTypes?.physicsStudents?.let { checkForATP(it) }
        scoreTypes?.computerScienceStudents?.let { checkForATP(it) }
        scoreTypes?.socialScienceStudents?.let { checkForATP(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForATP(it) }

        scoreTypes?.physicsStudents?.let { checkForKTO(it) }
        scoreTypes?.computerScienceStudents?.let { checkForKTO(it) }
        scoreTypes?.socialScienceStudents?.let { checkForKTO(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForKTO(it) }

        scoreTypes?.physicsStudents?.let { checkForMASH(it) }
        scoreTypes?.computerScienceStudents?.let { checkForMASH(it) }
        scoreTypes?.socialScienceStudents?.let { checkForMASH(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForMASH(it) }

        scoreTypes?.physicsStudents?.let { checkForMiTM(it) }
        scoreTypes?.computerScienceStudents?.let { checkForMiTM(it) }
        scoreTypes?.socialScienceStudents?.let { checkForMiTM(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForMiTM(it) }

        scoreTypes?.physicsStudents?.let { checkForMHT(it) }
        scoreTypes?.computerScienceStudents?.let { checkForMHT(it) }
        scoreTypes?.socialScienceStudents?.let { checkForMHT(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForMHT(it) }

        scoreTypes?.physicsStudents?.let { checkForPTMK(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPTMK(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPTMK(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPTMK(it) }

        scoreTypes?.physicsStudents?.let { checkFotTMO(it) }
        scoreTypes?.computerScienceStudents?.let { checkFotTMO(it) }
        scoreTypes?.socialScienceStudents?.let { checkFotTMO(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkFotTMO(it) }

        scoreTypes?.physicsStudents?.let { checkForUTS(it) }
        scoreTypes?.computerScienceStudents?.let { checkForUTS(it) }
        scoreTypes?.socialScienceStudents?.let { checkForUTS(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForUTS(it) }

        val atpSeparated = separateATP(atp)
        val ktoSeparated = separateKTO(kto)
        val mashSeparated = separateMASH(mash)
        val mitmSeparated = separateMiTM(mitm)
        val mhtSeparated = separateMHT(mht)
        val ptmkSeparated = separatePTMK(ptmk)
        val tmoSeparated = separateTMO(tmo)
        val utsSeparated = separateUTS(uts)

        val unti = Unti(atpSeparated, ktoSeparated, mashSeparated, mitmSeparated, mhtSeparated,
                ptmkSeparated, tmoSeparated, utsSeparated)
        myApplication.saveUnti(unti)
    }


    override fun showFaculties(faculties: ArrayList<Faculty>) {
        pickUpSpecialtiesRecyclerView.post {
            adapter?.updateFacultiesList(faculties)
            pickUpSpecialtiesRecyclerView.adapter = adapter
        }

        /*activity?.runOnUiThread {
            adapter?.updateFacultiesList(faculties)
            pickUpSpecialtiesRecyclerView.adapter = adapter
        }*/
    }
    override fun onFacultyClicked(faculty: Faculty, position: Int) {
        showLog("Выбран: ${faculty.name}")
        val bundle = Bundle()
        val faculties = myApplication.returnFaculties()

        fun moveToSpecialties(position: Int, title: String, list: ArrayList<Specialty>) {
            bundle.stringAndSerializable(title, list)
            bundle.putInt("pos", position)
            toSpecialties(bundle)
        }
        faculties?.let {
            when (position) {
                //УНТИ
                0 -> moveToSpecialties(position,"УНТИ", it.untiList)
                //ФЭУ
                1 -> moveToSpecialties(position,"ФЭУ", it.feuList)
                //ФИТ
                2 -> moveToSpecialties(position,"ФИТ", it.fitList)
                //МТФ
                3 -> moveToSpecialties(position,"МТФ", it.mtfList)
                //УНИТ
                4 -> moveToSpecialties(position,"УНИТ", it.unitList)
                //ФЭЭ
                5 -> moveToSpecialties(position,"ФЭЭ", it.feeList)
            }
        }
    }

    override fun toSpecialties(bundle: Bundle) {
        view?.let {
            Navigation.findNavController(it)
                .navigate(R.id.action_pickUpSpecialtiesView_to_showSpecialtiesView, bundle)
        }
    }
}