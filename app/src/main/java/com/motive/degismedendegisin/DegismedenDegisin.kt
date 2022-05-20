package com.motive.degismedendegisin

import android.app.Application
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.helpcrunch.library.core.HelpCrunch
import com.helpcrunch.library.core.models.user.HCUser
import com.helpcrunch.library.core.options.HCOptions
import com.helpcrunch.library.core.options.design.HCAvatarTheme
import com.helpcrunch.library.core.options.design.HCChatAreaTheme
import com.helpcrunch.library.core.options.design.HCTheme
import com.helpcrunch.library.core.options.design.HCToolbarAreaTheme
import com.motive.degismedendegisin.activity.LoginRegisterActivity
import com.motive.degismedendegisin.model.Users

class DegismedenDegisin : Application() {

    override fun onCreate() {
        super.onCreate()
        var avatarTheme = HCAvatarTheme.Builder().setPlaceholderBackgroundColor(R.color.tite).setPlaceholderTextColor(R.color.white).build()
        var chatAreaThemE = HCChatAreaTheme.Builder().setAvatarTheme(avatarTheme).setIncomingBubbleColor(R.color.outcomingBubble).setOutcomingBubbleColor(R.color.incomingBubble).build()
        var toolBarAreaTheme = HCToolbarAreaTheme.Builder().setBackgroundColor(R.color.splashbackround).setStatusBarColor(R.color.splashbackround).setStatusBarLight(false).setAgentsTextColor(R.color.white)
            .build()
        var theme = HCTheme.Builder().setChatAreaTheme(chatAreaThemE).setToolbarAreaTheme(toolBarAreaTheme).build()
        var options = HCOptions.Builder().setTheme(theme).build()
        var hcUser = HCUser.Builder().withName("Misafir").build()

        HelpCrunch.initialize("degismedendegisin",
            2,TOKEN,hcUser,options)



    }


}
