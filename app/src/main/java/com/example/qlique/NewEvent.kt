package com.example.qlique
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.ArrayList

class NewEvent : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_event)
        createEvent()

    }
    private fun createEvent(){
        var curUser =  "FTNv4hPQYgMz4ScpvBhUasCjm6B3"
        var mDatabase = FirebaseDatabase.getInstance().reference
        var photo = "https://www.soltlv.com/wp-content/uploads/2019/12/Sol_tlv-Yoga_Sculpt.jpg"
        var hobbies = ArrayList<String>(2)
        hobbies.add("Soccer")
        var event:Event =Event(photo,curUser,"Yoga tonight at 7:00 PM",hobbies)
        event.addMember( "FTNv4hPQYgMz4ScpvBhUasCjm6B3")
        event.addMember("VNFoQjNlV6NpncVKDczygbfxyGR2")
        mDatabase.child("/posts").push().setValue(event)
    }
}