package com.example.baksomanagement.data.remote.api

import com.example.baksomanagement.data.model.PaymentRequest
import com.example.baksomanagement.data.model.PaymentResponse
import com.example.baksomanagement.data.model.SnapResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    //@POST("api/payment/create-qris")

    //fun createQris(

        //@Body request:PaymentRequest

    //):Call<PaymentResponse>
    @POST("api/snap/create")
    fun createSnap(

        @Body request: PaymentRequest

    ): Call<SnapResponse>
}