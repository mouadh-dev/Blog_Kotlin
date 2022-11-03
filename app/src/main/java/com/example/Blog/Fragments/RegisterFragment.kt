package com.example.Blog.Fragments

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Log.println
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.Blog.Dao.SignUpCallback
import com.example.Blog.Dao.UserDao
import com.example.Blog.Entity.UserItem
import com.example.Blog.R
import com.example.Blog.databinding.FragmentRegisterBinding
import com.example.Blog.databinding.FragmentRegisterBinding.inflate
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.content.Context
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private var fullName: EditText? = null
    private var mail: EditText? = null
    private var password: EditText? = null
    private var confirmPassword: EditText? = null
    private var uri: Uri? = null
    private var mContext: Context? = null
    private val mAuth = FirebaseAuth.getInstance()






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = inflate(inflater, container, false)

        var view = binding.root
        initView(view)
        return binding.root

    }

    private fun initView(view: View) {

        fullName = binding.nameUserInscription
        mail = binding.mailUserInscription
        password = binding.PasswordUserInscription
        confirmPassword = binding.InscriptionConfirmPassword
        val userDao = UserDao()
        mContext = requireContext()


        binding.backToLogin.setOnClickListener {
            val loginFragment = LoginFragment()
            requireFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, loginFragment).commit()
        }

        binding.registerPicture.setOnClickListener {
            println(Log.ASSERT,"selected","showing selected photo")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent, 0    )
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == AppCompatActivity.RESULT_OK && data != null){
            Log.d("Register activity", "Photo was selected successfully")
            uri = data.data
            val resolver = requireActivity().contentResolver
            val picture = MediaStore.Images.Media.getBitmap(resolver,uri)
            val pictureDrawable = BitmapDrawable(picture)
            binding.registerPicture.setBackgroundDrawable(pictureDrawable)
        }



        /////sign up function
        val userDao = UserDao()
        val user = UserItem()
        binding.signUpButton.setOnClickListener {
            if (validateInput()) {
                user.fullname = fullName!!.text.toString()
                user.mail = mail!!.text.toString()
                user.password = password!!.text.toString()
                user.confirmpassword = confirmPassword!!.text.toString()

                ////////////////////////////////DIALOG///////////////////////////////
                val v = View.inflate(mContext, R.layout.progress_dialog, null)
                val builder = AlertDialog.Builder(mContext!!)
                builder.setView(v)

                val progressdialog = builder.create()
                progressdialog.show()
                progressdialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                progressdialog.setCancelable(false)

                val loginFragment = LoginFragment()
                userDao.signUpUser(requireActivity() as AppCompatActivity,user,object:SignUpCallback{
                    override fun success() {
                        userDao.uploadImageToFirebase(mAuth.currentUser!!.uid,uri!!)
                        progressdialog.dismiss()
                        requireFragmentManager().beginTransaction()
                            .replace(R.id.frameLayout, loginFragment).commit()
                    }
                    override fun failure(error: String) {
                        progressdialog.dismiss()
                        val duration = Toast.LENGTH_SHORT

                        val toast = Toast.makeText(context, "oops something went wrong!! please check your information", duration)
                        toast.show()
                    }
                })


            }
        }

    }

    private fun dialog() {

    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateInput(): Boolean {
        /////////PICTURE
        //if(binding.registerPicture.background === null){
          //  binding.registerPicture.error = "Please pick a picture"
            //return false
        //}
        /////////FULL NAME
        if (fullName!!.text.toString() == "") {
            fullName!!.error = "Please Enter Name"
            return false
        }
        /////////EMAIL
        if (mail!!.text.toString() == "") {
            mail!!.error = "Please Enter mail"
            return false
        }
        if (!isEmailValid(mail!!.text.toString())) {
            mail!!.error = "Please Enter Valid Email"
            return false
        }
        /////////PASSWORD
        if (password!!.text.toString() == "") {
            password!!.error = "Please Enter password"
            return false
        }
        if (password!!.text.length < 6) {
            password!!.error = "Password Length must be more than " + 6 + "characters"
            return false
        }
        if (confirmPassword!!.text.toString() == "") {
            confirmPassword!!.error = "Please Enter password"
            return false
        }
        if (password!!.text.toString() != confirmPassword!!.text.toString()) {
            confirmPassword!!.error = "Password does not match"
            return false
        }
        return true
    }
}