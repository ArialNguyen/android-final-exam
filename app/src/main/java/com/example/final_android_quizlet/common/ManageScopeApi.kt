package com.example.final_android_quizlet.common

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ManageScopeApi {
    fun getResponseWithAwait(func: suspend () -> ResponseObject, operation: CallbackInterface) {
        return runBlocking {
            try {
                operation.onBegin()
                if (!operation.onValidate()) {
                    throw Exception("Invalidate data")
                }
                val res: ResponseObject = func()
                operation.onCallback(res)
            } catch (e: Exception) {
                TODO()
            } finally {
                operation.onFinally()
            }
        }
    }

    fun getResponseWithCallback(
        lifecycleScope: LifecycleCoroutineScope,
        func: suspend () -> ResponseObject,
        callback: CallbackInterface
    ): Job {
        return lifecycleScope.launch {
            try {
                callback.onBegin()
                if (!callback.onValidate()) {
                    throw Exception("Invalidate data")
                }
                val res: ResponseObject = func()
                callback.onCallback(res)
            } catch (e: Exception) {
                TODO()
            } finally {
                callback.onFinally()
            }
        }
    }

}