package com.greenowl.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greenowl.instaclone.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*

class AccountSettingsActivity : AppCompatActivity() {


    //cria a variável firebase user
    private lateinit var firebaseUser: FirebaseUser
    private  var chekker=""
    //---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)



        //preenche a variável firebaseuser
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        //---


        Logout_btn_profile_frag.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, SigninActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

        close_profile_btn.setOnClickListener{

            }





        save_info_profile_btn.setOnClickListener{



           if (chekker == "clicked")
           {

           }
            else
           {
            updateUserInfoOnly()
           }


        }



        userInfo()
    }

    private fun updateUserInfoOnly()
    {
        when {
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> {
                Toast.makeText(this,"Full Name obrigatório",Toast.LENGTH_LONG).show()
            }
            username_profile_frag.text.toString().toLowerCase()=="" -> {
                Toast.makeText(this,"Username obrigatório",Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.text.toString().toLowerCase()=="" -> {
                Toast.makeText(this,"Bio obrigatório",Toast.LENGTH_LONG).show()
            }
            else -> {
                val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)



                val userMap = HashMap<String,Any>()
                userMap["fullname"]= full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"]= username_profile_frag.text.toString().toLowerCase()
                userMap["bio"]= bio_profile_frag.text.toString().toLowerCase()



                //usersRef.child(firebaseUser.uid).updateChildren(userMap).addOnCompleteListener{
                usersRef.updateChildren(userMap).addOnCompleteListener{
                    task ->


                    if(task.isSuccessful) {

                    Toast.makeText(this, "Account updated", Toast.LENGTH_LONG).show()
                }
                    else
                    {
                        Toast.makeText(this, "ERRO", Toast.LENGTH_LONG).show()
                    }

                }



                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)

                finish()
            }
        }




    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(p0: DataSnapshot)
            {


                /* if (context!= null)
                 {
                     return
                 }*/


                if (p0.exists())
                {
                    val user = p0.getValue<User>(User :: class.java)
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into (profile_image_view_profile_frag)
                    username_profile_frag.setText( user!!.username)
                    full_name_profile_frag.setText( user!!.fullname)
                    bio_profile_frag.setText( user!!.bio)

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}