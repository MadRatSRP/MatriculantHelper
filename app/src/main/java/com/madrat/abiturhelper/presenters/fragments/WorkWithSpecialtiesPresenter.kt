package com.madrat.abiturhelper.presenters.fragments

import android.content.Context
import android.os.Bundle
import com.madrat.abiturhelper.R
import com.madrat.abiturhelper.interfaces.fragments.WorkWithSpecialtiesMVP
import com.madrat.abiturhelper.model.*
import com.madrat.abiturhelper.util.MyApplication
import com.madrat.abiturhelper.util.showLog
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.measureTimeMillis

class WorkWithSpecialtiesPresenter(private var pv: WorkWithSpecialtiesMVP.View,
                                   private var pr: WorkWithSpecialtiesMVP.Repository)
    : WorkWithSpecialtiesMVP.Presenter{
    private val myApplication = MyApplication.instance

    /*Первый этап*/
    override fun generateBachelorsAndSpecialtiesLists(context: Context) {
        showLog("Начат первый этап")
        val time = measureTimeMillis {
            val specialties = grabSpecialties(context, "specialties.csv")
            val bachelorsAndSpecialists = divideSpecialtiesByEducationLevel(specialties)
            divideSpecialtiesByFaculty(bachelorsAndSpecialists)

            val students = grabStudents(context, "abiturs.csv")
            divideStudentsByAdmissions(students)
        }
        showLog("Первый этап завершён за $time ms")
    }
    override fun grabSpecialties(context: Context, path: String): ArrayList<Specialty> {
        val specialtiesList = ArrayList<Specialty>()
        val file = context.assets?.open(path)
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
    override fun grabStudents(context: Context, path: String): ArrayList<Student> {
        val studentsList = ArrayList<Student>()
        val file = context.assets?.open(path)
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
                    checkTextForBeingEmpty(russian), checkTextForBeingEmpty(maths), checkTextForBeingEmpty(physics),
                    checkTextForBeingEmpty(computerScience), checkTextForBeingEmpty(socialScience),
                    additionalScore.toIntOrNull(), isCertificateAvailable.toBoolean(),
                    isChargeAvailable.toBoolean(), priority.toIntOrNull()))
        }
        showLog("Подавших документы: ${studentsList.size}")
        return studentsList
    }
    override fun checkTextForBeingEmpty(text: String): Int {
        return if (text.isEmpty()) {
            0
        } else text.toInt()
    }
    override fun divideSpecialtiesByEducationLevel(list: ArrayList<Specialty>): ArrayList<Specialty> {
        val bachelorsAndSpecialists = list.filter {
            it.educationLevel == "Академический бакалавр" || it.educationLevel == "Специалист" }
                as ArrayList<Specialty>
        showLog("Специальностей, ведущих набор на бакалавриат и специалитет: ${bachelorsAndSpecialists.size}")
        return bachelorsAndSpecialists
    }
    override fun divideSpecialtiesByFaculty(list: ArrayList<Specialty>) {
        // УНТИ
        val listUNTI = list.filter { it.faculty == "Учебно-научный технологический институт" }
                as ArrayList<Specialty>
        // ФЭУ
        val listFEU = list.filter { it.faculty == "Факультет экономики и управления" }
                as ArrayList<Specialty>
        // ФИТ
        val listFIT = list.filter { it.faculty == "Факультет информационных технологий" }
                as ArrayList<Specialty>
        // МТФ
        val listMTF = list.filter { it.faculty == "Механико-технологический факультет" }
                as ArrayList<Specialty>
        // УНИТ
        val listUNIT = list.filter { it.faculty == "Учебно-научный институт транспорта" }
                as ArrayList<Specialty>
        // ФЭЭ
        val listFEE = list.filter { it.faculty == "Факультет энергетики и электроники" }
                as ArrayList<Specialty>

        myApplication.saveFaculties(Faculties(listUNTI, listFEU, listFIT, listMTF, listUNIT, listFEE))
    }
    override fun divideStudentsByAdmissions(list: ArrayList<Student>) {
        val bachelors = list.filter { it.admissions == "бак"} as ArrayList<Student>
        myApplication.saveBachelors(bachelors)
    }

    /*Второй этап*/
    override fun generateScoreTypedListsAndCalculateAvailableFacultyPlaces() {
        showLog("Начат второй этап")
        val time = measureTimeMillis {
            // Получить и сохранить списки студентов, разделённые по баллам
            val scoreTypes = returnStudentsSeparatedByScoreType()
            myApplication.saveScoreTypes(scoreTypes)

            // Получаем и сохраняем количество общих и свободных мест для каждого из факультетов
            val listOfFaculties = returnListOfFaculties()
            myApplication.saveFacultyList(listOfFaculties)
        }
        showLog("Второй этап завершён за $time ms")
    }
    override fun returnStudentsSeparatedByScoreType(): ScoreTypes {
        val bachelors = myApplication.returnBachelors()

        // Вычисляем количество студентов, у которых достаточно баллов
        val studentsWithEnoughData = bachelors?.filter {
            it.maths != 0 && it.russian != 0 && (it.physics != 0 || it.computerScience != 0
                    || it.socialScience != 0) } as ArrayList<Student>
        showLog("Студентов, чьих баллов достаточно: ${studentsWithEnoughData.size}")

        // Вычисляем количество студентов, у которых недостаточно баллов по предметам
        val studentsWithoutEnoughData = bachelors.filter {
            (it.maths == 0 && it.russian == 0) || it.maths == 0 || it.russian == 0
                    || (it.physics == 0 && it.computerScience == 0 && it.socialScience == 0)} as ArrayList<Student>

        return ScoreTypes(
                withdrawPhysicsStudents(studentsWithEnoughData),
                withdrawComputerScienceStudents(studentsWithEnoughData),
                withdrawSocialScienceStudents(studentsWithEnoughData),
                studentsWithoutEnoughData,
                withdrawStudentsWithPartAndFullData(studentsWithEnoughData)
        )
    }
    override fun returnListOfFaculties(): ArrayList<Faculty> {
        val facultyList = ArrayList<Faculty>()
        val faculties = myApplication.returnFaculties()
        //facultyList.clear()

        val calculatedPlacesUNTI = calculateAvailableFacultyPlaces("УНТИ", faculties?.untiList)
        val calculatedPlacesFEU = calculateAvailableFacultyPlaces("ФЭУ", faculties?.feuList)
        val calculatedPlacesFIT = calculateAvailableFacultyPlaces("ФИТ", faculties?.fitList)
        val calculatedPlacesMTF = calculateAvailableFacultyPlaces("МТФ", faculties?.mtfList)
        val calculatedPlacesUNIT = calculateAvailableFacultyPlaces("УНИТ", faculties?.unitList)
        val calculatedPlacesFEE = calculateAvailableFacultyPlaces("ФЭЭ", faculties?.feeList)

        val collection = arrayListOf(calculatedPlacesUNTI, calculatedPlacesFEU, calculatedPlacesFIT,
                calculatedPlacesMTF, calculatedPlacesUNIT, calculatedPlacesFEE)
        facultyList.addAll(collection)
        return facultyList
    }
    // Физика
    override fun withdrawPhysicsStudents(bachelors: ArrayList<Student>)
            = bachelors.filter { it.physics != 0 && it.computerScience == 0 && it.socialScience == 0 } as ArrayList<Student>
    // Информатика
    override fun withdrawComputerScienceStudents(bachelors: ArrayList<Student>)
            = bachelors.filter { it.physics == 0 && it.computerScience != 0 && it.socialScience == 0 } as ArrayList<Student>
    // Обществознание
    override fun withdrawSocialScienceStudents(bachelors: ArrayList<Student>)
            = bachelors.filter { it.physics == 0 && it.computerScience == 0 && it.socialScience != 0 } as ArrayList<Student>
    // Баллы по двум/трем предметам
    override fun withdrawStudentsWithPartAndFullData(bachelors: ArrayList<Student>)
            = bachelors.filter { (it.physics != 0 && it.computerScience != 0 && it.socialScience != 0)
            || (it.physics != 0 && it.computerScience != 0 && it.socialScience == 0)
            || (it.physics != 0 && it.computerScience == 0 && it.socialScience != 0)
            || (it.physics == 0 && it.computerScience != 0 && it.socialScience != 0) } as ArrayList<Student>
    override fun calculateAvailableFacultyPlaces(name: String, list: ArrayList<Specialty>?)
            : Faculty {
        val total = list?.sumBy { it.entriesTotal }
        val free = list?.sumBy { it.entriesFree }
        val amountOfSpecialties = list?.size
        return Faculty(name, total, free, amountOfSpecialties)
    }

    /*Третий этап*/
    override fun separateStudentsBySpecialties() {
        checkForUnti()
        checkForFEU()
        checkForFIT()
        checkForMTF()
        checkForUNIT()
        checkForFEE()
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

        val listUNTI = ArrayList<ArrayList<Student>>()

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

        fun separateATP(list: ArrayList<Student>)/*: ATP*/ {
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
            val collection = arrayListOf(zaochnBudg, zaochnLgot, zaochnPlat, ochnBudg,
                    ochnLgot, ochnPlat, ochnCelevoe)
            listUNTI.addAll(collection)
        }
        fun separateKTO(list: ArrayList<Student>)/*: KTO*/ {
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
            val collection = arrayListOf(atkmOchnBudg, atkmOchnLgot, atkmOchnPlat, tmOchnBudg, tmOchnLgot,
                    tmOchnPlat, tmOchnCelevoe, vechBudg, vechLgot, vechPlat)
            listUNTI.addAll(collection)
        }
        fun separateMASH(list: ArrayList<Student>)/*: MASH*/ {
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
            val collection = arrayListOf(tmZaochnBudg, tmZaochnLgot, tmZaochnPlat)
            listUNTI.addAll(collection)
        }
        fun separateMiTM(list: ArrayList<Student>)/*: MiTM*/ {
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
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat)
            listUNTI.addAll(collection)
        }
        fun separateMHT(list: ArrayList<Student>)/*: MHT*/ {
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
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat)
            listUNTI.addAll(collection)
        }
        fun separatePTMK(list: ArrayList<Student>)/*: PTMK*/ {
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
            val collection = arrayListOf(zaochnBudg, ochnBudg, ochnLgot, ochnPlat)
            listUNTI.addAll(collection)
        }
        fun separateTMO(list: ArrayList<Student>)/*: TMO*/ {
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
            val collection = arrayListOf(oipmZaochnBudg, oipmZaochnLgot, oipmZaochnPlat,
                    ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listUNTI.addAll(collection)
        }
        fun separateUTS(list: ArrayList<Student>)/*: UTS*/ {
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
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listUNTI.addAll(collection)
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

        /*val atpSeparated = */separateATP(atp)
        /*val ktoSeparated = */separateKTO(kto)
        /*val mashSeparated = */separateMASH(mash)
        /*val mitmSeparated = */separateMiTM(mitm)
        /*val mhtSeparated = */separateMHT(mht)
        /*val ptmkSeparated = */separatePTMK(ptmk)
        /*val tmoSeparated = */separateTMO(tmo)
        /*val utsSeparated = */separateUTS(uts)

        /*val unti = Unti(atpSeparated, ktoSeparated, mashSeparated, mitmSeparated, mhtSeparated,
                ptmkSeparated, tmoSeparated, utsSeparated)*/
        myApplication.saveUnti(listUNTI)
    }
    override fun checkForFEU() {
        val scoreTypes = myApplication.returnScoreTypes()
        val bi = ArrayList<Student>()
        val pi = ArrayList<Student>()
        val sc = ArrayList<Student>()
        val td = ArrayList<Student>()
        val eb = ArrayList<Student>()
        val ek = ArrayList<Student>()
        var check = ArrayList<Student>()

        val listFEU = ArrayList<ArrayList<Student>>()

        fun checkForBI(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "БИ_заочн_плат" || list[i].specialtyFirst == "БИ_очн_плат")
                        || (list[i].specialtySecond == "БИ_заочн_плат" || list[i].specialtySecond == "БИ_очн_плат")
                        || (list[i].specialtyThird == "БИ_заочн_плат" || list[i].specialtyThird == "БИ_очн_плат")) {
                    bi.add(list[i])
                }
            }
        }
        fun checkForPI(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПИ(КИС)_очн_бюдж" || list[i].specialtyFirst == "ПИ(КИС)_очн_льгот"
                                || list[i].specialtyFirst == "ПИ(КИС)_очн_плат" || list[i].specialtyFirst == "ПИ(ЦЭ)_очн_бюдж"
                                || list[i].specialtyFirst == "ПИ(ЦЭ)_очн_льгот" || list[i].specialtyFirst == "ПИ(ЦЭ)_очн_плат")
                        || (list[i].specialtySecond == "ПИ(КИС)_очн_бюдж" || list[i].specialtySecond == "ПИ(КИС)_очн_льгот"
                                || list[i].specialtySecond == "ПИ(КИС)_очн_плат" || list[i].specialtySecond == "ПИ(ЦЭ)_очн_бюдж"
                                || list[i].specialtySecond == "ПИ(ЦЭ)_очн_льгот" || list[i].specialtySecond == "ПИ(ЦЭ)_очн_плат")
                        || (list[i].specialtyThird == "ПИ(КИС)_очн_бюдж" || list[i].specialtyThird == "ПИ(КИС)_очн_льгот"
                                || list[i].specialtyThird == "ПИ(КИС)_очн_плат" || list[i].specialtyThird == "ПИ(ЦЭ)_очн_бюдж"
                                || list[i].specialtyThird == "ПИ(ЦЭ)_очн_льгот" || list[i].specialtyThird == "ПИ(ЦЭ)_очн_плат")) {
                    pi.add(list[i])
                }
            }
        }
        fun checkForSC(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "СЦ_заочн_плат" || list[i].specialtyFirst == "СЦ_очн_плат")
                        || (list[i].specialtySecond == "СЦ_заочн_плат" || list[i].specialtySecond == "СЦ_очн_плат")
                        || (list[i].specialtyThird == "СЦ_заочн_плат" || list[i].specialtyThird == "СЦ_очн_плат")) {
                    sc.add(list[i])
                }
            }
        }
        fun checkForTD(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ТД_заочн_плат" || list[i].specialtyFirst == "ТД_очн_плат")
                        || (list[i].specialtySecond == "ТД_заочн_плат" || list[i].specialtySecond == "ТД_очн_плат")
                        || (list[i].specialtyThird == "ТД_заочн_плат" || list[i].specialtyThird == "ТД_очн_плат")) {
                    td.add(list[i])
                }
            }
        }
        fun checkForEB(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭБ_заоч_плат" || list[i].specialtyFirst == "ЭБ_очн_плат")
                        || (list[i].specialtySecond == "ЭБ_заоч_плат" || list[i].specialtySecond == "ЭБ_очн_плат")
                        || (list[i].specialtyThird == "ЭБ_заоч_плат" || list[i].specialtyThird == "ЭБ_очн_плат")) {
                    eb.add(list[i])
                }
            }
        }
        fun checkForEK(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭК(БУА)_заоч_плат" || list[i].specialtyFirst == "ЭК(БУА)_очн_плат"
                                || list[i].specialtyFirst == "ЭК(ЛОГ)_очн_плат" || list[i].specialtyFirst == "ЭК(ОЦ)_очн_плат"
                                || list[i].specialtyFirst == "ЭК(Ф)_заоч_плат" || list[i].specialtyFirst == "ЭК(Ф)_очн_плат"
                                || list[i].specialtyFirst == "ЭК(ЭПО)_очн_плат") || (list[i].specialtySecond == "ЭК(БУА)_заоч_плат"
                                || list[i].specialtySecond == "ЭК(БУА)_очн_плат" || list[i].specialtySecond == "ЭК(ЛОГ)_очн_плат"
                                || list[i].specialtySecond == "ЭК(ОЦ)_очн_плат" || list[i].specialtySecond == "ЭК(Ф)_заоч_плат"
                                || list[i].specialtySecond == "ЭК(Ф)_очн_плат" || list[i].specialtySecond == "ЭК(ЭПО)_очн_плат")
                        || (list[i].specialtyThird == "ЭК(БУА)_заоч_плат" || list[i].specialtyThird == "ЭК(БУА)_очн_плат"
                                || list[i].specialtyThird == "ЭК(ЛОГ)_очн_плат" || list[i].specialtyThird == "ЭК(ОЦ)_очн_плат"
                                || list[i].specialtyThird == "ЭК(Ф)_заоч_плат" || list[i].specialtyThird == "ЭК(Ф)_очн_плат"
                                || list[i].specialtyThird == "ЭК(ЭПО)_очн_плат")) {
                    ek.add(list[i])
                }
            }
        }

        /*fun check(list: ArrayList<Student>) {
           val filteredMap = list.filter { it.specialtyFirst == "ПИ(КИС)_очн_бюдж" /*|| it.specialtyFirst == "ПИ(КИС)_очн_льгот"*/ }
           check = filteredMap as ArrayList<Student>
           showLog("CHECK = " + check.size.toString())
       }*/

        fun separateBI(list: ArrayList<Student>)/*: BI*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "БИ_заочн_плат" || list[i].specialtySecond == "БИ_заочн_плат"
                        || list[i].specialtyThird == "БИ_заочн_плат") {
                    zaochnPlat.add(list[i])
                }
                else if (list[i].specialtyFirst == "БИ_очн_плат" || list[i].specialtySecond == "БИ_очн_плат"
                        || list[i].specialtyThird == "БИ_очн_плат")
                    ochnPlat.add(list[i])
            }

            val collection = arrayListOf(zaochnPlat, ochnPlat)
            listFEU.addAll(collection)
        }
        fun separatePI(list: ArrayList<Student>)/*: PI*/ {
            val kisOchnBudg = ArrayList<Student>()
            val kisOchnLgot = ArrayList<Student>()
            val kisOchnPlat = ArrayList<Student>()
            val ceOchnBudg = ArrayList<Student>()
            val ceOchnLgot = ArrayList<Student>()
            val ceOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПИ(КИС)_очн_бюдж" || list[i].specialtySecond == "ПИ(КИС)_очн_бюдж"
                        || list[i].specialtyThird == "ПИ(КИС)_очн_бюдж")
                    kisOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПИ(КИС)_очн_льгот" || list[i].specialtySecond == "ПИ(КИС)_очн_льгот"
                        || list[i].specialtyThird == "ПИ(КИС)_очн_льгот")
                    kisOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПИ(КИС)_очн_плат" || list[i].specialtySecond == "ПИ(КИС)_очн_плат"
                        || list[i].specialtyThird == "ПИ(КИС)_очн_плат")
                    kisOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПИ(ЦЭ)_очн_бюдж" || list[i].specialtySecond == "ПИ(ЦЭ)_очн_бюдж"
                        || list[i].specialtyThird == "ПИ(ЦЭ)_очн_бюдж")
                    ceOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПИ(ЦЭ)_очн_льгот" || list[i].specialtySecond == "ПИ(ЦЭ)_очн_льгот"
                        || list[i].specialtyThird == "ПИ(ЦЭ)_очн_льгот")
                    ceOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПИ(ЦЭ)_очн_плат" || list[i].specialtySecond == "ПИ(ЦЭ)_очн_плат"
                        || list[i].specialtyThird == "ПИ(ЦЭ)_очн_плат")
                    ceOchnPlat.add(list[i])
            }

            val collection = arrayListOf(kisOchnBudg, kisOchnLgot, kisOchnPlat,
                    ceOchnBudg, ceOchnLgot, ceOchnPlat)
            listFEU.addAll(collection)
        }
        fun separateSC(list: ArrayList<Student>)/*: SC*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "СЦ_заочн_плат" || list[i].specialtySecond == "СЦ_заочн_плат"
                        || list[i].specialtyThird == "СЦ_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "СЦ_очн_плат" || list[i].specialtySecond == "СЦ_очн_плат"
                        || list[i].specialtyThird == "СЦ_очн_плат")
                    ochnPlat.add(list[i])
            }

            val collection = arrayListOf(zaochnPlat, ochnPlat)
            listFEU.addAll(collection)
        }
        fun separateTD(list: ArrayList<Student>)/*: TD*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ТД_заочн_плат" || list[i].specialtySecond == "ТД_заочн_плат"
                        || list[i].specialtyThird == "ТД_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТД_очн_плат" || list[i].specialtySecond == "ТД_очн_плат"
                        || list[i].specialtyThird == "ТД_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnPlat, ochnPlat)
            listFEU.addAll(collection)
        }
        fun separateEB(list: ArrayList<Student>)/*: EB*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭБ_заоч_плат" || list[i].specialtySecond == "ЭБ_заоч_плат"
                        || list[i].specialtyThird == "ЭБ_заоч_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭБ_очн_плат" || list[i].specialtySecond == "ЭБ_очн_плат"
                        || list[i].specialtyThird == "ЭБ_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnPlat, ochnPlat)
            listFEU.addAll(collection)
        }
        fun separateEK(list: ArrayList<Student>)/*: EK*/ {
            val buaZaochnPlat = ArrayList<Student>()
            val buaOchnPlat = ArrayList<Student>()
            val logOchnPlat = ArrayList<Student>()
            val ocOchnPlat = ArrayList<Student>()
            val fZaochnPlat = ArrayList<Student>()
            val fOchnPlat = ArrayList<Student>()
            val epoOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭК(БУА)_заоч_плат" || list[i].specialtySecond == "ЭК(БУА)_заоч_плат"
                        || list[i].specialtyThird == "ЭК(БУА)_заоч_плат")
                    buaZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(БУА)_очн_плат" || list[i].specialtySecond == "ЭК(БУА)_очн_плат"
                        || list[i].specialtyThird == "ЭК(БУА)_очн_плат")
                    buaOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(ЛОГ)_очн_плат" || list[i].specialtySecond == "ЭК(ЛОГ)_очн_плат"
                        || list[i].specialtyThird == "ЭК(ЛОГ)_очн_плат")
                    logOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(ОЦ)_очн_плат" || list[i].specialtySecond == "ЭК(ОЦ)_очн_плат"
                        || list[i].specialtyThird == "ЭК(ОЦ)_очн_плат")
                    ocOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(Ф)_заоч_плат" || list[i].specialtySecond == "ЭК(Ф)_заоч_плат"
                        || list[i].specialtyThird == "ЭК(Ф)_заоч_плат")
                    fZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(Ф)_очн_плат" || list[i].specialtySecond == "ЭК(Ф)_очн_плат"
                        || list[i].specialtyThird == "ЭК(Ф)_очн_плат")
                    fOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭК(ЭПО)_очн_плат" || list[i].specialtySecond == "ЭК(ЭПО)_очн_плат"
                        || list[i].specialtyThird == "ЭК(ЭПО)_очн_плат")
                    epoOchnPlat.add(list[i])
            }

            val collection = arrayListOf(buaZaochnPlat, buaOchnPlat, logOchnPlat, ocOchnPlat,
                    fZaochnPlat, fOchnPlat, epoOchnPlat)
            listFEU.addAll(collection)
        }

        scoreTypes?.physicsStudents?.let { checkForBI(it) }
        scoreTypes?.computerScienceStudents?.let { checkForBI(it) }
        scoreTypes?.socialScienceStudents?.let { checkForBI(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForBI(it) }

        scoreTypes?.physicsStudents?.let { checkForPI(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPI(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPI(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPI(it) }

        scoreTypes?.physicsStudents?.let { checkForSC(it) }
        scoreTypes?.computerScienceStudents?.let { checkForSC(it) }
        scoreTypes?.socialScienceStudents?.let { checkForSC(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForSC(it) }

        scoreTypes?.physicsStudents?.let { checkForTD(it) }
        scoreTypes?.computerScienceStudents?.let { checkForTD(it) }
        scoreTypes?.socialScienceStudents?.let { checkForTD(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForTD(it) }

        scoreTypes?.physicsStudents?.let { checkForEB(it) }
        scoreTypes?.computerScienceStudents?.let { checkForEB(it) }
        scoreTypes?.socialScienceStudents?.let { checkForEB(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForEB(it) }

        scoreTypes?.physicsStudents?.let { checkForEK(it) }
        scoreTypes?.computerScienceStudents?.let { checkForEK(it) }
        scoreTypes?.socialScienceStudents?.let { checkForEK(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForEK(it) }

        /*scoreTypes?.physicsStudents?.let { check(it) }
        scoreTypes?.computerScienceStudents?.let { check(it) }
        scoreTypes?.socialScienceStudents?.let { check(it) }
        scoreTypes?.partAndAllDataStudents?.let { check(it) }*/

        /*val separatedBI = */separateBI(bi)
        /*val separatedPI = */separatePI(pi)
        /*val separatedSC = */separateSC(sc)
        /*val separatedTD = */separateTD(td)
        /*val separatedEB = */separateEB(eb)
        /*val separatedEK = */separateEK(ek)

        showLog(bi.size.toString())
        showLog(pi.size.toString())
        showLog(sc.size.toString())
        showLog(td.size.toString())
        showLog(eb.size.toString())
        showLog(ek.size.toString())

        /*val feu = Feu(separatedBI,  separatedPI, separatedSC,
                separatedTD, separatedEB, separatedEK)*/
        myApplication.saveFeu(listFEU)
    }
    override fun checkForFIT() {
        val scoreTypes = myApplication.returnScoreTypes()
        val iasb = ArrayList<Student>()
        val ib = ArrayList<Student>()
        val ibas = ArrayList<Student>()
        val ivt = ArrayList<Student>()
        val inn = ArrayList<Student>()
        val ist = ArrayList<Student>()
        val moa = ArrayList<Student>()
        val pri = ArrayList<Student>()
        val pro = ArrayList<Student>()

        val listFIT = ArrayList<ArrayList<Student>>()

        fun checkForIASB(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИАСБ_очн_бюдж" || list[i].specialtyFirst == "ИАСБ_очн_льгот"
                                || list[i].specialtyFirst == "ИАСБ_очн_плат") || (list[i].specialtySecond == "ИАСБ_очн_бюдж"
                                || list[i].specialtySecond == "ИАСБ_очн_льгот" || list[i].specialtySecond == "ИАСБ_очн_плат")
                        || (list[i].specialtyThird == "ИАСБ_очн_бюдж" || list[i].specialtyThird == "ИАСБ_очн_льгот"
                                || list[i].specialtyThird == "ИАСБ_очн_плат")) {
                    iasb.add(list[i])
                }
            }
        }
        fun checkForIB(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИБ_веч_платн" || list[i].specialtyFirst == "ИБ_очн_бюдж"
                                || list[i].specialtyFirst == "ИБ_очн_льгот" || list[i].specialtyFirst == "ИБ_очн_плат")
                        || (list[i].specialtySecond == "ИБ_веч_платн" || list[i].specialtySecond == "ИБ_очн_бюдж"
                                || list[i].specialtySecond == "ИБ_очн_льгот" || list[i].specialtySecond == "ИБ_очн_плат")
                        || (list[i].specialtyThird == "ИБ_веч_платн" || list[i].specialtyThird == "ИБ_очн_бюдж"
                                || list[i].specialtyThird == "ИБ_очн_льгот" || list[i].specialtyThird == "ИБ_очн_плат")) {
                    ib.add(list[i])
                }
            }
        }
        fun checkForIBAS(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИБАС_очн_бюдж" || list[i].specialtyFirst == "ИБАС_очн_льгот"
                                || list[i].specialtyFirst == "ИБАС_очн_плат") || (list[i].specialtySecond == "ИБАС_очн_бюдж"
                                || list[i].specialtySecond == "ИБАС_очн_льгот" || list[i].specialtySecond == "ИБАС_очн_плат")
                        || (list[i].specialtyThird == "ИБАС_очн_бюдж" || list[i].specialtyThird == "ИБАС_очн_льгот"
                                || list[i].specialtyThird == "ИБАС_очн_плат")) {
                    ibas.add(list[i])
                }
            }
        }
        fun checkForIVT(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИиВТ(ПО)_заочн_плат" || list[i].specialtyFirst == "ИиВТ(ПО)_очн_бюдж"
                                || list[i].specialtyFirst == "ИиВТ(ПО)_очн_льгот" || list[i].specialtyFirst == "ИиВТ(ПО)_очн_плат"
                                || list[i].specialtyFirst == "ИиВТ(ПО)_очн_целевое" || list[i].specialtyFirst == "ИиВТ(САПР)_очн_бюдж"
                                || list[i].specialtyFirst == "ИиВТ(САПР)_очн_льгот" || list[i].specialtyFirst == "ИиВТ(САПР)_очн_плат")
                        || (list[i].specialtySecond == "ИиВТ(ПО)_заочн_плат" || list[i].specialtySecond == "ИиВТ(ПО)_очн_бюдж"
                                || list[i].specialtySecond == "ИиВТ(ПО)_очн_льгот" || list[i].specialtySecond == "ИиВТ(ПО)_очн_плат"
                                || list[i].specialtySecond == "ИиВТ(ПО)_очн_целевое" || list[i].specialtySecond == "ИиВТ(САПР)_очн_бюдж"
                                || list[i].specialtySecond == "ИиВТ(САПР)_очн_льгот" || list[i].specialtySecond == "ИиВТ(САПР)_очн_плат")
                        || (list[i].specialtyThird == "ИиВТ(ПО)_заочн_плат" || list[i].specialtyThird == "ИиВТ(ПО)_очн_бюдж"
                                || list[i].specialtyThird == "ИиВТ(ПО)_очн_льгот" || list[i].specialtyThird == "ИиВТ(ПО)_очн_плат"
                                || list[i].specialtyThird == "ИиВТ(ПО)_очн_целевое" || list[i].specialtyThird == "ИиВТ(САПР)_очн_бюдж"
                                || list[i].specialtyThird == "ИиВТ(САПР)_очн_льгот" || list[i].specialtyThird == "ИиВТ(САПР)_очн_плат")) {
                    ivt.add(list[i])
                }
            }
        }
        fun checkForINN(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИНН_заочн_плат" || list[i].specialtyFirst == "ИНН_очн_бюдж"
                                || list[i].specialtyFirst == "ИНН_очн_льгот" || list[i].specialtyFirst == "ИНН_очн_плат")
                        || (list[i].specialtySecond == "ИНН_заочн_плат" || list[i].specialtySecond == "ИНН_очн_бюдж"
                                || list[i].specialtySecond == "ИНН_очн_льгот" || list[i].specialtySecond == "ИНН_очн_плат")
                        || (list[i].specialtyThird == "ИНН_заочн_плат" || list[i].specialtyThird == "ИНН_очн_бюдж"
                                || list[i].specialtyThird == "ИНН_очн_льгот" || list[i].specialtyThird == "ИНН_очн_плат")) {
                    inn.add(list[i])
                }
            }
        }
        fun checkForIST(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_бюдж" || list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_льгот"
                                || list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_плат" || list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_бюдж"
                                || list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_льгот" || list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_плат"
                                || list[i].specialtyFirst == "ИСТ_заочн_плат") || (list[i].specialtySecond == "ИСТ(ИСиТД)_очн_бюдж"
                                || list[i].specialtySecond == "ИСТ(ИСиТД)_очн_льгот" || list[i].specialtySecond == "ИСТ(ИСиТД)_очн_плат"
                                || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_бюдж" || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_льгот"
                                || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_плат" || list[i].specialtySecond == "ИСТ_заочн_плат")
                        || (list[i].specialtyThird == "ИСТ(ИСиТД)_очн_бюдж" || list[i].specialtyThird == "ИСТ(ИСиТД)_очн_льгот"
                                || list[i].specialtyThird == "ИСТ(ИСиТД)_очн_плат" || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_бюдж"
                                || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_льгот" || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_плат"
                                || list[i].specialtyThird == "ИСТ_заочн_плат")) {
                    ist.add(list[i])
                }
            }
        }
        fun checkForMOA(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "МОА_очн_бюдж" || list[i].specialtyFirst == "МОА_очн_льгот"
                                || list[i].specialtyFirst == "МОА_очн_плат" || list[i].specialtyFirst == "МОА_очн_целевое")
                        || (list[i].specialtySecond == "МОА_очн_бюдж" || list[i].specialtySecond == "МОА_очн_льгот"
                                || list[i].specialtySecond == "МОА_очн_плат" || list[i].specialtySecond == "МОА_очн_целевое")
                        || (list[i].specialtyThird == "МОА_очн_бюдж" || list[i].specialtyThird == "МОА_очн_льгот"
                                || list[i].specialtyThird == "МОА_очн_плат" || list[i].specialtyThird == "МОА_очн_целевое")) {
                    moa.add(list[i])
                }
            }
        }
        fun checkForPRI(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПРИ_очн_бюдж" || list[i].specialtyFirst == "ПРИ_очн_льгот"
                                || list[i].specialtyFirst == "ПРИ_очн_плат" || list[i].specialtyFirst == "ПРИ_очн_целевое")
                        || (list[i].specialtySecond == "ПРИ_очн_бюдж" || list[i].specialtySecond == "ПРИ_очн_льгот"
                                || list[i].specialtySecond == "ПРИ_очн_плат" || list[i].specialtySecond == "ПРИ_очн_целевое")
                        || (list[i].specialtyThird == "ПРИ_очн_бюдж" || list[i].specialtyThird == "ПРИ_очн_льгот"
                                || list[i].specialtyThird == "ПРИ_очн_плат" || list[i].specialtyThird == "ПРИ_очн_целевое")) {
                    pri.add(list[i])
                }
            }
        }
        fun checkForPRO(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПРО(ГД)_очн_бюдж" || list[i].specialtyFirst == "ПРО(ГД)_очн_льгот"
                                || list[i].specialtyFirst == "ПРО(ГД)_очн_плат" || list[i].specialtyFirst == "ПРО(ИВТ)_заочн_плат"
                                || list[i].specialtyFirst == "ПРО(ЭК)_заочн_плат") || (list[i].specialtySecond == "ПРО(ГД)_очн_бюдж"
                                || list[i].specialtySecond == "ПРО(ГД)_очн_льгот" || list[i].specialtySecond == "ПРО(ГД)_очн_плат"
                                || list[i].specialtySecond == "ПРО(ИВТ)_заочн_плат" || list[i].specialtySecond == "ПРО(ЭК)_заочн_плат")
                        || (list[i].specialtyThird == "ПРО(ГД)_очн_бюдж" || list[i].specialtyThird == "ПРО(ГД)_очн_льгот"
                                || list[i].specialtyThird == "ПРО(ГД)_очн_плат" || list[i].specialtyThird == "ПРО(ИВТ)_заочн_плат"
                                || list[i].specialtyThird == "ПРО(ЭК)_заочн_плат")) {
                    pro.add(list[i])
                }
            }
        }

        fun separateIASB(list: ArrayList<Student>)/*: IASB*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИАСБ_очн_бюдж" || list[i].specialtySecond == "ИАСБ_очн_бюдж"
                        || list[i].specialtyThird == "ИАСБ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИАСБ_очн_льгот" || list[i].specialtySecond == "ИАСБ_очн_льгот"
                        || list[i].specialtyThird == "ИАСБ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИАСБ_очн_плат" || list[i].specialtySecond == "ИАСБ_очн_плат"
                        || list[i].specialtyThird == "ИАСБ_очн_плат")
                    ochnPlat.add(list[i])
            }

            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat)
            listFIT.addAll(collection)
        }
        fun separateIB(list: ArrayList<Student>)/*: IB*/ {
            val vechPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИБ_веч_платн" || list[i].specialtySecond == "ИБ_веч_платн"
                        || list[i].specialtyThird == "ИБ_веч_платн")
                    vechPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИБ_очн_бюдж" || list[i].specialtySecond == "ИБ_очн_бюдж"
                        || list[i].specialtyThird == "ИБ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИБ_очн_льгот" || list[i].specialtySecond == "ИБ_очн_льгот"
                        || list[i].specialtyThird == "ИБ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИБ_очн_плат" || list[i].specialtySecond == "ИБ_очн_плат"
                        || list[i].specialtyThird == "ИБ_очн_плат")
                    ochnPlat.add(list[i])
            }

            val collection = arrayListOf(vechPlat, ochnBudg, ochnLgot, ochnPlat)
            listFIT.addAll(collection)
        }
        fun separateIBAS(list: ArrayList<Student>)/*: IBAS*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИБАС_очн_бюдж" || list[i].specialtySecond == "ИБАС_очн_бюдж"
                        || list[i].specialtyThird == "ИБАС_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИБАС_очн_льгот" || list[i].specialtySecond == "ИБАС_очн_льгот"
                        || list[i].specialtyThird == "ИБАС_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИБАС_очн_плат" || list[i].specialtySecond == "ИБАС_очн_плат"
                        || list[i].specialtyThird == "ИБАС_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat)
            listFIT.addAll(collection)
        }
        fun separateIVT(list: ArrayList<Student>)/*: IVT*/ {
            val poZaochnPlat = ArrayList<Student>()
            val poOchnBudg = ArrayList<Student>()
            val poOchnLgot = ArrayList<Student>()
            val poOchnPlat = ArrayList<Student>()
            val poOchnCelevoe = ArrayList<Student>()
            val saprOchnBudg = ArrayList<Student>()
            val saprOchnLgot = ArrayList<Student>()
            val saprOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИиВТ(ПО)_заочн_плат" || list[i].specialtySecond == "ИиВТ(ПО)_заочн_плат"
                        || list[i].specialtyThird == "ИиВТ(ПО)_заочн_плат")
                    poZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(ПО)_очн_бюдж" || list[i].specialtySecond == "ИиВТ(ПО)_очн_бюдж"
                        || list[i].specialtyThird == "ИиВТ(ПО)_очн_бюдж")
                    poOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(ПО)_очн_льгот" || list[i].specialtySecond == "ИиВТ(ПО)_очн_льгот"
                        || list[i].specialtyThird == "ИиВТ(ПО)_очн_льгот")
                    poOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(ПО)_очн_плат" || list[i].specialtySecond == "ИиВТ(ПО)_очн_плат"
                        || list[i].specialtyThird == "ИиВТ(ПО)_очн_плат")
                    poOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(ПО)_очн_целевое" || list[i].specialtySecond == "ИиВТ(ПО)_очн_целевое"
                        || list[i].specialtyThird == "ИиВТ(ПО)_очн_целевое")
                    poOchnCelevoe.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(САПР)_очн_бюдж" || list[i].specialtySecond == "ИиВТ(САПР)_очн_бюдж"
                        || list[i].specialtyThird == "ИиВТ(САПР)_очн_бюдж")
                    saprOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(САПР)_очн_льгот" || list[i].specialtySecond == "ИиВТ(САПР)_очн_льгот"
                        || list[i].specialtyThird == "ИиВТ(САПР)_очн_льгот")
                    saprOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИиВТ(САПР)_очн_плат" || list[i].specialtySecond == "ИиВТ(САПР)_очн_плат"
                        || list[i].specialtyThird == "ИиВТ(САПР)_очн_плат")
                    saprOchnPlat.add(list[i])
            }

            val collection = arrayListOf(poZaochnPlat, poOchnBudg, poOchnLgot, poOchnPlat,
                    poOchnCelevoe, saprOchnBudg, saprOchnLgot, saprOchnPlat)
            listFIT.addAll(collection)
        }
        fun separateINN(list: ArrayList<Student>)/*: INN*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИНН_заочн_плат" || list[i].specialtySecond == "ИНН_заочн_плат"
                        || list[i].specialtyThird == "ИНН_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИНН_очн_бюдж" || list[i].specialtySecond == "ИНН_очн_бюдж"
                        || list[i].specialtyThird == "ИНН_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИНН_очн_льгот" || list[i].specialtySecond == "ИНН_очн_льгот"
                        || list[i].specialtyThird == "ИНН_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИНН_очн_плат" || list[i].specialtySecond == "ИНН_очн_плат"
                        || list[i].specialtyThird == "ИНН_очн_плат")
                    ochnPlat.add(list[i])
            }

            val collection = arrayListOf(zaochnPlat, ochnBudg, ochnLgot, ochnPlat)
            listFIT.addAll(collection)
        }
        fun separateIST(list: ArrayList<Student>)/*: IST*/ {
            val isitdOchnBudg = ArrayList<Student>()
            val isitdOchnLgot = ArrayList<Student>()
            val isitdOchnPlat = ArrayList<Student>()
            val itipkOchnBudg = ArrayList<Student>()
            val itipkOchnLgot = ArrayList<Student>()
            val itipkOchnPlat = ArrayList<Student>()
            val zaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_бюдж" || list[i].specialtySecond == "ИСТ(ИСиТД)_очн_бюдж"
                        || list[i].specialtyThird == "ИСТ(ИСиТД)_очн_бюдж")
                    isitdOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_льгот" || list[i].specialtySecond == "ИСТ(ИСиТД)_очн_льгот"
                        || list[i].specialtyThird == "ИСТ(ИСиТД)_очн_льгот")
                    isitdOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ(ИСиТД)_очн_плат" || list[i].specialtySecond == "ИСТ(ИСиТД)_очн_плат"
                        || list[i].specialtyThird == "ИСТ(ИСиТД)_очн_плат")
                    isitdOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_бюдж" || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_бюдж"
                        || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_бюдж")
                    itipkOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_льгот" || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_льгот"
                        || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_льгот")
                    itipkOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ(ИТиПК)_очн_плат" || list[i].specialtySecond == "ИСТ(ИТиПК)_очн_плат"
                        || list[i].specialtyThird == "ИСТ(ИТиПК)_очн_плат")
                    itipkOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ИСТ_заочн_плат" || list[i].specialtySecond == "ИСТ_заочн_плат"
                        || list[i].specialtyThird == "ИСТ_заочн_плат")
                    zaochnPlat.add(list[i])
            }

            val collection = arrayListOf(isitdOchnBudg, isitdOchnLgot, isitdOchnPlat,
                    itipkOchnBudg, itipkOchnLgot, itipkOchnPlat, zaochnPlat)
            listFIT.addAll(collection)
        }
        fun separateMOA(list: ArrayList<Student>)/*: MOA*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "МОА_очн_бюдж" || list[i].specialtySecond == "МОА_очн_бюдж"
                        || list[i].specialtyThird == "МОА_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МОА_очн_льгот" || list[i].specialtySecond == "МОА_очн_льгот"
                        || list[i].specialtyThird == "МОА_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МОА_очн_плат" || list[i].specialtySecond == "МОА_очн_плат"
                        || list[i].specialtyThird == "МОА_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "МОА_очн_целевое" || list[i].specialtySecond == "МОА_очн_целевое"
                        || list[i].specialtyThird == "МОА_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listFIT.addAll(collection)
        }
        fun separatePRI(list: ArrayList<Student>)/*: PRI*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПРИ_очн_бюдж" || list[i].specialtySecond == "ПРИ_очн_бюдж"
                        || list[i].specialtyThird == "ПРИ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПРИ_очн_льгот" || list[i].specialtySecond == "ПРИ_очн_льгот"
                        || list[i].specialtyThird == "ПРИ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПРИ_очн_плат" || list[i].specialtySecond == "ПРИ_очн_плат"
                        || list[i].specialtyThird == "ПРИ_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПРИ_очн_целевое" || list[i].specialtySecond == "ПРИ_очн_целевое"
                        || list[i].specialtyThird == "ПРИ_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listFIT.addAll(collection)
        }
        fun separatePRO(list: ArrayList<Student>)/*: PRO*/ {
            val gdOchnBudg = ArrayList<Student>()
            val gdOchnLgot = ArrayList<Student>()
            val gdOchnPlat = ArrayList<Student>()
            val ivtZaochnPlat = ArrayList<Student>()
            val ekZaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПРО(ГД)_очн_бюдж" || list[i].specialtySecond == "ПРО(ГД)_очн_бюдж"
                        || list[i].specialtyThird == "ПРО(ГД)_очн_бюдж")
                    gdOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПРО(ГД)_очн_льгот" || list[i].specialtySecond == "ПРО(ГД)_очн_льгот"
                        || list[i].specialtyThird == "ПРО(ГД)_очн_льгот")
                    gdOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПРО(ГД)_очн_плат" || list[i].specialtySecond == "ПРО(ГД)_очн_плат"
                        || list[i].specialtyThird == "ПРО(ГД)_очн_плат")
                    gdOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПРО(ИВТ)_заочн_плат" || list[i].specialtySecond == "ПРО(ИВТ)_заочн_плат"
                        || list[i].specialtyThird == "ПРО(ИВТ)_заочн_плат")
                    ivtZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПРО(ЭК)_заочн_плат" || list[i].specialtySecond == "ПРО(ЭК)_заочн_плат"
                        || list[i].specialtyThird == "ПРО(ЭК)_заочн_плат")
                    ekZaochnPlat.add(list[i])
            }
            val collection = arrayListOf(gdOchnBudg, gdOchnLgot, gdOchnPlat,
                    ivtZaochnPlat, ekZaochnPlat)
            listFIT.addAll(collection)
        }

        scoreTypes?.physicsStudents?.let { checkForIASB(it) }
        scoreTypes?.computerScienceStudents?.let { checkForIASB(it) }
        scoreTypes?.socialScienceStudents?.let { checkForIASB(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForIASB(it) }

        scoreTypes?.physicsStudents?.let { checkForIB(it) }
        scoreTypes?.computerScienceStudents?.let { checkForIB(it) }
        scoreTypes?.socialScienceStudents?.let { checkForIB(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForIB(it) }

        scoreTypes?.physicsStudents?.let { checkForIBAS(it) }
        scoreTypes?.computerScienceStudents?.let { checkForIBAS(it) }
        scoreTypes?.socialScienceStudents?.let { checkForIBAS(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForIBAS(it) }

        scoreTypes?.physicsStudents?.let { checkForIVT(it) }
        scoreTypes?.computerScienceStudents?.let { checkForIVT(it) }
        scoreTypes?.socialScienceStudents?.let { checkForIVT(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForIVT(it) }

        scoreTypes?.physicsStudents?.let { checkForINN(it) }
        scoreTypes?.computerScienceStudents?.let { checkForINN(it) }
        scoreTypes?.socialScienceStudents?.let { checkForINN(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForINN(it) }

        scoreTypes?.physicsStudents?.let { checkForIST(it) }
        scoreTypes?.computerScienceStudents?.let { checkForIST(it) }
        scoreTypes?.socialScienceStudents?.let { checkForIST(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForIST(it) }

        scoreTypes?.physicsStudents?.let { checkForMOA(it) }
        scoreTypes?.computerScienceStudents?.let { checkForMOA(it) }
        scoreTypes?.socialScienceStudents?.let { checkForMOA(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForMOA(it) }

        scoreTypes?.physicsStudents?.let { checkForPRI(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPRI(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPRI(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPRI(it) }

        scoreTypes?.physicsStudents?.let { checkForPRO(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPRO(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPRO(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPRO(it) }

        /*val separatedIASB = */separateIASB(iasb)
        /*val separatedIB = */separateIB(ib)
        /*val separatedIBAS = */separateIBAS(ibas)
        /*val separatedIVT = */separateIVT(ivt)
        /*val separatedINN = */separateINN(inn)
        /*val separatedIST = */separateIST(ist)
        /*val separatedMOA = */separateMOA(moa)
        /*val separatedPRI = */separatePRI(pri)
        /*val separatedPRO = */separatePRO(pro)


        /*val fit = Fit(separatedIASB, separatedIB, separatedIBAS, separatedIVT, separatedINN,
                separatedIST, separatedMOA, separatedPRI, separatedPRO)*/
        myApplication.saveFIT(listFIT)
    }
    override fun checkForMTF() {
        val scoreTypes = myApplication.returnScoreTypes()
        val mash = ArrayList<Student>()
        val sim = ArrayList<Student>()
        val tb = ArrayList<Student>()
        val uk = ArrayList<Student>()

        val listMTF = ArrayList<ArrayList<Student>>()

        fun checkForMASH(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "МАШ(Л)_заочн_бюдж" || list[i].specialtyFirst == "МАШ(Л)_заочн_льгот"
                                || list[i].specialtyFirst == "МАШ(Л)_заочн_плат" || list[i].specialtyFirst == "МАШ(Л)_очн_бюдж"
                                || list[i].specialtyFirst == "МАШ(Л)_очн_льгот" || list[i].specialtyFirst == "МАШ(Л)_очн_плат"
                                || list[i].specialtyFirst == "МАШ(С)_заочн_бюдж" || list[i].specialtyFirst == "МАШ(С)_заочн_льгот"
                                || list[i].specialtyFirst == "МАШ(С)_заочн_плат" || list[i].specialtyFirst == "МАШ(С)_очн_бюдж"
                                || list[i].specialtyFirst == "МАШ(С)_очн_льгот" || list[i].specialtyFirst == "МАШ(С)_очн_плат"
                                || list[i].specialtyFirst == "МАШ(С)_очн_целевое") || (list[i].specialtySecond == "МАШ(Л)_заочн_бюдж"
                                || list[i].specialtySecond == "МАШ(Л)_заочн_льгот" || list[i].specialtySecond == "МАШ(Л)_заочн_плат"
                                || list[i].specialtySecond == "МАШ(Л)_очн_бюдж" || list[i].specialtySecond == "МАШ(Л)_очн_льгот"
                                || list[i].specialtySecond == "МАШ(Л)_очн_плат" || list[i].specialtySecond == "МАШ(С)_заочн_бюдж"
                                || list[i].specialtySecond == "МАШ(С)_заочн_льгот" || list[i].specialtySecond == "МАШ(С)_заочн_плат"
                                || list[i].specialtySecond == "МАШ(С)_очн_бюдж" || list[i].specialtySecond == "МАШ(С)_очн_льгот"
                                || list[i].specialtySecond == "МАШ(С)_очн_плат" || list[i].specialtySecond == "МАШ(С)_очн_целевое")
                        || (list[i].specialtyThird == "МАШ(Л)_заочн_бюдж" || list[i].specialtyThird == "МАШ(Л)_заочн_льгот"
                                || list[i].specialtyThird == "МАШ(Л)_заочн_плат" || list[i].specialtyThird == "МАШ(Л)_очн_бюдж"
                                || list[i].specialtyThird == "МАШ(Л)_очн_льгот" || list[i].specialtyThird == "МАШ(Л)_очн_плат"
                                || list[i].specialtyThird == "МАШ(С)_заочн_бюдж" || list[i].specialtyThird == "МАШ(С)_заочн_льгот"
                                || list[i].specialtyThird == "МАШ(С)_заочн_плат" || list[i].specialtyThird == "МАШ(С)_очн_бюдж"
                                || list[i].specialtyThird == "МАШ(С)_очн_льгот" || list[i].specialtyThird == "МАШ(С)_очн_плат"
                                || list[i].specialtyThird == "МАШ(С)_очн_целевое")) {
                    mash.add(list[i])
                }
            }
        }
        fun checkForSIM(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "СиМ_заочн_плат" || list[i].specialtyFirst == "СиМ_очн_бюдж"
                                || list[i].specialtyFirst == "СиМ_очн_льгот" || list[i].specialtyFirst == "СиМ_очн_плат")
                        || (list[i].specialtySecond == "СиМ_заочн_плат" || list[i].specialtySecond == "СиМ_очн_бюдж"
                                || list[i].specialtySecond == "СиМ_очн_льгот" || list[i].specialtySecond == "СиМ_очн_плат")
                        || (list[i].specialtyThird == "СиМ_заочн_плат" || list[i].specialtyThird == "СиМ_очн_бюдж"
                                || list[i].specialtyThird == "СиМ_очн_льгот" || list[i].specialtyThird == "СиМ_очн_плат")) {
                    sim.add(list[i])
                }
            }
        }
        fun checkForTB(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ТБ(БТПиП)_заочн_плат" || list[i].specialtyFirst == "ТБ(БТПиП)_очн_бюдж"
                                || list[i].specialtyFirst == "ТБ(БТПиП)_очн_льгот" || list[i].specialtyFirst == "ТБ(БТПиП)_очн_плат")
                        || (list[i].specialtySecond == "ТБ(БТПиП)_заочн_плат" || list[i].specialtySecond == "ТБ(БТПиП)_очн_бюдж"
                                || list[i].specialtySecond == "ТБ(БТПиП)_очн_льгот" || list[i].specialtySecond == "ТБ(БТПиП)_очн_плат")
                        || (list[i].specialtyThird == "ТБ(БТПиП)_заочн_плат" || list[i].specialtyThird == "ТБ(БТПиП)_очн_бюдж"
                                || list[i].specialtyThird == "ТБ(БТПиП)_очн_льгот" || list[i].specialtyThird == "ТБ(БТПиП)_очн_плат")) {
                    tb.add(list[i])
                }
            }
        }
        fun checkForUK(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "УК_заочн_плат" || list[i].specialtySecond == "УК_заочн_плат"
                        || list[i].specialtyThird == "УК_заочн_плат") {
                    uk.add(list[i])
                }
            }
        }

        fun separateMASH(list: ArrayList<Student>)/*: com.madrat.abiturhelper.model.faculties.mtf.MASH*/ {
            val lZaochnBudg = ArrayList<Student>()
            val lZaochnLgot = ArrayList<Student>()
            val lZaochnPlat = ArrayList<Student>()
            val lOchnBudg = ArrayList<Student>()
            val lOchnLgot = ArrayList<Student>()
            val lOchnPlat = ArrayList<Student>()
            val sZaochnBudg = ArrayList<Student>()
            val sZaochnLgot = ArrayList<Student>()
            val sZaochnPlat = ArrayList<Student>()
            val sOchnBudg = ArrayList<Student>()
            val sOchnLgot = ArrayList<Student>()
            val sOchnPlat = ArrayList<Student>()
            val sOchnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "МАШ(Л)_заочн_бюдж" || list[i].specialtySecond == "МАШ(Л)_заочн_бюдж"
                        || list[i].specialtyThird == "МАШ(Л)_заочн_бюдж")
                    lZaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(Л)_заочн_льгот" || list[i].specialtySecond == "МАШ(Л)_заочн_льгот"
                        || list[i].specialtyThird == "МАШ(Л)_заочн_льгот")
                    lZaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(Л)_заочн_плат" || list[i].specialtySecond == "МАШ(Л)_заочн_плат"
                        || list[i].specialtyThird == "МАШ(Л)_заочн_плат")
                    lZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(Л)_очн_бюдж" || list[i].specialtySecond == "МАШ(Л)_очн_бюдж"
                        || list[i].specialtyThird == "МАШ(Л)_очн_бюдж")
                    lOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(Л)_очн_льгот" || list[i].specialtySecond == "МАШ(Л)_очн_льгот"
                        || list[i].specialtyThird == "МАШ(Л)_очн_льгот")
                    lOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(Л)_очн_плат" || list[i].specialtySecond == "МАШ(Л)_очн_плат"
                        || list[i].specialtyThird == "МАШ(Л)_очн_плат")
                    lOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_заочн_бюдж" || list[i].specialtySecond == "МАШ(С)_заочн_бюдж"
                        || list[i].specialtyThird == "МАШ(С)_заочн_бюдж")
                    sZaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_заочн_льгот" || list[i].specialtySecond == "МАШ(С)_заочн_льгот"
                        || list[i].specialtyThird == "МАШ(С)_заочн_льгот")
                    sZaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_заочн_плат" || list[i].specialtySecond == "МАШ(С)_заочн_плат"
                        || list[i].specialtyThird == "МАШ(С)_заочн_плат")
                    sZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_очн_бюдж" || list[i].specialtySecond == "МАШ(С)_очн_бюдж"
                        || list[i].specialtyThird == "МАШ(С)_очн_бюдж")
                    sOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_очн_льгот" || list[i].specialtySecond == "МАШ(С)_очн_льгот"
                        || list[i].specialtyThird == "МАШ(С)_очн_льгот")
                    sOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_очн_плат" || list[i].specialtySecond == "МАШ(С)_очн_плат"
                        || list[i].specialtyThird == "МАШ(С)_очн_плат")
                    sOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "МАШ(С)_очн_целевое" || list[i].specialtySecond == "МАШ(С)_очн_целевое"
                        || list[i].specialtyThird == "МАШ(С)_очн_целевое")
                    sOchnCelevoe.add(list[i])
            }
            val collection = arrayListOf(lZaochnBudg, lZaochnLgot, lZaochnPlat,lOchnBudg, lOchnLgot,
                    lOchnPlat, sZaochnBudg, sZaochnLgot, sZaochnPlat, sOchnBudg, sOchnLgot, sOchnPlat, sOchnCelevoe)
            listMTF.addAll(collection)
        }
        fun separateSIM(list: ArrayList<Student>)/*: SIM*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "СиМ_заочн_плат" || list[i].specialtySecond == "СиМ_заочн_плат"
                        || list[i].specialtyThird == "СиМ_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "СиМ_очн_бюдж" || list[i].specialtySecond == "СиМ_очн_бюдж"
                        || list[i].specialtyThird == "СиМ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "СиМ_очн_льгот" || list[i].specialtySecond == "СиМ_очн_льгот"
                        || list[i].specialtyThird == "СиМ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "СиМ_очн_плат" || list[i].specialtySecond == "СиМ_очн_плат"
                        || list[i].specialtyThird == "СиМ_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnPlat, ochnBudg, ochnLgot, ochnPlat)
            listMTF.addAll(collection)
        }
        fun separateTB(list: ArrayList<Student>)/*: TB*/ {
            val btpipZaochnPlat = ArrayList<Student>()
            val btpipOchnBudg = ArrayList<Student>()
            val btpipOchnLgot = ArrayList<Student>()
            val btpipOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ТБ(БТПиП)_заочн_плат" || list[i].specialtySecond == "ТБ(БТПиП)_заочн_плат"
                        || list[i].specialtyThird == "ТБ(БТПиП)_заочн_плат")
                    btpipZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТБ(БТПиП)_очн_бюдж" || list[i].specialtySecond == "ТБ(БТПиП)_очн_бюдж"
                        || list[i].specialtyThird == "ТБ(БТПиП)_очн_бюдж")
                    btpipOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТБ(БТПиП)_очн_льгот" || list[i].specialtySecond == "ТБ(БТПиП)_очн_льгот"
                        || list[i].specialtyThird == "ТБ(БТПиП)_очн_льгот")
                    btpipOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТБ(БТПиП)_очн_плат" || list[i].specialtySecond == "ТБ(БТПиП)_очн_плат"
                        || list[i].specialtyThird == "ТБ(БТПиП)_очн_плат")
                    btpipOchnPlat.add(list[i])
            }
            val collection = arrayListOf(btpipZaochnPlat, btpipOchnBudg,
                    btpipOchnLgot, btpipOchnPlat)
            listMTF.addAll(collection)
        }
        fun separateUK(list: ArrayList<Student>)/*: UK*/ {
            val zaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "УК_заочн_плат" || list[i].specialtySecond == "УК_заочн_плат"
                        || list[i].specialtyThird == "УК_заочн_плат")
                    zaochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnPlat)
            listMTF.addAll(collection)
        }

        scoreTypes?.physicsStudents?.let { checkForMASH(it) }
        scoreTypes?.computerScienceStudents?.let { checkForMASH(it) }
        scoreTypes?.socialScienceStudents?.let { checkForMASH(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForMASH(it) }

        scoreTypes?.physicsStudents?.let { checkForSIM(it) }
        scoreTypes?.computerScienceStudents?.let { checkForSIM(it) }
        scoreTypes?.socialScienceStudents?.let { checkForSIM(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForSIM(it) }

        scoreTypes?.physicsStudents?.let { checkForTB(it) }
        scoreTypes?.computerScienceStudents?.let { checkForTB(it) }
        scoreTypes?.socialScienceStudents?.let { checkForTB(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForTB(it) }

        scoreTypes?.physicsStudents?.let { checkForUK(it) }
        scoreTypes?.computerScienceStudents?.let { checkForUK(it) }
        scoreTypes?.socialScienceStudents?.let { checkForUK(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForUK(it) }

        /*val separatedMASH = */separateMASH(mash)
        /*val separatedSIM = */separateSIM(sim)
        /*val separatedTB = */separateTB(tb)
        /*val separatedUK = */separateUK(uk)

        //val mtf = MTF(separatedMASH, separatedSIM, separatedTB, separatedUK)
        myApplication.saveMTF(listMTF)
    }
    override fun checkForUNIT() {
        val scoreTypes = myApplication.returnScoreTypes()
        val nttk = ArrayList<Student>()
        val ntts = ArrayList<Student>()
        val pm = ArrayList<Student>()
        val psjd = ArrayList<Student>()
        val ttp = ArrayList<Student>()
        val ettk = ArrayList<Student>()

        val listUNIT = ArrayList<ArrayList<Student>>()

        fun checkForNTTK(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "НТТК_заочн_бюдж" || list[i].specialtyFirst == "НТТК_заочн_льгот"
                                || list[i].specialtyFirst == "НТТК_заочн_плат") || (list[i].specialtySecond == "НТТК_заочн_бюдж"
                                || list[i].specialtySecond == "НТТК_заочн_льгот" || list[i].specialtySecond == "НТТК_заочн_плат")
                        || (list[i].specialtyThird == "НТТК_заочн_бюдж" || list[i].specialtyThird == "НТТК_заочн_льгот"
                                || list[i].specialtyThird == "НТТК_заочн_плат")) {
                    nttk.add(list[i])
                }
            }
        }
        fun checkForNTTS(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "НТТС_очн_бюдж" || list[i].specialtyFirst == "НТТС_очн_льгот"
                                || list[i].specialtyFirst == "НТТС_очн_плат") || (list[i].specialtySecond == "НТТС_очн_бюдж"
                                || list[i].specialtySecond == "НТТС_очн_льгот" || list[i].specialtySecond == "НТТС_очн_плат")
                        || (list[i].specialtyThird == "НТТС_очн_бюдж" || list[i].specialtyThird == "НТТС_очн_льгот"
                                || list[i].specialtyThird == "НТТС_очн_плат")) {
                    ntts.add(list[i])
                }
            }
        }
        fun checkForPM(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПМ(БМ)_очн_бюдж" || list[i].specialtyFirst == "ПМ(БМ)_очн_льгот"
                                || list[i].specialtyFirst == "ПМ(БМ)_очн_плат" || list[i].specialtyFirst == "ПМ(ДПМ)_очн_бюдж"
                                || list[i].specialtyFirst == "ПМ(ДПМ)_очн_льгот" || list[i].specialtyFirst == "ПМ(ДПМ)_очн_плат")
                        || (list[i].specialtySecond == "ПМ(БМ)_очн_бюдж" || list[i].specialtySecond == "ПМ(БМ)_очн_льгот"
                                || list[i].specialtySecond == "ПМ(БМ)_очн_плат" || list[i].specialtySecond == "ПМ(ДПМ)_очн_бюдж"
                                || list[i].specialtySecond == "ПМ(ДПМ)_очн_льгот" || list[i].specialtySecond == "ПМ(ДПМ)_очн_плат")
                        || (list[i].specialtyThird == "ПМ(БМ)_очн_бюдж" || list[i].specialtyThird == "ПМ(БМ)_очн_льгот"
                                || list[i].specialtyThird == "ПМ(БМ)_очн_плат" || list[i].specialtyThird == "ПМ(ДПМ)_очн_бюдж"
                                || list[i].specialtyThird == "ПМ(ДПМ)_очн_льгот" || list[i].specialtyThird == "ПМ(ДПМ)_очн_плат")) {
                    pm.add(list[i])
                }
            }
        }
        fun checkForPSJD(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ПСЖД(В)_очн_плат" || list[i].specialtyFirst == "ПСЖД(Л)_очн_плат"
                                || list[i].specialtyFirst == "ПСЖД_заочн_плат") || (list[i].specialtySecond == "ПСЖД(В)_очн_плат"
                                || list[i].specialtySecond == "ПСЖД(Л)_очн_плат" || list[i].specialtySecond == "ПСЖД_заочн_плат")
                        || (list[i].specialtyThird == "ПСЖД(В)_очн_плат" || list[i].specialtyThird == "ПСЖД(Л)_очн_плат"
                                || list[i].specialtyThird == "ПСЖД_заочн_плат")) {
                    psjd.add(list[i])
                }
            }
        }
        fun checkForTTP(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ТТП_заочн_бюдж" || list[i].specialtyFirst == "ТТП_заочн_льгот"
                                || list[i].specialtyFirst == "ТТП_заочн_плат" || list[i].specialtyFirst == "ТТП_очн_бюдж"
                                || list[i].specialtyFirst == "ТТП_очн_льгот" || list[i].specialtyFirst == "ТТП_очн_плат")
                        || (list[i].specialtySecond == "ТТП_заочн_бюдж" || list[i].specialtySecond == "ТТП_заочн_льгот"
                                || list[i].specialtySecond == "ТТП_заочн_плат" || list[i].specialtySecond == "ТТП_очн_бюдж"
                                || list[i].specialtySecond == "ТТП_очн_льгот" || list[i].specialtySecond == "ТТП_очн_плат")
                        || (list[i].specialtyThird == "ТТП_заочн_бюдж" || list[i].specialtyThird == "ТТП_заочн_льгот"
                                || list[i].specialtyThird == "ТТП_заочн_плат" || list[i].specialtyThird == "ТТП_очн_бюдж"
                                || list[i].specialtyThird == "ТТП_очн_льгот" || list[i].specialtyThird == "ТТП_очн_плат")) {
                    ttp.add(list[i])
                }
            }
        }
        fun checkForETTK(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_бюдж" || list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_льгот"
                                || list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_плат" || list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_целевое"
                                || list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_бюдж" || list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_льгот"
                                || list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_плат") || (list[i].specialtySecond == "ЭТТК(АиАХ)_очн_бюдж"
                                || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_льгот" || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_плат"
                                || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_целевое" || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_бюдж"
                                || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_льгот" || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_плат")
                        || (list[i].specialtyThird == "ЭТТК(АиАХ)_очн_бюдж" || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_льгот"
                                || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_плат" || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_целевое"
                                || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_бюдж" || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_льгот"
                                || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_плат")) {
                    ettk.add(list[i])
                }
            }
        }

        fun separateNTTK(list: ArrayList<Student>)/*: NTTK*/ {
            val zaochnBudg = ArrayList<Student>()
            val zaochnLgot = ArrayList<Student>()
            val zaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "НТТК_заочн_бюдж" || list[i].specialtySecond == "НТТК_заочн_бюдж"
                        || list[i].specialtyThird == "НТТК_заочн_бюдж")
                    zaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "НТТК_заочн_льгот" || list[i].specialtySecond == "НТТК_заочн_льгот"
                        || list[i].specialtyThird == "НТТК_заочн_льгот")
                    zaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "НТТК_заочн_плат" || list[i].specialtySecond == "НТТК_заочн_плат"
                        || list[i].specialtyThird == "НТТК_заочн_плат")
                    zaochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnBudg, zaochnLgot, zaochnPlat)
            listUNIT.addAll(collection)
        }
        fun separateNTTS(list: ArrayList<Student>)/*: NTTS*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "НТТС_очн_бюдж" || list[i].specialtySecond == "НТТС_очн_бюдж"
                        || list[i].specialtyThird == "НТТС_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "НТТС_очн_льгот" || list[i].specialtySecond == "НТТС_очн_льгот"
                        || list[i].specialtyThird == "НТТС_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "НТТС_очн_плат" || list[i].specialtySecond == "НТТС_очн_плат"
                        || list[i].specialtyThird == "НТТС_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat)
            listUNIT.addAll(collection)
        }
        fun separatePM(list: ArrayList<Student>)/*: PM*/ {
            val bmOchnBudg = ArrayList<Student>()
            val bmOchnLgot = ArrayList<Student>()
            val bmOchnPlat = ArrayList<Student>()
            val dpmOchnBudg = ArrayList<Student>()
            val dpmOchnLgot = ArrayList<Student>()
            val dpmOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПМ(БМ)_очн_бюдж" || list[i].specialtySecond == "ПМ(БМ)_очн_бюдж"
                        || list[i].specialtyThird == "ПМ(БМ)_очн_бюдж")
                    bmOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПМ(БМ)_очн_льгот" || list[i].specialtySecond == "ПМ(БМ)_очн_льгот"
                        || list[i].specialtyThird == "ПМ(БМ)_очн_льгот")
                    bmOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПМ(БМ)_очн_плат" || list[i].specialtySecond == "ПМ(БМ)_очн_плат"
                        || list[i].specialtyThird == "ПМ(БМ)_очн_плат")
                    bmOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПМ(ДПМ)_очн_бюдж" || list[i].specialtySecond == "ПМ(ДПМ)_очн_бюдж"
                        || list[i].specialtyThird == "ПМ(ДПМ)_очн_бюдж")
                    dpmOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ПМ(ДПМ)_очн_льгот" || list[i].specialtySecond == "ПМ(ДПМ)_очн_льгот"
                        || list[i].specialtyThird == "ПМ(ДПМ)_очн_льгот")
                    dpmOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ПМ(ДПМ)_очн_плат" || list[i].specialtySecond == "ПМ(ДПМ)_очн_плат"
                        || list[i].specialtyThird == "ПМ(ДПМ)_очн_плат")
                    dpmOchnPlat.add(list[i])
            }
            val collection = arrayListOf(bmOchnBudg, bmOchnLgot, bmOchnPlat,
                    dpmOchnBudg, dpmOchnLgot, dpmOchnPlat)
            listUNIT.addAll(collection)
        }
        fun separatePSJD(list: ArrayList<Student>)/*: PSJD*/ {
            val vOchnPlat = ArrayList<Student>()
            val lOchnPlat = ArrayList<Student>()
            val zaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ПСЖД(В)_очн_плат" || list[i].specialtySecond == "ПСЖД(В)_очн_плат"
                        || list[i].specialtyThird == "ПСЖД(В)_очн_плат")
                    vOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПСЖД(Л)_очн_плат" || list[i].specialtySecond == "ПСЖД(Л)_очн_плат"
                        || list[i].specialtyThird == "ПСЖД(Л)_очн_плат")
                    lOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ПСЖД_заочн_плат" || list[i].specialtySecond == "ПСЖД_заочн_плат"
                        || list[i].specialtyThird == "ПСЖД_заочн_плат")
                    zaochnPlat.add(list[i])
            }
            val collection = arrayListOf(vOchnPlat, lOchnPlat, zaochnPlat)
            listUNIT.addAll(collection)
        }
        fun separateTTP(list: ArrayList<Student>)/*: TTP*/ {
            val zaochnBudg = ArrayList<Student>()
            val zaochnLgot = ArrayList<Student>()
            val zaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ТТП_заочн_бюдж" || list[i].specialtySecond == "ТТП_заочн_бюдж"
                        || list[i].specialtyThird == "ТТП_заочн_бюдж")
                    zaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТТП_заочн_льгот" || list[i].specialtySecond == "ТТП_заочн_льгот"
                        || list[i].specialtyThird == "ТТП_заочн_льгот")
                    zaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТТП_заочн_плат" || list[i].specialtySecond == "ТТП_заочн_плат"
                        || list[i].specialtyThird == "ТТП_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТТП_очн_бюдж" || list[i].specialtySecond == "ТТП_очн_бюдж"
                        || list[i].specialtyThird == "ТТП_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТТП_очн_льгот" || list[i].specialtySecond == "ТТП_очн_льгот"
                        || list[i].specialtyThird == "ТТП_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТТП_очн_плат" || list[i].specialtySecond == "ТТП_очн_плат"
                        || list[i].specialtyThird == "ТТП_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(zaochnBudg, zaochnLgot, zaochnPlat,
                    ochnBudg, ochnLgot, ochnPlat)
            listUNIT.addAll(collection)
        }
        fun separateETTK(list: ArrayList<Student>)/*: ETTK*/ {
            val aiahOchnBudg = ArrayList<Student>()
            val aiahOchnLgot = ArrayList<Student>()
            val aiahOchnPlat = ArrayList<Student>()
            val aiahOchnCelevoe = ArrayList<Student>()
            val psjdOchnBudg = ArrayList<Student>()
            val psjdOchnLgot = ArrayList<Student>()
            val psjdOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_бюдж" || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_бюдж"
                        || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_бюдж")
                    aiahOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_льгот" || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_льгот"
                        || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_льгот")
                    aiahOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_плат" || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_плат"
                        || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_плат")
                    aiahOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(АиАХ)_очн_целевое" || list[i].specialtySecond == "ЭТТК(АиАХ)_очн_целевое"
                        || list[i].specialtyThird == "ЭТТК(АиАХ)_очн_целевое")
                    aiahOchnCelevoe.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_бюдж" || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_бюдж"
                        || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_бюдж")
                    psjdOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_льгот" || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_льгот"
                        || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_льгот")
                    psjdOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭТТК(ПСЖД)_очн_плат" || list[i].specialtySecond == "ЭТТК(ПСЖД)_очн_плат"
                        || list[i].specialtyThird == "ЭТТК(ПСЖД)_очн_плат")
                    psjdOchnPlat.add(list[i])
            }
            val collection = arrayListOf(aiahOchnBudg, aiahOchnLgot, aiahOchnPlat, aiahOchnCelevoe,
                    psjdOchnBudg, psjdOchnLgot, psjdOchnPlat)
            listUNIT.addAll(collection)
        }

        scoreTypes?.physicsStudents?.let { checkForNTTK(it) }
        scoreTypes?.computerScienceStudents?.let { checkForNTTK(it) }
        scoreTypes?.socialScienceStudents?.let { checkForNTTK(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForNTTK(it) }

        scoreTypes?.physicsStudents?.let { checkForNTTS(it) }
        scoreTypes?.computerScienceStudents?.let { checkForNTTS(it) }
        scoreTypes?.socialScienceStudents?.let { checkForNTTS(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForNTTS(it) }

        scoreTypes?.physicsStudents?.let { checkForPM(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPM(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPM(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPM(it) }

        scoreTypes?.physicsStudents?.let { checkForPSJD(it) }
        scoreTypes?.computerScienceStudents?.let { checkForPSJD(it) }
        scoreTypes?.socialScienceStudents?.let { checkForPSJD(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForPSJD(it) }

        scoreTypes?.physicsStudents?.let { checkForTTP(it) }
        scoreTypes?.computerScienceStudents?.let { checkForTTP(it) }
        scoreTypes?.socialScienceStudents?.let { checkForTTP(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForTTP(it) }

        scoreTypes?.physicsStudents?.let { checkForETTK(it) }
        scoreTypes?.computerScienceStudents?.let { checkForETTK(it) }
        scoreTypes?.socialScienceStudents?.let { checkForETTK(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForETTK(it) }

        /*val separatedNTTK = separateNTTK(nttk)
        val separatedNTTS = separateNTTS(ntts)
        val separatedPM = separatePM(pm)
        val separatedPSJD = separatePSJD(psjd)
        val separatedTTP = separateTTP(ttp)
        val separatedETTK = separateETTK(ettk)*/

        separateNTTK(nttk)
        separateNTTS(ntts)
        separatePM(pm)
        separatePSJD(psjd)
        separateTTP(ttp)
        separateETTK(ettk)

        /*val unit = UNIT(separatedNTTK, separatedNTTS, separatedPM,
                separatedPSJD, separatedTTP, separatedETTK)
        myApplication.saveUNIT(unit)*/
        myApplication.saveUNIT(listUNIT)
    }
    override fun checkForFEE() {
        val scoreTypes = myApplication.returnScoreTypes()
        val rad = ArrayList<Student>()
        val tit = ArrayList<Student>()
        val ein = ArrayList<Student>()
        val eie = ArrayList<Student>()
        val em = ArrayList<Student>()

        val listFEE = ArrayList<ArrayList<Student>>()

        fun checkForRAD(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "РАД_очн_бюдж" || list[i].specialtyFirst == "РАД_очн_льгот"
                                || list[i].specialtyFirst == "РАД_очн_плат" || list[i].specialtyFirst == "РАД_очн_целевое")
                        || (list[i].specialtySecond == "РАД_очн_бюдж" || list[i].specialtySecond == "РАД_очн_льгот"
                                || list[i].specialtySecond == "РАД_очн_плат" || list[i].specialtySecond == "РАД_очн_целевое")
                        || (list[i].specialtyThird == "РАД_очн_бюдж" || list[i].specialtyThird == "РАД_очн_льгот"
                                || list[i].specialtyThird == "РАД_очн_плат" || list[i].specialtyThird == "РАД_очн_целевое")) {
                    rad.add(list[i])
                }
            }
        }
        fun checkForTIT(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ТиТ(ИСК)_заочн_плат" || list[i].specialtyFirst == "ТиТ_очн_бюдж"
                                || list[i].specialtyFirst == "ТиТ_очн_льгот" || list[i].specialtyFirst == "ТиТ_очн_плат")
                        || (list[i].specialtySecond == "ТиТ(ИСК)_заочн_плат" || list[i].specialtySecond == "ТиТ_очн_бюдж"
                                || list[i].specialtySecond == "ТиТ_очн_льгот" || list[i].specialtySecond == "ТиТ_очн_плат")
                        || (list[i].specialtyThird == "ТиТ(ИСК)_заочн_плат" || list[i].specialtyThird == "ТиТ_очн_бюдж"
                                || list[i].specialtyThird == "ТиТ_очн_льгот" || list[i].specialtyThird == "ТиТ_очн_плат")) {
                    tit.add(list[i])
                }
            }
        }
        fun checkForEIN(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭиН(МТЭ)_очн_бюдж" || list[i].specialtyFirst == "ЭиН(МТЭ)_очн_льгот"
                                || list[i].specialtyFirst == "ЭиН(МТЭ)_очн_плат" || list[i].specialtyFirst == "ЭиН(ПЭ)_очн_бюдж"
                                || list[i].specialtyFirst == "ЭиН(ПЭ)_очн_льгот" || list[i].specialtyFirst == "ЭиН(ПЭ)_очн_плат")
                        || (list[i].specialtySecond == "ЭиН(МТЭ)_очн_бюдж" || list[i].specialtySecond == "ЭиН(МТЭ)_очн_льгот"
                                || list[i].specialtySecond == "ЭиН(МТЭ)_очн_плат" || list[i].specialtySecond == "ЭиН(ПЭ)_очн_бюдж"
                                || list[i].specialtySecond == "ЭиН(ПЭ)_очн_льгот" || list[i].specialtySecond == "ЭиН(ПЭ)_очн_плат")
                        || (list[i].specialtyThird == "ЭиН(МТЭ)_очн_бюдж" || list[i].specialtyThird == "ЭиН(МТЭ)_очн_льгот"
                                || list[i].specialtyThird == "ЭиН(МТЭ)_очн_плат" || list[i].specialtyThird == "ЭиН(ПЭ)_очн_бюдж"
                                || list[i].specialtyThird == "ЭиН(ПЭ)_очн_льгот" || list[i].specialtyThird == "ЭиН(ПЭ)_очн_плат")) {
                    ein.add(list[i])
                }
            }
        }
        fun checkForEIE(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭиЭ_заочн_плат" || list[i].specialtyFirst == "ЭиЭ_очн_бюдж"
                                || list[i].specialtyFirst == "ЭиЭ_очн_льгот" || list[i].specialtyFirst == "ЭиЭ_очн_плат"
                                || list[i].specialtyFirst == "ЭиЭ_очн_целевое") || (list[i].specialtySecond == "ЭиЭ_заочн_плат"
                                || list[i].specialtySecond == "ЭиЭ_очн_бюдж" || list[i].specialtySecond == "ЭиЭ_очн_льгот"
                                || list[i].specialtySecond == "ЭиЭ_очн_плат" || list[i].specialtySecond == "ЭиЭ_очн_целевое")
                        || (list[i].specialtyThird == "ЭиЭ_заочн_плат" || list[i].specialtyThird == "ЭиЭ_очн_бюдж"
                                || list[i].specialtyThird == "ЭиЭ_очн_льгот" || list[i].specialtyThird == "ЭиЭ_очн_плат"
                                || list[i].specialtyThird == "ЭиЭ_очн_целевое")) {
                    eie.add(list[i])
                }
            }
        }
        fun checkForEM(list: ArrayList<Student>) {
            for (i in 0 until list.size) {
                if ((list[i].specialtyFirst == "ЭМ(ДВС)_заочн_бюдж" || list[i].specialtyFirst == "ЭМ(ДВС)_заочн_льгот"
                                || list[i].specialtyFirst == "ЭМ(ДВС)_заочн_плат" || list[i].specialtyFirst == "ЭМ(ДВС)_очн_бюдж"
                                || list[i].specialtyFirst == "ЭМ(ДВС)_очн_льгот" || list[i].specialtyFirst == "ЭМ(ДВС)_очн_плат"
                                || list[i].specialtyFirst == "ЭМ(Т)_очн_бюдж" || list[i].specialtyFirst == "ЭМ(Т)_очн_льгот"
                                || list[i].specialtyFirst == "ЭМ(Т)_очн_плат" || list[i].specialtyFirst == "ЭМ(Т)_очн_целевое"
                                || list[i].specialtyFirst == "ЭМ(ЭМКС)_заочн_плат") || (list[i].specialtySecond == "ЭМ(ДВС)_заочн_бюдж"
                                || list[i].specialtySecond == "ЭМ(ДВС)_заочн_льгот" || list[i].specialtySecond == "ЭМ(ДВС)_заочн_плат"
                                || list[i].specialtySecond == "ЭМ(ДВС)_очн_бюдж" || list[i].specialtySecond == "ЭМ(ДВС)_очн_льгот"
                                || list[i].specialtySecond == "ЭМ(ДВС)_очн_плат" || list[i].specialtySecond == "ЭМ(Т)_очн_бюдж"
                                || list[i].specialtySecond == "ЭМ(Т)_очн_льгот" || list[i].specialtySecond == "ЭМ(Т)_очн_плат"
                                || list[i].specialtySecond == "ЭМ(Т)_очн_целевое" || list[i].specialtySecond == "ЭМ(ЭМКС)_заочн_плат")
                        || (list[i].specialtyThird == "ЭМ(ДВС)_заочн_бюдж" || list[i].specialtyThird == "ЭМ(ДВС)_заочн_льгот"
                                || list[i].specialtyThird == "ЭМ(ДВС)_заочн_плат" || list[i].specialtyThird == "ЭМ(ДВС)_очн_бюдж"
                                || list[i].specialtyThird == "ЭМ(ДВС)_очн_льгот" || list[i].specialtyThird == "ЭМ(ДВС)_очн_плат"
                                || list[i].specialtyThird == "ЭМ(Т)_очн_бюдж" || list[i].specialtyThird == "ЭМ(Т)_очн_льгот"
                                || list[i].specialtyThird == "ЭМ(Т)_очн_плат" || list[i].specialtyThird == "ЭМ(Т)_очн_целевое"
                                || list[i].specialtyThird == "ЭМ(ЭМКС)_заочн_плат")) {
                    em.add(list[i])
                }
            }
        }

        fun separateRAD(list: ArrayList<Student>)/*: RAD*/ {
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "РАД_очн_бюдж" || list[i].specialtySecond == "РАД_очн_бюдж"
                        || list[i].specialtyThird == "РАД_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "РАД_очн_льгот" || list[i].specialtySecond == "РАД_очн_льгот"
                        || list[i].specialtyThird == "РАД_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "РАД_очн_плат" || list[i].specialtySecond == "РАД_очн_плат"
                        || list[i].specialtyThird == "РАД_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "РАД_очн_целевое" || list[i].specialtySecond == "РАД_очн_целевое"
                        || list[i].specialtyThird == "РАД_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            //return RAD(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            val collection = arrayListOf(ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listFEE.addAll(collection)

            /*listFEE.add(ochnBudg)
            listFEE.add(ochnLgot)
            listFEE.add(ochnPlat)
            listFEE.add(ochnCelevoe)*/
        }
        fun separateTIT(list: ArrayList<Student>)/*: TIT*/ {
            val iskZaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ТиТ(ИСК)_заочн_плат" || list[i].specialtySecond == "ТиТ(ИСК)_заочн_плат"
                        || list[i].specialtyThird == "ТиТ(ИСК)_заочн_плат")
                    iskZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ТиТ_очн_бюдж" || list[i].specialtySecond == "ТиТ_очн_бюдж"
                        || list[i].specialtyThird == "ТиТ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ТиТ_очн_льгот" || list[i].specialtySecond == "ТиТ_очн_льгот"
                        || list[i].specialtyThird == "ТиТ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ТиТ_очн_плат" || list[i].specialtySecond == "ТиТ_очн_плат"
                        || list[i].specialtyThird == "ТиТ_очн_плат")
                    ochnPlat.add(list[i])
            }
            val collection = arrayListOf(iskZaochnPlat, ochnBudg, ochnLgot, ochnPlat)
            listFEE.addAll(collection)

            /*listFEE.add(iskZaochnPlat)
            listFEE.add(ochnBudg)
            listFEE.add(ochnLgot)
            listFEE.add(ochnPlat)*/
        }
        fun separateEIN(list: ArrayList<Student>)/*: EIN*/ {
            val mteOchnBudg = ArrayList<Student>()
            val mteOchnLgot = ArrayList<Student>()
            val mteOchnPlat = ArrayList<Student>()
            val peOchnBudg = ArrayList<Student>()
            val peOchnLgot = ArrayList<Student>()
            val peOchnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭиН(МТЭ)_очн_бюдж" || list[i].specialtySecond == "ЭиН(МТЭ)_очн_бюдж"
                        || list[i].specialtyThird == "ЭиН(МТЭ)_очн_бюдж")
                    mteOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭиН(МТЭ)_очн_льгот" || list[i].specialtySecond == "ЭиН(МТЭ)_очн_льгот"
                        || list[i].specialtyThird == "ЭиН(МТЭ)_очн_льгот")
                    mteOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭиН(МТЭ)_очн_плат" || list[i].specialtySecond == "ЭиН(МТЭ)_очн_плат"
                        || list[i].specialtyThird == "ЭиН(МТЭ)_очн_плат")
                    mteOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭиН(ПЭ)_очн_бюдж" || list[i].specialtySecond == "ЭиН(ПЭ)_очн_бюдж"
                        || list[i].specialtyThird == "ЭиН(ПЭ)_очн_бюдж")
                    peOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭиН(ПЭ)_очн_льгот" || list[i].specialtySecond == "ЭиН(ПЭ)_очн_льгот"
                        || list[i].specialtyThird == "ЭиН(ПЭ)_очн_льгот")
                    peOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭиН(ПЭ)_очн_плат" || list[i].specialtySecond == "ЭиН(ПЭ)_очн_плат"
                        || list[i].specialtyThird == "ЭиН(ПЭ)_очн_плат")
                    peOchnPlat.add(list[i])
            }
            val collection = arrayListOf(mteOchnBudg, mteOchnLgot, mteOchnPlat,
                    peOchnBudg, peOchnLgot, peOchnPlat)
            listFEE.addAll(collection)
        }
        fun separateEIE(list: ArrayList<Student>)/*: EIE*/ {
            val zaochnPlat = ArrayList<Student>()
            val ochnBudg = ArrayList<Student>()
            val ochnLgot = ArrayList<Student>()
            val ochnPlat = ArrayList<Student>()
            val ochnCelevoe = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭиЭ_заочн_плат" || list[i].specialtySecond == "ЭиЭ_заочн_плат"
                        || list[i].specialtyThird == "ЭиЭ_заочн_плат")
                    zaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭиЭ_очн_бюдж" || list[i].specialtySecond == "ЭиЭ_очн_бюдж"
                        || list[i].specialtyThird == "ЭиЭ_очн_бюдж")
                    ochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭиЭ_очн_льгот" || list[i].specialtySecond == "ЭиЭ_очн_льгот"
                        || list[i].specialtyThird == "ЭиЭ_очн_льгот")
                    ochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭиЭ_очн_плат" || list[i].specialtySecond == "ЭиЭ_очн_плат"
                        || list[i].specialtyThird == "ЭиЭ_очн_плат")
                    ochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭиЭ_очн_целевое" || list[i].specialtySecond == "ЭиЭ_очн_целевое"
                        || list[i].specialtyThird == "ЭиЭ_очн_целевое")
                    ochnCelevoe.add(list[i])
            }
            val collection = arrayListOf(zaochnPlat, ochnBudg, ochnLgot, ochnPlat, ochnCelevoe)
            listFEE.addAll(collection)
        }
        fun separateEM(list: ArrayList<Student>)/*: EM*/ {
            val dvsZaochnBudg = ArrayList<Student>()
            val dvsZaochnLgot = ArrayList<Student>()
            val dvsZaochnPlat = ArrayList<Student>()
            val dvsOchnBudg = ArrayList<Student>()
            val dvsOchnLgot = ArrayList<Student>()
            val dvsOchnPlat = ArrayList<Student>()
            val tOchnBudg = ArrayList<Student>()
            val tOchnLgot = ArrayList<Student>()
            val tOchnPlat = ArrayList<Student>()
            val tOchnCelevoe = ArrayList<Student>()
            val emksZaochnPlat = ArrayList<Student>()

            for (i in 0 until list.size) {
                if (list[i].specialtyFirst == "ЭМ(ДВС)_заочн_бюдж" || list[i].specialtySecond == "ЭМ(ДВС)_заочн_бюдж"
                        || list[i].specialtyThird == "ЭМ(ДВС)_заочн_бюдж")
                    dvsZaochnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ДВС)_заочн_льгот" || list[i].specialtySecond == "ЭМ(ДВС)_заочн_льгот"
                        || list[i].specialtyThird == "ЭМ(ДВС)_заочн_льгот")
                    dvsZaochnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ДВС)_заочн_плат" || list[i].specialtySecond == "ЭМ(ДВС)_заочн_плат"
                        || list[i].specialtyThird == "ЭМ(ДВС)_заочн_плат")
                    dvsZaochnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ДВС)_очн_бюдж" || list[i].specialtySecond == "ЭМ(ДВС)_очн_бюдж"
                        || list[i].specialtyThird == "ЭМ(ДВС)_очн_бюдж")
                    dvsOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ДВС)_очн_льгот" || list[i].specialtySecond == "ЭМ(ДВС)_очн_льгот"
                        || list[i].specialtyThird == "ЭМ(ДВС)_очн_льгот")
                    dvsOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ДВС)_очн_плат" || list[i].specialtySecond == "ЭМ(ДВС)_очн_плат"
                        || list[i].specialtyThird == "ЭМ(ДВС)_очн_плат")
                    dvsOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(Т)_очн_бюдж" || list[i].specialtySecond == "ЭМ(Т)_очн_бюдж"
                        || list[i].specialtyThird == "ЭМ(Т)_очн_бюдж")
                    tOchnBudg.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(Т)_очн_льгот" || list[i].specialtySecond == "ЭМ(Т)_очн_льгот"
                        || list[i].specialtyThird == "ЭМ(Т)_очн_льгот")
                    tOchnLgot.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(Т)_очн_плат" || list[i].specialtySecond == "ЭМ(Т)_очн_плат"
                        || list[i].specialtyThird == "ЭМ(Т)_очн_плат")
                    tOchnPlat.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(Т)_очн_целевое" || list[i].specialtySecond == "ЭМ(Т)_очн_целевое"
                        || list[i].specialtyThird == "ЭМ(Т)_очн_целевое")
                    tOchnCelevoe.add(list[i])
                else if (list[i].specialtyFirst == "ЭМ(ЭМКС)_заочн_плат" || list[i].specialtySecond == "ЭМ(ЭМКС)_заочн_плат"
                        || list[i].specialtyThird == "ЭМ(ЭМКС)_заочн_плат")
                    emksZaochnPlat.add(list[i])
            }
            val collection = arrayListOf(dvsZaochnBudg, dvsZaochnLgot, dvsZaochnPlat, dvsOchnBudg,
                    dvsOchnLgot, dvsOchnPlat, tOchnBudg, tOchnLgot, tOchnPlat, tOchnCelevoe, emksZaochnPlat)
            listFEE.addAll(collection)
        }

        scoreTypes?.physicsStudents?.let { checkForRAD(it) }
        scoreTypes?.computerScienceStudents?.let { checkForRAD(it) }
        scoreTypes?.socialScienceStudents?.let { checkForRAD(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForRAD(it) }

        scoreTypes?.physicsStudents?.let { checkForTIT(it) }
        scoreTypes?.computerScienceStudents?.let { checkForTIT(it) }
        scoreTypes?.socialScienceStudents?.let { checkForTIT(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForTIT(it) }

        scoreTypes?.physicsStudents?.let { checkForEIN(it) }
        scoreTypes?.computerScienceStudents?.let { checkForEIN(it) }
        scoreTypes?.socialScienceStudents?.let { checkForEIN(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForEIN(it) }

        scoreTypes?.physicsStudents?.let { checkForEIE(it) }
        scoreTypes?.computerScienceStudents?.let { checkForEIE(it) }
        scoreTypes?.socialScienceStudents?.let { checkForEIE(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForEIE(it) }

        scoreTypes?.physicsStudents?.let { checkForEM(it) }
        scoreTypes?.computerScienceStudents?.let { checkForEM(it) }
        scoreTypes?.socialScienceStudents?.let { checkForEM(it) }
        scoreTypes?.partAndAllDataStudents?.let { checkForEM(it) }

        /*val separatedRAD = separateRAD(rad)
        val separatedTIT = separateTIT(tit)
        val separatedEIN = separateEIN(ein)
        val separatedEIE = separateEIE(eie)
        val separatedEM = separateEM(em)*/

        separateRAD(rad)
        separateTIT(tit)
        separateEIN(ein)
        separateEIE(eie)
        separateEM(em)

        myApplication.saveFEE(listFEE)

        //val fee = FEE(separatedRAD, separatedTIT, separatedEIN, separatedEIE, separatedEM)
        //myApplication.saveFEE(fee)
    }

    // Четвертый этап
    override fun checkSpecialtiesForMinimalScore(context: Context) {
        val listUNTI = checkUNTIForMinimalScore(context, 0)
        val listFEU = checkFEUForMinimalScore(context, 1)
        val listFIT = checkFITForMinimalScore(context, 2)
        val listMTF = checkMTFForMinimalScore(context, 3)
        val listUNIT = checkUNITForMinimalScore(context, 4)
        val listFEE = checkFEEForMinimalScore(context, 5)

        val faculties = listUNTI?.let { listFEU?.let { it1 ->
            listFIT?.let { it2 -> listMTF?.let { it3 ->
                listUNIT?.let { it4 ->
                    listFEE?.let { it5 -> Faculties(it, it1, it2, it3, it4, it5) } }
            } } } }
        faculties?.let { myApplication.saveFaculties(it) }
    }
    override fun checkUNTIForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listUNTI = returnUNTI()

        list?.let {
            for (i in 0 until list.size) {
                listUNTI?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }

        return list
    }
    override fun checkFEUForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listFEU = returnFEU()

        list?.let {
            for (i in 0 until list.size) {
                listFEU?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }
        return list
    }
    override fun checkFITForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listFIT = returnFIT()

        list?.let {
            for (i in 0 until list.size) {
                listFIT?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }
        return list
    }
    override fun checkMTFForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listMTF = returnMTF()

        list?.let {
            for (i in 0 until list.size) {
                listMTF?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }
        return list
    }
    override fun checkUNITForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listUNIT = returnUNIT()

        list?.let {
            for (i in 0 until list.size) {
                listUNIT?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }
        return list
    }
    override fun checkFEEForMinimalScore(context: Context, position: Int): ArrayList<Specialty>? {
        val list = getSpecialtiesListByPosition(position)
        val listFEE = returnFEE()

        list?.let {
            for (i in 0 until list.size) {
                listFEE?.let {
                    list[i].amountOfStatements = it[i].size

                    when(list[i].profileTerm) {
                        "Физика" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.physics }

                            list[i].scoreTitle = context?.getString(R.string.facultyMathsAndPhysics)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.physics }
                        }
                        "Обществознание" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.socialScience }

                            list[i].scoreTitle = context?.getString(R.string.facultyMathsAndSocialScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.socialScience }
                        }
                        "Информатика и ИКТ" -> {
                            val minimalScore = it[i].minBy { r -> r.maths + r.computerScience }

                            list[i].scoreTitle = context?.getString(R.string.facultyMathsAndComputerScience)

                            minimalScore?.let {  r -> list[i].minimalScore = r.maths + r.computerScience }
                        }
                        else -> return null
                    }
                }
            }
        }
        return list
    }
    override fun getSpecialtiesListByPosition(pos: Int)
            : ArrayList<Specialty>? {
        val faculties = myApplication.returnFaculties()
        return when (pos) {
            //УНТИ
            0 -> faculties?.untiList
            //ФЭУ
            1 -> faculties?.feuList
            //ФИТ
            2 -> faculties?.fitList
            //МТФ
            3 -> faculties?.mtfList
            //УНИТ
            4 -> faculties?.unitList
            //ФЭЭ
            5 -> faculties?.feeList
            else -> null
        }
    }

    // Пятый этап
    override fun checkForZeroMinimalScore() {
        val scores = myApplication.returnScore()
        //val zeroList = ArrayList<Specialty>()
        val zeroList = ArrayList<ArrayList<Specialty>>()

        val listUNTI = checkUNTIForZeroMinimalScore(0, scores)
        val listFEU = checkFEUForZeroMinimalScore(1, scores)
        val listFIT = checkFITForZeroMinimalScore(2, scores)
        val listMTF = checkMTFForZeroMinimalScore(3, scores)
        val listUNIT = checkUNITForZeroMinimalScore(4, scores)
        val listFEE = checkFEEForZeroMinimalScore(5, scores)

        /*zeroList.addAll(listUNTI)
        zeroList.addAll(listFEU)
        zeroList.addAll(listFIT)
        zeroList.addAll(listMTF)
        zeroList.addAll(listUNIT)
        zeroList.addAll(listFEE)
        showLog("ZEROLIST ${zeroList.size}")*/

        val collection = arrayListOf(listUNTI, listFEU, listFIT, listMTF, listUNIT, listFEE)
        zeroList.addAll(collection)

        myApplication.saveListOfSpecialtiesWithZeroMinimalScore(zeroList)
    }
    override fun checkUNTIForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                                                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }
    override fun checkFEUForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }
    override fun checkFITForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }
    override fun checkMTFForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }
    override fun checkUNITForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }
    override fun checkFEEForZeroMinimalScore(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithZeroMinimalScore = ArrayList<Specialty>()

        list?.let {
            scores?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Физика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Информатика" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.scoreTitle == "Математика + Обществознание" &&
                            it.minimalScore == 0} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Информатика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Физика") && it.minimalScore == 0} as ArrayList<Specialty>
                // Обществознание + Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Информатика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithZeroMinimalScore = list.filter {(it.scoreTitle == "Математика + Физика"
                            || it.scoreTitle == "Математика + Обществознание") && it.minimalScore == 0} as ArrayList<Specialty>
            }
        }
        return listWithZeroMinimalScore
    }

    // Шестой этап
    override fun checkForFittingSpecialties() {
        val scores = myApplication.returnScore()
        val fittingList = ArrayList<ArrayList<Specialty>>()

        val listUNTI = checkUNTIForFittingSpecialties(0, scores)
        val listFEU = checkFEUForFittingSpecialties(1, scores)
        val listFIT = checkFITForFittingSpecialties(2, scores)
        val listMTF = checkMTFForFittingSpecialties(3, scores)
        val listUNIT = checkUNITForFittingSpecialties(4, scores)
        val listFEE = checkFEEForFittingSpecialties(5, scores)

        showLog("${listUNTI.size} ${listFEU.size} ${listFIT.size} ${listMTF.size}" +
                "${listUNIT.size} ${listFEE.size}")

        /*val collection = arrayListOf(listUNTI, listFEU, listFIT, listMTF, listUNIT, listFEE)
        zeroList.addAll(collection)*/

        //myApplication.saveListOfSpecialtiesWithZeroMinimalScore(zeroList)
    }
    override fun checkUNTIForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }
    override fun checkFEUForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                                    && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                                    || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }
    override fun checkFITForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                                    && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                                    || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }
    override fun checkMTFForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                                    && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                                    || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }
    override fun checkUNITForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                                    && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                                    || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }
    override fun checkFEEForFittingSpecialties(position: Int, scores: Score?): ArrayList<Specialty> {
        val list = getSpecialtiesListByPosition(position)
        var listWithFittingSpecialties = ArrayList<Specialty>()

        scores?.let {
            val mathsAndPhysics: Int = scores.maths + scores.physics//scores?.let { scores.maths + scores.physics }
            val mathsAndComputerScience = scores.maths + scores.computerScience
            val mathsAndSocialScience = scores.maths + scores.socialScience

            list?.let {
                // Физика
                if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                            || it.minimalScore < mathsAndPhysics)} as ArrayList<Specialty>
                // Информатика
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                            || it.minimalScore < mathsAndComputerScience)} as ArrayList<Specialty>
                // Обществознание
                else if (scores.physics == 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            it.scoreTitle == "Математика + Обществознание" && (it.minimalScore == mathsAndSocialScience
                            || it.minimalScore < mathsAndSocialScience)} as ArrayList<Specialty>
                // Все три
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 && ((it.scoreTitle == "Математика + Физика"
                            && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics))
                            || (it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience ||
                            it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                            && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
                // Информатика + Физика
                else if (scores.physics != 0 && scores.computerScience != 0 && scores.socialScience == 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Физика"
                                    && (it.minimalScore == mathsAndPhysics || it.minimalScore < mathsAndPhysics)))}
                            as ArrayList<Specialty>
                // Информатика + Обществознание
                else if (scores.physics == 0 && scores.computerScience != 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Информатика" && (it.minimalScore == mathsAndComputerScience
                                    || it.minimalScore < mathsAndComputerScience)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>

                // Физика + Обществознание
                else if (scores.physics != 0 && scores.computerScience == 0 && scores.socialScience != 0 )
                    listWithFittingSpecialties = list.filter {it.minimalScore != 0 &&
                            ((it.scoreTitle == "Математика + Физика" && (it.minimalScore == mathsAndPhysics
                                    || it.minimalScore < mathsAndPhysics)) || (it.scoreTitle == "Математика + Обществознание"
                                    && (it.minimalScore == mathsAndSocialScience || it.minimalScore < mathsAndSocialScience)))}
                            as ArrayList<Specialty>
            }
        }
        return listWithFittingSpecialties
    }

    override fun returnFacultyList(): ArrayList<Faculty>?
            = myApplication.returnFacultyList()
    override fun returnFaculties(): Faculties?
            = myApplication.returnFaculties()
    override fun returnFacultyBundle(context: Context, position: Int, titleId: Int): Bundle {
        val bundle = Bundle()
        val title = context.getString(titleId)

        bundle.putString("title", title)
        bundle.putInt("pos", position)
        return bundle
    }
    override fun returnUNTI(): ArrayList<ArrayList<Student>>?
            = myApplication.returnUNTI()
    override fun returnFEU(): ArrayList<ArrayList<Student>>?
            = myApplication.returnFEU()
    override fun returnFIT(): ArrayList<ArrayList<Student>>?
            = myApplication.returnFIT()
    override fun returnMTF(): ArrayList<ArrayList<Student>>?
            = myApplication.returnMTF()
    override fun returnUNIT(): ArrayList<ArrayList<Student>>?
            = myApplication.returnUNIT()
    override fun returnFEE(): ArrayList<ArrayList<Student>>?
            = myApplication.returnFEE()
}