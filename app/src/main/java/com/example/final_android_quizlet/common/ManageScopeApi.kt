package com.example.final_android_quizlet.common

import androidx.lifecycle.LifecycleCoroutineScope
import com.example.final_android_quizlet.dao.ResponseObject
import com.example.final_android_quizlet.db.CallbackInterface
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ManageScopeApi {
    private lateinit var lifecycleScope: LifecycleCoroutineScope
    fun getResponseWithAwait(func: suspend () -> ResponseObject, callback: CallbackInterface): Job{
        return runBlocking {
            launch {
                try {
                    callback.onBegin()
                    if(!callback.onValidate()){
                        throw Exception("Invalidate data")
                    }
                    val res: ResponseObject = func()
                    callback.onCallback(res)
                }catch (e: Exception){
                    TODO()
                }finally {
                    callback.onFinally()
                }
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
                if(!callback.onValidate()){
                    throw Exception("Invalidate data")
                }
                val res: ResponseObject = func()
                callback.onCallback(res)
            }catch (e: Exception){
                TODO()
            }finally {
                callback.onFinally()
                lifecycleScope.cancel()
            }
        }
    }

}