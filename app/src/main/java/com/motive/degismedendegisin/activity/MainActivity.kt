package com.motive.degismedendegisin.activity

//DEVELOPED BY KAAN KAPLAN
//Unauthorized use, distribution and reproduction for any reason is prohibited.

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.helpcrunch.library.core.HelpCrunch
import com.helpcrunch.library.core.models.user.HCUser
import com.helpcrunch.library.core.options.design.*
import com.motive.degismedendegisin.R
import com.motive.degismedendegisin.fragment.*
import com.motive.degismedendegisin.model.Users
import com.motive.degismedendegisin.utils.EventBusDataEvents
import com.motive.degismedendegisin.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header.*
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView : NavigationView
    lateinit var mAuth : FirebaseAuth
    lateinit var circle : CircleImageView
    lateinit var headerisim : TextView
    lateinit var progressHeader : ProgressBar
    lateinit var db  : FirebaseFirestore
    lateinit var mAuthListener : FirebaseAuth.AuthStateListener
    var ameilatOlmusMu = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupAuthListener()
        initImageLoader()
        fcmTokenKaydet()

        mAuth = FirebaseAuth.getInstance()
        db = Firebase.firestore
        drawerLayout = findViewById(R.id.drawerlayout)
        navigationView = findViewById(R.id.nav_View)
        val user_id = mAuth.currentUser!!.uid
        var user_name = ""
        var user_surname = ""
        var user_phone = ""

        db.collection("ameliyatistegi").document(mAuth.currentUser!!.uid).get().addOnSuccessListener {
            if (it.get("ameliyatOlmusMu")!!.equals("Evet")){
                setCurrentFragment(AmeliyatOldumFragment())
                ameilatOlmusMu = true
            }else{
                setCurrentFragment(InfoFragment())
                ameilatOlmusMu = false
            }
        }.addOnFailureListener {
            setCurrentFragment(InfoFragment())
            Log.e("AmeliyatMain",it.message.toString())
        }

        var toolbar = toolbar

        setSupportActionBar(toolbar)
        navigationView.bringToFront()
        val header = navigationView.getHeaderView(0)
        circle = header.findViewById<CircleImageView>(R.id.circlepp)
        headerisim = header.findViewById(R.id.tv_navview_name)
        progressHeader = header.findViewById(R.id.header_progress)


        db.collection("users").document(user_id).addSnapshotListener { snapshot, e ->
            if (e != null){
                Toast.makeText(
                    this@MainActivity,
                    "Lütfen internet bağlantınızı kontrol edin.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("HATA", "listen failed ", e)
            }else{
                val user = snapshot!!.toObject(Users::class.java)
                user_name = user!!.name!!
                user_surname = user!!.surname!!
                user_phone = user!!.phone_number!!
                UniversalImageLoader.setImage(user!!.profilePic!!, circle, progressHeader, "")
                EventBus.getDefault().postSticky(EventBusDataEvents.KullaniciBilgileriniGonder(user))


                var hcUser = HCUser.Builder().withUserId(user_id).withName(user_name + " " + user_surname).withPhone(
                    user_phone
                ).build()

                HelpCrunch.updateUser(hcUser)


                headerisim.text = user_name + " " + user_surname


            }
        }

        var toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle.syncState()


        navigationView.setNavigationItemSelectedListener(this)


    }

    private fun initImageLoader(){
        var universalImageLoader = UniversalImageLoader(applicationContext)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    private fun setupAuthListener() {
        mAuthListener = object : FirebaseAuth.AuthStateListener {
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user = FirebaseAuth.getInstance().currentUser
                if (user == null) {
                    startActivity(
                        Intent(this@MainActivity, LoginRegisterActivity::class.java).addFlags(
                            Intent.FLAG_ACTIVITY_NO_ANIMATION
                        )
                    )
                        finish()
                }else{

                }
            }

        }
    }



    private fun setCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragment, fragment)
            commit()
        }

    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            super.onBackPressed()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.navProfile -> {
                setCurrentFragment(ProfileFragment())
            }
            R.id.navMesajlar -> {
                setCurrentFragment(MesajlarFragment())
            }
            R.id.navDoktorum -> {
                setCurrentFragment(DoktorumFragment())
            }
            R.id.navTakvim -> {
                setCurrentFragment(TakvimFragment())
            }
            R.id.cıkıs -> {
                var dialog = SignOutFragment()
                dialog.show(supportFragmentManager, "cikisyapdialoggöster")
            }
            R.id.anasayfa -> {
                if (ameilatOlmusMu == false){
                    setCurrentFragment(InfoFragment())
                }else{
                    setCurrentFragment(AmeliyatOldumFragment())
                }

            }
        }
        return true;
    }

    private fun fcmTokenKaydet(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            yeniTokenVeritabanınaKaydet(token)

        })
    }

    private fun yeniTokenVeritabanınaKaydet(yeniToken : String) {
        if (FirebaseAuth.getInstance().currentUser != null){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).update("fcm_token",yeniToken).addOnSuccessListener {

            }.addOnFailureListener {
                Log.e("FCM",it.message.toString())
            }

        }
    }

}