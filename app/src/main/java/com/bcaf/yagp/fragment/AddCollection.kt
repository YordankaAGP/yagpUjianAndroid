package com.bcaf.yagp.fragment

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import com.bcaf.yagp.R
import com.bcaf.yagp.service.api.APIConfig
import com.bcaf.yagp.service.model.CollectionItem
import com.bcaf.yagp.service.model.ResponseSuccess
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddCollection.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddCollection : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: CollectionItem? = null

    lateinit var inputNama : EditText
    lateinit var inputAlamat : EditText
    lateinit var inputOutstanding : EditText

    lateinit var txtJudul : TextView

    lateinit var btnSend : Button
    lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getParcelable(ARG_PARAM2,CollectionItem::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputNama = view.findViewById(R.id.inputNama)
        inputAlamat = view.findViewById(R.id.inputAlamat)
        inputOutstanding = view.findViewById(R.id.inputOutstanding)
        btnSend = view.findViewById(R.id.btnSend)
        progressBar = view.findViewById(R.id.progressBar2)
        txtJudul = view.findViewById(R.id.txtJudul)

        if(param1 == "add"){

            btnSend.setOnClickListener(View.OnClickListener {
                addCollection(CollectionItem(inputNama.text.toString(),
                    inputAlamat.text.toString(),
                    inputOutstanding.text.toString()))
            })

        }else{
            txtJudul.text = "Update Data"
            inputNama.setText(param2?.nama)
            inputAlamat.setText(param2?.alamat)
            inputOutstanding.setText(param2?.outstanding)
            btnSend.setOnClickListener {
                updateCollection(CollectionItem(inputNama.text.toString(),
                    inputAlamat.text.toString(),
                    inputOutstanding.text.toString(),
                    param2?.id))
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddCollection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: CollectionItem) =
            AddCollection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putParcelable(ARG_PARAM2, param2)
                }
            }
    }

    fun addCollection(data : CollectionItem){
        val client = APIConfig.getApiService()
            .createCollection(toRequestBody(data.nama.toString()),
                toRequestBody(data.alamat.toString()),
                toRequestBody(data.outstanding.toString())
            )

        showProgressBar(true)
        client.enqueue(object : Callback<ResponseSuccess> {
            override fun onResponse(
                call: Call<ResponseSuccess>,
                response: Response<ResponseSuccess>
            ) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Log.e("INFO", "onSuccess: ${responseBody.message}")
                    showProgressBar(false)
                    parentFragmentManager.popBackStackImmediate()
                }
            }

            override fun onFailure(call: Call<ResponseSuccess>, t: Throwable) {
                showProgressBar(false)
                Log.e("INFO", "onFailure: ${t.message.toString()}")
            }
        })


    }

    fun updateCollection(data : CollectionItem){
        val client = APIConfig.getApiService()
            .updateCollection(toRequestBody(data.id.toString()),toRequestBody(data.nama.toString()),
                toRequestBody(data.alamat.toString()),
                toRequestBody(data.outstanding.toString()))


        showProgressBar(true)
        client.enqueue(object : Callback<ResponseSuccess> {
            override fun onResponse(
                call: Call<ResponseSuccess>,
                response: Response<ResponseSuccess>
            ) {

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Log.e("INFO", "onSuccess: ${responseBody.message}")
                    showProgressBar(false)
                    parentFragmentManager.popBackStackImmediate()
                }
            }

            override fun onFailure(call: Call<ResponseSuccess>, t: Throwable) {
                showProgressBar(false)
                Log.e("INFO", "onFailure: ${t.message.toString()}")
            }
        })
    }


    fun toRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun showProgressBar(flag:Boolean){
        if (flag){
            progressBar.visibility = View.VISIBLE
            progressBar.animate()
        }else{
            progressBar.visibility = View.GONE

        }
    }
}