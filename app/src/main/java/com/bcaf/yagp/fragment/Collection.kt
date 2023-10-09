package com.bcaf.yagp.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bcaf.yagp.R
import com.bcaf.yagp.adapter.CollectionAdapter
import com.bcaf.yagp.service.api.APIConfig
import com.bcaf.yagp.service.model.CollectionItem
import com.bcaf.yagp.service.model.ResponseGetAllData
import com.bcaf.yagp.service.model.ResponseSuccess
import com.google.android.material.floatingactionbutton.FloatingActionButton
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.floor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Collection.newInstance] factory method to
 * create an instance of this fragment.
 */
class Collection : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var collectionAdapter : CollectionAdapter
    lateinit var fabAddData: FloatingActionButton
    lateinit var progressBar : ProgressBar
    lateinit var searchBar : EditText
    lateinit var swipeRefreshLayout : SwipeRefreshLayout
    lateinit var loadMoreBtn : Button

    var fullData : List<CollectionItem> = emptyList()
    var collectionData : MutableList<CollectionItem> = arrayListOf()

    var cursor =  0;
    var maxCursor = 0;
    val perPage = 4;

    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable = Runnable { /* Initial value */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.collectionList)
        progressBar = view.findViewById(R.id.progressBar)
        fabAddData = view.findViewById(R.id.fabAdd)
        searchBar = view.findViewById(R.id.searchBar)
        loadMoreBtn = view.findViewById(R.id.loadMoreBtn)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            searchBar.setText("")
            getCollections()
        }

        loadMoreBtn.setOnClickListener(View.OnClickListener {
            cursor += 1
            updateShowedData()
            Log.i("test", collectionData.toString())
            collectionAdapter.setCollection(collectionData)
        })

        fabAddData.setOnClickListener(View.OnClickListener {
            parentFragmentManager.beginTransaction()
                .addToBackStack("add form")
                .replace(R.id.fragmentRoot, AddCollection.newInstance("add", CollectionItem()))
                .commit()

        })

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.i("asd", "masooook")
                // Remove any previously scheduled callbacks
                handler.removeCallbacks(runnable)

                // Delay the execution of the callback by 500 milliseconds
                runnable = Runnable {
                    if (s.toString().length == 0) {
                        getCollections()
                    } else {
                        getFilteredTodoList(s.toString())
                    }
                }
                handler.postDelayed(runnable, 700) // 500 milliseconds delay (adjust as needed)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        getCollections()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Collection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Collection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onResume() {
        super.onResume()
        getCollections()
    }

    fun toRequestBody(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun updateShowedData() {
        collectionData = arrayListOf()
        run breaking@ {
            fullData.forEachIndexed { idx, d ->
                Log.i("ingpo", d.toString())
                Log.i("cursor", cursor.toString())
                if ((idx/perPage) > cursor) return@breaking
                collectionData.add(d)
            }
        }

        if(collectionData.size > 0 && cursor < maxCursor) {
            loadMoreBtn.visibility = View.VISIBLE
        } else {
            loadMoreBtn.visibility = View.GONE
        }
    }

    private fun onContentLoaded(): Callback<ResponseGetAllData> {
        return object: Callback<ResponseGetAllData> {
            override fun onResponse(
                call: Call<ResponseGetAllData>,
                response: Response<ResponseGetAllData>
            ) {
                showProgressBar(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Log.e("INFO", "onSuccess: ${responseBody.data?.collection}")
                    fullData = responseBody.data?.collection as List<CollectionItem>
                    maxCursor = fullData.size / perPage
                    updateShowedData()

                    collectionAdapter = CollectionAdapter(collectionData, {item ->
                        parentFragmentManager.beginTransaction()
                            .addToBackStack("add form")
                            .replace(R.id.fragmentRoot, AddCollection.newInstance("update", item))
                            .commit()
                    } , { item ->
                        deleteCollection(item)
                    })
                    recyclerView.layoutManager = LinearLayoutManager(context)
                    recyclerView.adapter = collectionAdapter
                }
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<ResponseGetAllData>, t: Throwable) {
                showProgressBar(false)
                Log.e("INFO", "onFailure: ${t.message.toString()}")
                swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    fun getFilteredTodoList(query: String) {
        val filterField = "nama"
        val filterOperator = "like"
        val sortOrder = "ASC"

        collectionAdapter.setCollection(emptyList())
        val client = APIConfig.getApiService().getCollectionsByFilter(filterField, filterOperator, query, sortOrder)

        showProgressBar(true)
        client.enqueue(onContentLoaded())
    }

    fun getCollections(){
        val client = APIConfig.getApiService().getCollections()
        showProgressBar(true)

        client.enqueue(onContentLoaded())
    }

    fun deleteCollection(data : CollectionItem){
        val client = APIConfig.getApiService()
            .deleteCollection(toRequestBody(data.id.toString()))
        showProgressBar(true)

        client.enqueue(object : Callback<ResponseSuccess> {
            override fun onResponse(
                call: Call<ResponseSuccess>,
                response: Response<ResponseSuccess>
            ) {
                showProgressBar(false)

                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    Log.e("INFO", "onSuccess: ${responseBody.message}")

                    getCollections()
                }
            }

            override fun onFailure(call: Call<ResponseSuccess>, t: Throwable) {
                showProgressBar(false)

                Log.e("INFO", "onFailure: ${t.message.toString()}")
            }
        })
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