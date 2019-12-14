/*
 * Copyright (c) Kuba Szczodrzyński 2019-10-5.
 */

package pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik

import com.google.gson.JsonObject
import pl.szczodrzynski.edziennik.App
import pl.szczodrzynski.edziennik.api.v2.CODE_INTERNAL_LIBRUS_ACCOUNT_410
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.data.MobidziennikData
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.data.web.MobidziennikWebGetAttachment
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.data.web.MobidziennikWebGetMessage
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.firstlogin.MobidziennikFirstLogin
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.login.MobidziennikLogin
import pl.szczodrzynski.edziennik.api.v2.edziennik.mobidziennik.login.MobidziennikLoginWeb
import pl.szczodrzynski.edziennik.api.v2.interfaces.EdziennikCallback
import pl.szczodrzynski.edziennik.api.v2.interfaces.EdziennikInterface
import pl.szczodrzynski.edziennik.api.v2.mobidziennikLoginMethods
import pl.szczodrzynski.edziennik.api.v2.models.ApiError
import pl.szczodrzynski.edziennik.api.v2.prepare
import pl.szczodrzynski.edziennik.data.db.modules.login.LoginStore
import pl.szczodrzynski.edziennik.data.db.modules.messages.Message
import pl.szczodrzynski.edziennik.data.db.modules.messages.MessageFull
import pl.szczodrzynski.edziennik.data.db.modules.profiles.Profile
import pl.szczodrzynski.edziennik.utils.Utils.d

class Mobidziennik(val app: App, val profile: Profile?, val loginStore: LoginStore, val callback: EdziennikCallback) : EdziennikInterface {
    companion object {
        private const val TAG = "Mobidziennik"
    }

    val internalErrorList = mutableListOf<Int>()
    val data: DataMobidziennik

    init {
        data = DataMobidziennik(app, profile, loginStore).apply {
            callback = wrapCallback(this@Mobidziennik.callback)
            satisfyLoginMethods()
        }
    }

    private fun completed() {
        data.saveData()
        data.notify {
            callback.onCompleted()
        }
    }

    /*    _______ _                     _                  _ _   _
         |__   __| |              /\   | |                (_) | | |
            | |  | |__   ___     /  \  | | __ _  ___  _ __ _| |_| |__  _ __ ___
            | |  | '_ \ / _ \   / /\ \ | |/ _` |/ _ \| '__| | __| '_ \| '_ ` _ \
            | |  | | | |  __/  / ____ \| | (_| | (_) | |  | | |_| | | | | | | | |
            |_|  |_| |_|\___| /_/    \_\_|\__, |\___/|_|  |_|\__|_| |_|_| |_| |_|
                                           __/ |
                                          |__*/
    override fun sync(featureIds: List<Int>, viewId: Int?, arguments: JsonObject?) {
        data.arguments = arguments
        data.prepare(mobidziennikLoginMethods, MobidziennikFeatures, featureIds, viewId)
        d(TAG, "LoginMethod IDs: ${data.targetLoginMethodIds}")
        d(TAG, "Endpoint IDs: ${data.targetEndpointIds}")
        MobidziennikLogin(data) {
            MobidziennikData(data) {
                completed()
            }
        }
    }

    override fun getMessage(message: MessageFull) {
        MobidziennikLoginWeb(data) {
            MobidziennikWebGetMessage(data, message) {
                completed()
            }
        }
    }

    override fun markAllAnnouncementsAsRead() {

    }

    override fun getAttachment(message: Message, attachmentId: Long, attachmentName: String) {
        MobidziennikLoginWeb(data) {
            MobidziennikWebGetAttachment(data, message, attachmentId, attachmentName) {
                completed()
            }
        }
    }

    override fun firstLogin() {
        MobidziennikFirstLogin(data) {
            completed()
        }
    }

    override fun cancel() {
        d(TAG, "Cancelled")
        data.cancel()
    }

    private fun wrapCallback(callback: EdziennikCallback): EdziennikCallback {
        return object : EdziennikCallback {
            override fun onCompleted() {
                callback.onCompleted()
            }

            override fun onProgress(step: Float) {
                callback.onProgress(step)
            }

            override fun onStartProgress(stringRes: Int) {
                callback.onStartProgress(stringRes)
            }

            override fun onError(apiError: ApiError) {
                when (apiError.errorCode) {
                    in internalErrorList -> {
                        // finish immediately if the same error occurs twice during the same sync
                        callback.onError(apiError)
                    }
                    CODE_INTERNAL_LIBRUS_ACCOUNT_410 -> {
                        internalErrorList.add(apiError.errorCode)
                        loginStore.removeLoginData("refreshToken") // force a clean login
                        //loginLibrus()
                    }
                    else -> callback.onError(apiError)
                }
            }
        }
    }
}