package com.motive.degismedendegisin.fragment
//DEVELOPED BY KAAN KAPLAN
//Unauthorized use, distribution and reproduction for any reason is prohibited.

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.helpcrunch.library.core.Callback
import com.helpcrunch.library.core.HelpCrunch
import com.helpcrunch.library.core.options.HCOptions
import com.motive.degismedendegisin.DegismedenDegisin
import com.motive.degismedendegisin.R


class BildirimlerFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_bildirimler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}