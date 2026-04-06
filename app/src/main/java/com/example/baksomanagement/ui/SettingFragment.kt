package com.example.baksomanagement.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.baksomanagement.R

class SettingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnAccount).setOnClickListener {

        }

        view.findViewById<View>(R.id.btnNotification).setOnClickListener {

        }

        view.findViewById<View>(R.id.btnLanguage).setOnClickListener {

        }
    }
}