package com.greenowl.instaclone.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.annotation.NonNull
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.greenowl.instaclone.Fragments.HomeFragment
import com.greenowl.instaclone.Fragments.ProfileFragment
import com.greenowl.instaclone.Model.User
import com.greenowl.instaclone.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.user_item_layout.view.*
import kotlin.coroutines.coroutineContext

class UserAdapter (private var mContext:Context,
                   private var mUser : List<User>,
                   private var isFragment:Boolean = false  ): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser?=FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent, false)
        return UserAdapter.ViewHolder(view)

    }

    override fun getItemCount(): Int {
       return mUser.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTextView.text = user.username
        holder.userFullnameTextView.text = user.fullname
        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfileImage)


        checkFollowingStatus(user.uid, holder.followButton)



        //TODO estudar SHARED PREFERENCE


        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileid",user.uid)
                pref.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container,ProfileFragment()).commit()

        })



    holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString()=="follow")
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid)
                        .setValue(true).addOnCompleteListener{task ->
                            if(task.isSuccessful){

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid)
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener{task ->
                                            if(task.isSuccessful)  {      }
                                        }
                                }
                            }
                    }
                }
            }
            else
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid)
                        .removeValue().addOnCompleteListener{task ->
                            if(task.isSuccessful){


                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid)
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener{task ->
                                            if(task.isSuccessful){   }
                                        }
                                }
                            }

                        }
                }
            }
        }

    }



    class ViewHolder (@NonNull itemView: View)  : RecyclerView.ViewHolder(itemView)
    {
     var userNameTextView: TextView = itemView.findViewById(R.id.user_name_search)
        var userFullnameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image)
        var followButton: Button = itemView.findViewById(R.id.follow_btn_search)

    }


    private fun checkFollowingStatus(uid: String, followButton: Button)
    {
       var followingRef =  firebaseUser?.uid.let { it1 ->
           FirebaseDatabase.getInstance().reference
               .child("Follow").child(it1.toString())
               .child("Following")

       }
        followingRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(uid).exists())
                {followButton.text="Following"}
                else
                {followButton.text="follow"}


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}