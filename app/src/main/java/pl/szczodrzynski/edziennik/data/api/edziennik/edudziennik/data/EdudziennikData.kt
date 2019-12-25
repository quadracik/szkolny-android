/*
 * Copyright (c) Kacper Ziubryniewicz 2019-12-22
 */

package pl.szczodrzynski.edziennik.data.api.edziennik.edudziennik.data

import pl.szczodrzynski.edziennik.R
import pl.szczodrzynski.edziennik.data.api.edziennik.edudziennik.*
import pl.szczodrzynski.edziennik.data.api.edziennik.edudziennik.data.web.*
import pl.szczodrzynski.edziennik.utils.Utils

class EdudziennikData(val data: DataEdudziennik, val onSuccess: () -> Unit) {
    companion object {
        private const val TAG = "EdudziennikData"
    }

    init {
        nextEndpoint(onSuccess)
    }

    private fun nextEndpoint(onSuccess: () -> Unit) {
        if (data.targetEndpointIds.isEmpty()) {
            onSuccess()
            return
        }
        if (data.cancelled) {
            onSuccess()
            return
        }
        useEndpoint(data.targetEndpointIds.removeAt(0)) {
            data.progress(data.progressStep)
            nextEndpoint(onSuccess)
        }
    }

    private fun useEndpoint(endpointId: Int, onSuccess: () -> Unit) {
        Utils.d(TAG, "Using endpoint $endpointId")
        when (endpointId) {
            ENDPOINT_EDUDZIENNIK_WEB_START -> {
                data.startProgress(R.string.edziennik_progress_endpoint_data)
                EdudziennikWebStart(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_TEACHERS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_teachers)
                EdudziennikWebTeachers(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_GRADES -> {
                data.startProgress(R.string.edziennik_progress_endpoint_grades)
                EdudziennikWebGrades(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_TIMETABLE -> {
                data.startProgress(R.string.edziennik_progress_endpoint_timetable)
                EdudziennikWebTimetable(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_EXAMS -> {
                data.startProgress(R.string.edziennik_progress_endpoint_exams)
                EdudziennikWebExams(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_ATTENDANCE -> {
                data.startProgress(R.string.edziennik_progress_endpoint_attendance)
                EdudziennikWebAttendance(data, onSuccess)
            }
            ENDPOINT_EDUDZIENNIK_WEB_LUCKY_NUMBER -> {
                data.startProgress(R.string.edziennik_progress_endpoint_lucky_number)
                EdudziennikWebLuckyNumber(data, onSuccess)
            }
            else -> onSuccess()
        }
    }
}