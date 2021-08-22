package com.motive.degismedendegisin.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.helpcrunch.library.core.HelpCrunch
import com.motive.degismedendegisin.R
import kotlinx.android.synthetic.main.fragment_mesajlar.*


class MesajlarFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mesajlar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatButton.setOnClickListener {
            HelpCrunch.showChatScreen()

        }
    }

}