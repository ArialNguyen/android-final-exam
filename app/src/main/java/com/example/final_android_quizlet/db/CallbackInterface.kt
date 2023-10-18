package com.example.final_android_quizlet.db

import com.example.final_android_quizlet.dao.ResponseObject

interface CallbackInterface {
     fun onBegin() {}
     fun onValidate(): Boolean{
          return true
     }
     fun onCallback(res: ResponseObject) {}

     fun onFinally(){}

}
