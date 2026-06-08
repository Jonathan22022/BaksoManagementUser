package com.example.baksomanagement.ui.favourite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.baksomanagement.R
import com.example.baksomanagement.data.repository.FavouriteRepository

class FavouriteFragment : Fragment() {

    companion object {
        private const val TAG = "FavouriteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d(TAG, "onCreateView() called")

        val view = inflater.inflate(
            R.layout.fragment_favourite,
            container,
            false
        )

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFavourite)
        val emptyLayout = view.findViewById<View>(R.id.layoutEmpty)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        Log.d(TAG, "RecyclerView initialized")

        val repository = FavouriteRepository()

        Log.d(TAG, "Requesting favourite list from repository...")

        repository.getFavouriteList { favouriteList ->

            Log.d(
                TAG,
                "Favourite callback received. Total items = ${favouriteList.size}"
            )

            favouriteList.forEachIndexed { index, favourite ->
                Log.d(
                    TAG,
                    "Item[$index] -> menuId=${favourite.menuId}"
                )
            }

            requireActivity().runOnUiThread {

                if (favouriteList.isEmpty()) {

                    Log.d(TAG, "Favourite list is EMPTY")

                    recycler.visibility = View.GONE
                    emptyLayout.visibility = View.VISIBLE

                } else {

                    Log.d(
                        TAG,
                        "Favourite list found. Showing RecyclerView"
                    )

                    recycler.visibility = View.VISIBLE
                    emptyLayout.visibility = View.GONE

                    recycler.adapter =
                        FavouriteAdapter(favouriteList) { favourite ->

                            Log.d(
                                TAG,
                                "Favourite clicked -> menuId=${favourite.menuId}"
                            )

                            val bundle = Bundle().apply {
                                putString(
                                    "MENU_ID",
                                    favourite.menuId
                                )
                            }

                            Log.d(
                                TAG,
                                "Navigating to DetailMenuFragment with MENU_ID=${favourite.menuId}"
                            )

                            findNavController().navigate(
                                R.id.action_favouriteFragment_to_detailMenuFragment,
                                bundle
                            )
                        }

                    Log.d(
                        TAG,
                        "Adapter attached. Item count = ${favouriteList.size}"
                    )
                }
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}