package com.example.qlique

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.qlique.Chat.ChatListActivity
import com.example.qlique.LoginAndSignUp.LoginActivity
import com.example.qlique.LoginAndSignUp.UpdatePassword
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var logoutBtn: Button
    private lateinit var updatePass: Button
    private lateinit var chatBtn :Button
    private lateinit var profileBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }
        setContentView(R.layout.activity_main)
        profileBtn = findViewById(R.id.profile)
        logoutBtn = findViewById(R.id.logout_btn)
        updatePass = findViewById(R.id.update_pass_btn)
        chatBtn = findViewById(R.id.btn_chat_feed)
        // when clicking the logout we will return to the login activity.
        logoutBtn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        chatBtn.setOnClickListener{
            val intent = Intent(this, ChatListActivity::class.java)
            Toast.makeText(this, "chat", Toast.LENGTH_LONG)
                .show()

            startActivity(intent)
            finish()
        }
        // when clicking the change password we will enter the change password activity.
        updatePass.setOnClickListener{
            val intent = Intent(this, UpdatePassword::class.java)
            startActivity(intent)
        }
        profileBtn.setOnClickListener{
            val intent = Intent(this, ProfilePage::class.java)
            intent.putExtra("EXTRA_SESSION_ID", FirebaseAuth.getInstance().uid);
            startActivity(intent)
        }
    }

}