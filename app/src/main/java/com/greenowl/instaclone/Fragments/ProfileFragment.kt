package com.greenowl.instaclone.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greenowl.instaclone.AccountSettingsActivity
import com.greenowl.instaclone.Model.User
import com.greenowl.instaclone.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*




class ProfileFragment : Fragment() {


        private lateinit var profileId: String
        private lateinit var firebaseUser: FirebaseUser


    


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref!=null)
        {




            this.profileId= pref.getString("profileid","").toString()

        }
        if (profileId==firebaseUser.uid)
        {
            view.edit_account_settings_btn.text= "Edit Profile"
        }
        else if (profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtonStatus()
        }





        //Implementação do botão dentro do detalhe do perfil, botão Follow/Following

        view.edit_account_settings_btn.setOnClickListener{
            val getButtonText = view.edit_account_settings_btn.text.toString()


            when{
                getButtonText=="Edit Profile" -> startActivity(Intent(context,AccountSettingsActivity::class.java))


                getButtonText=="Follow" ->{
//                    firebaseUser?.uid.let { it1 ->
//                        FirebaseDatabase.getInstance().reference
//                            .child("Follow").child(it1.toString())
//                            .child("Following").child(profileId)
//                            .setValue(true)
//                    }
//                    firebaseUser?.uid.let { it1 ->
//                        FirebaseDatabase.getInstance().reference
//                            .child("Follow").child(profileId)
//                            .child("Followers").child(it1.toString())
//                            .setValue(true)
//                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .setValue(true).addOnCompleteListener{task ->
                                if(task.isSuccessful){

                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(profileId)
                                            .child("Followers").child(it1.toString())
                                            .setValue(true).addOnCompleteListener{task ->
                                                if(task.isSuccessful)  {
                                                    view?.edit_account_settings_btn?.text= "Following"




                                                }
                                            }
                                    }
                                }
                            }
                    }
                }



                getButtonText=="Following" ->{

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId)
                            .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .removeValue()
                    }

                    val followers = profileId.let{it1->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Followers")}

                    followers.addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot)
                        {
                            if (p0.exists())
                            {
                                view?.total_followers?.text = p0.childrenCount.toString()
                            }
                            else
                            {
                                view?.total_followers?.text = "0"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })









//                    firebaseUser?.uid.let { it1 ->
//                        FirebaseDatabase.getInstance().reference
//                            .child("Follow").child(it1.toString())
//                            .child("Following").child(profileId)
//                            .removeValue().addOnCompleteListener{task ->
//                                if(task.isSuccessful){
//
//
//                                    firebaseUser?.uid.let { it1 ->
//                                        FirebaseDatabase.getInstance().reference
//                                            .child("Follow").child(profileId)
//                                            .child("Followers").child(it1.toString())
//                                            .removeValue().addOnCompleteListener{task ->
//                                                if(task.isSuccessful){
//                                                    view?.edit_account_settings_btn?.text= "Follow"
//                                                }
//                                            }
//                                    }
//                                }
//
//                            }
//                    }

                }
            }



        }

        getFollowers()
        getFollowings()
        userInfo()


        return view

    }

    //TODO TENTANDO FAZER O BOTÃO FILTRO PARA DETALHES FUNCIONAR FOLLOW FOLLOWING





    private fun getFollowers()
    {
       // val followers = firebaseUser?.uid.let{it1->
        val followers = profileId.let{it1->
                FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Followers")}

        followers.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
            if (p0.exists())
            {
                view?.total_followers?.text = p0.childrenCount.toString()
            }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun getFollowings()
    {
        //val followersRef = firebaseUser?.uid.let{it1->
        val followersRef = profileId.let{it1->
                FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")}

        followersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot)
            {
                if (p0.exists())
                {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun userInfo()
    {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object :ValueEventListener
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
                    Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into (view?.pro_image_profile_frag)
                    view?.profile_fragment_username?.text = user!!.username
                    view?.full_name_profile_frag?.text = user!!.fullname
                    view?.bio_profile_pic?.text = user!!.bio

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            
        })

    }

// onStop, onPause, onDestroy usado para voltar para usuario logado quando sair do aplicativo
    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseUser.uid)
        pref?.apply()

    }
    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseUser.uid)
        pref?.apply()
    }
    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileid",firebaseUser.uid)
        pref?.apply()
    }




    private fun checkFollowAndFollowingButtonStatus() {
        var followingRef = profileId.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(firebaseUser.uid)
                .child("Following")
        }
        if ( followingRef!=null)
        {
            followingRef.child(profileId)
            followingRef.addValueEventListener(object : ValueEventListener{


                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.child(profileId).exists())
                    {
                        view?.edit_account_settings_btn?.text= "Following"
                    }
                    else
                    {
                        view?.edit_account_settings_btn?.text= "Follow"
                    }
                }

                override fun onCancelled(error: DatabaseError)
                {

                }
            })
        }

    }







}