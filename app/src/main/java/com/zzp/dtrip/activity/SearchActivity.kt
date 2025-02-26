package com.zzp.dtrip.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zzp.dtrip.R
import com.zzp.dtrip.adapter.AddressAdapter
import com.zzp.dtrip.data.Data
import com.zzp.dtrip.data.SuggestionResult
import com.zzp.dtrip.fragment.TripFragment
import com.zzp.dtrip.util.TencentAppService
import com.zzp.dtrip.util.TencentRetrofitManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    companion object {
        val resultList = ArrayList<Data>()
    }

    private lateinit var searchEdit: EditText

    private lateinit var searchButton: Button

    private lateinit var recyclerView: RecyclerView

    private var adapter: AddressAdapter? = null

    private var keyword = ""

    private val KEY = "F5IBZ-US3CW-3JIRY-OKBB5-TUMWV-S7BVZ"

    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        findViewById()
        initRecyclerView()
        initEdit()
        searchButton.setOnClickListener {
            keyword = searchEdit.text.toString()
            if (keyword.trim().isEmpty()) {
                Toast.makeText(this, "输入为空",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                getSuggestion()
            }
        }
    }

    private fun findViewById() {
        searchEdit = findViewById(R.id.search_edit)
        searchButton = findViewById(R.id.search_button)
        recyclerView = findViewById(R.id.address_recycler)
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = AddressAdapter(this, resultList)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun initEdit() {
        if(TripFragment.postion != -1) {
            keyword = resultList[TripFragment.postion].title
            searchEdit.setText(keyword)
            getSuggestion()
        }

    }

    private fun getSuggestion() {
        val appService = TencentRetrofitManager.create<TencentAppService>()
        val task = appService.getSuggestion(keyword, TripFragment.city, KEY)
        Log.d(TAG, "getSuggestion: " + TripFragment.city)
        task.enqueue(object : Callback<SuggestionResult>{
            override fun onResponse(call: Call<SuggestionResult>,
                                    response: Response<SuggestionResult>) {
                response.body()?.apply {
                    if (this.status == 0) {
                        if (resultList.size != 0) {
                            resultList.clear()
                        }
                        for (obj in this.data) {
                            resultList.add(obj)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    else {
                        Toast.makeText(this@SearchActivity, "请求错误",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SuggestionResult>, t: Throwable) {
                Log.d(TAG, "onFailure ==> $t")
            }
        })
    }
}