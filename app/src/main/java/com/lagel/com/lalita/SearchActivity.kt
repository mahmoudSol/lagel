package com.lagel.com.lalita

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.lagel.com.R
import com.lagel.com.lalita.adapters.SearchAdapter
import com.lagel.com.lalita.adapters.SuggestionsAdapter
import com.lagel.com.lalita.utils.ObjectSerializer
import com.lagel.com.pojo_class.home_explore_pojo.ExploreResponseDatas
import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas
import com.lagel.com.utility.VariableConstants
import kotlinx.android.synthetic.main.activity_search.*
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Lalita Gill on 11/03/18.
 */
class SearchActivity : AppCompatActivity() {
    private var t1: TextToSpeech? = null
    private val REQ_CODE_SPEECH_INPUT = 100
    private var recentSearch = ArrayList<String>()
    var adapter: SearchAdapter? = null
    var adapterSuggestion: SuggestionsAdapter? = null
    private var searchList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val myList = intent.getParcelableArrayListExtra<ExploreResponseDatas>("LIST")

        for (i in 0 until myList.size) {
            //  var data= ExploreResponseDatas()
            searchList.add(myList.get(i).productName)
            searchList.add(myList[i].category)
        }


        searchList.add("art")
        searchList.add("auto parts")
        searchList.add("baby & child")
        searchList.add("bicycles")
        searchList.add("boats & marine")
        searchList.add("books & magazines")
        searchList.add("cars & trucks")
        searchList.add("catering")
        searchList.add("cds & dvds")
        searchList.add("cell phones")
        searchList.add("cosmetics")
        searchList.add("electronics")
        searchList.add("electronics repair")
        searchList.add("events")
        searchList.add("fashion & accessories")
        searchList.add("food & dining")
        searchList.add("food product")
        searchList.add("free stuff")
        searchList.add("health")
        searchList.add("home and garden")
        searchList.add("home services")
        searchList.add("home and garden")
        searchList.add("house for rent")
        searchList.add("house for sale")
        searchList.add("jewelry & accessories")
        searchList.add("lessons & tutorials")
        searchList.add("mechanical")
        searchList.add("motorcycles")
        searchList.add("movies & musics")
        searchList.add("musical instruments")
        searchList.add("painting & decorating")
        searchList.add("photography")
        searchList.add("professional services")
        searchList.add("tickets")
        searchList.add("tv")
        searchList.add("video games")




        imageView_mic.tag = "MIC"
        // load tasks from preference
        val prefs = getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE)
        try {
            recentSearch = ObjectSerializer.deserialize(prefs.getString("LIST", ObjectSerializer.serialize(ArrayList<String>()))) as ArrayList<String>
        } catch (e: IOException) {
            e.printStackTrace()
        }

        if (recentSearch.size == 0) {
            relativeLayoutRecent.visibility = View.GONE
        }


        imageView_back.setOnClickListener({ finish() })
        imageView_mic.setOnClickListener({

            //            Log.e("ID", "" + imageView_mic.tag)

            try {
                val resourceID = imageView_mic.tag as String
                if (resourceID == "MIC") {
                    promptSpeechInput()
                }
            } catch (e: ClassCastException) {
                edit_search.setText("")
            }

        })
        textViewEdit.setOnClickListener({
            adapter?.showCrossIcon(true)
            textViewEdit.visibility = View.GONE
            linearLayoutDelete.visibility = View.VISIBLE
        })
        textViewCancel.setOnClickListener({

            adapter?.showCrossIcon(false)
            textViewEdit.visibility = View.VISIBLE
            linearLayoutDelete.visibility = View.GONE
        })

        textViewDeleteAll.setOnClickListener({
            val dialog = AlertDialog.Builder(this)
                    .setTitle("Delete recent searches?")
                    .setMessage("This will clear your search history.")
                    .setCancelable(false)
                    .setPositiveButton("Delete") { dialog, _ ->
                        dialog.dismiss()
                        recentSearch.clear()
                        adapter?.notifyDataSetChanged()
                        val prefs = getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()
                        relativeLayoutRecent.visibility = View.GONE

                    }
                    .setNegativeButton("Cancel", null)
                    .create()
            dialog.show()
        })

        t1 = TextToSpeech(applicationContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                t1?.language = Locale.US

            }
        })


        edit_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

                if (charSequence.isNotEmpty()) {
                    fillter(charSequence.toString().toLowerCase())
                    imageView_mic.setImageResource(R.drawable.ic_clear_black_24dp)
                    imageView_mic.tag = R.drawable.ic_clear_black_24dp
                    textSuggestions.visibility = View.VISIBLE
                    recyclerViewSuggestions.visibility = View.VISIBLE
                    view_search.visibility = View.VISIBLE

                } else {
                    imageView_mic.setImageResource(R.drawable.ic_mic_black_24dp)
                    imageView_mic.tag = R.drawable.ic_mic_black_24dp
                    textSuggestions.visibility = View.GONE
                    recyclerViewSuggestions.visibility = View.GONE
                    view_search.visibility = View.GONE
                }
            }

            override fun afterTextChanged(editable: Editable) {


            }
        })




        setRecentResults()
        setSearchResults()


        edit_search?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                edit_search.clearFocus()
                val `in` = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                `in`!!.hideSoftInputFromWindow(edit_search.windowToken, 0)


                var arrayListDate = java.util.ArrayList<String>()
                // load tasks from preference
                val prefs = getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE)

                try {
                    arrayListDate = ObjectSerializer.deserialize(prefs.getString("LIST", ObjectSerializer.serialize(java.util.ArrayList<String>()))) as java.util.ArrayList<String>
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                arrayListDate.add(edit_search.text.toString())

                val editor = prefs.edit()
                try {
                    editor.putString("LIST", ObjectSerializer.serialize(arrayListDate))
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                editor.commit()

                val datas = ProductCategoryResDatas()
                datas.isSelected = true
                datas.name = edit_search.text.toString()

                val arrayList = java.util.ArrayList<ProductCategoryResDatas>()
                arrayList.add(datas)


                val filterIntent = Intent()
                setResult(VariableConstants.FILTER_REQUEST_SEARCH, filterIntent)
                filterIntent.putExtra("aL_categoryDatas", arrayList)
                filterIntent.putExtra("distance", "")
                filterIntent.putExtra("currentLatitude", "")
                filterIntent.putExtra("currentLongitude", "")
                filterIntent.putExtra("address", "")
                filterIntent.putExtra("sortBy", "")
                filterIntent.putExtra("postedWithin", "")
                filterIntent.putExtra("minPrice", "")
                filterIntent.putExtra("maxPrice", "")
                filterIntent.putExtra("currency_code", "")
                filterIntent.putExtra("currency", "")
                filterIntent.putExtra("postedWithinText", "")
                filterIntent.putExtra("sortByText", "")

                setResult(VariableConstants.FILTER_REQUEST_SEARCH, filterIntent)
                finish()

                return@OnEditorActionListener true
            }
            false
        })

    }


    fun fillter(text: String) {
        var temp = ArrayList<String>()

        for (i in 0 until searchList.size) {
            //  Log.e("Position", "" + i)
            if (searchList[i].contains(text)) {
                temp.add(searchList[i])
                //  adapterSuggestion?.updateList(temp)
            }
        }
        var handler = Handler()
        handler.postDelayed({
            adapterSuggestion?.updateList(temp)
            handler.removeCallbacksAndMessages(null)
        }, 500)


    }

    /**
     * Setting Recent search Recycler view
     */
    private fun setRecentResults() {
        recyclerViewSearch.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        // recyclerViewSearch.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        adapter = SearchAdapter(this, recentSearch)
        recyclerViewSearch.adapter = adapter
    }

    /**
     * Setting Recent search Recycler view
     */
    private fun setSearchResults() {
        recyclerViewSuggestions.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        // recyclerViewSearch.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        adapterSuggestion = SuggestionsAdapter(this, searchList)
        recyclerViewSuggestions.adapter = adapterSuggestion
    }

    /**
     * Showing google speech input dialog
     */
    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt))
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
            Toast.makeText(applicationContext,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show()
        }

    }

    /**
     * Receiving speech input
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    Log.e("Result", result[0])

                    recentSearch.add(result[0])

                    val prefs = getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    try {
                        editor.putString("LIST", ObjectSerializer.serialize(recentSearch))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    editor.commit()
                    adapter?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBackPressed() {
        val aL_categoryDatas = java.util.ArrayList<ProductCategoryResDatas>()
        val filterIntent = Intent()
        setResult(VariableConstants.FILTER_REQUEST_SEARCH, filterIntent)
        filterIntent.putExtra("aL_categoryDatas", aL_categoryDatas)
        filterIntent.putExtra("distance", "")
        filterIntent.putExtra("currentLatitude", "")
        filterIntent.putExtra("currentLongitude", "")
        filterIntent.putExtra("address", "")
        filterIntent.putExtra("sortBy", "")
        filterIntent.putExtra("postedWithin", "")
        filterIntent.putExtra("minPrice", "")
        filterIntent.putExtra("maxPrice", "")
        filterIntent.putExtra("currency_code", "")
        filterIntent.putExtra("currency", "")
        filterIntent.putExtra("postedWithinText", "")
        filterIntent.putExtra("sortByText", "")

        setResult(VariableConstants.FILTER_REQUEST_SEARCH, filterIntent)
        finish()
    }
}
