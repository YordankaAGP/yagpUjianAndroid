package com.bcaf.yagp.service.api

import com.bcaf.yagp.service.model.ResponseGetAllData
import com.bcaf.yagp.service.model.ResponseSuccess
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface APIService {
    @GET("collection/all")
    fun getCollections(): Call<ResponseGetAllData>

    @Multipart
    @POST("collection/add")
    fun createCollection(@Part("nama") tugas: RequestBody, @Part("alamat")
    detail: RequestBody, @Part("outstanding") status: RequestBody) : Call<ResponseSuccess>

    @Multipart
    @POST("collection/update")
    fun updateCollection(
        @Part("id") id: RequestBody, @Part("nama") tugas: RequestBody, @Part("alamat")
        detail: RequestBody, @Part("outstanding") status: RequestBody
    ) : Call<ResponseSuccess>


    @Multipart
    @POST("collection/delete")
    fun deleteCollection(@Part("id") id: RequestBody) : Call<ResponseSuccess>

    @GET("collection/all")
    fun getCollectionsByFilter(@Query("filters[0][co][0][fl]") filterField : String,
                           @Query("filters[0][co][0][op]") filterOperator : String,
                           @Query("filters[0][co][0][vl]") filterValue : String,
                           @Query("sort_order") sortorder : String

    ) : Call<ResponseGetAllData>
}