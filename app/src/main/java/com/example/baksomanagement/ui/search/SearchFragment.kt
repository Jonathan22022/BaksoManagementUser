package com.example.baksomanagement.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.model.Menu
import com.example.baksomanagement.data.repository.MenuRepository
import android.view.inputmethod.EditorInfo

class SearchFragment : Fragment() {

    private lateinit var etSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutEmpty: LinearLayout
    private val repository = MenuRepository()
    private lateinit var adapter: SearchAdapter
    private var allMenu = listOf<Menu>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etSearch = view.findViewById(R.id.etSearch)
        recyclerView = view.findViewById(R.id.recyclerViewSearchResults)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)

        adapter = SearchAdapter(emptyList())

        recyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        recyclerView.adapter = adapter

        repository.getMenuList { menus ->

            allMenu = menus

            Log.d("SEARCH", "Jumlah menu = ${menus.size}")
            menus.forEach {
                Log.d("SEARCH", it.namaMenu)
            }

            adapter.updateData(menus)

            layoutEmpty.visibility =
                if (menus.isEmpty()) View.VISIBLE
                else View.GONE
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                etSearch.clearFocus()
                true
            } else {
                false
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                val keyword = s.toString()

                val filtered = allMenu.filter {
                    it.namaMenu.contains(
                        keyword,
                        ignoreCase = true
                    )
                }

                Log.d("SEARCH", "Keyword = $keyword")
                Log.d("SEARCH", "Hasil = ${filtered.size}")

                adapter.updateData(filtered)

                layoutEmpty.visibility =
                    if (filtered.isEmpty()) View.VISIBLE
                    else View.GONE
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}