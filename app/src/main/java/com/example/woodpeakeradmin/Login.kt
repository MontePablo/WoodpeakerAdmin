package com.example.woodpeakeradmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.woodpeakeradmin.Daos.FirebaseDao.auth
import com.example.woodpeakeradmin.Daos.UserDao
import com.example.woodpeakeradmin.databinding.ActivityLoginBinding
import com.example.woodpeakeradmin.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {
    lateinit var googleSignInClient: GoogleSignInClient
    lateinit var binding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.google.setOnClickListener(View.OnClickListener { googleSignIn() })
    }
    private fun googleSignIn() {
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        var signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 123)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 123) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken)
            } catch (e: ApiException) {
                Log.d("TAG", "onActivityResult EXEPTION : " + e.message)
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        allButtonsVisibility(View.INVISIBLE)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this,
                OnCompleteListener<AuthResult> { task ->
                    if (task.isSuccessful) {
                        Log.d("TAG", "signInWithCredential:success")
                        Log.d("TAG", "task.getResult().toString()=" + task.result.toString())
                        updateUI(auth.currentUser)
                    } else {
                        Log.d("TAG", "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                })
    }


    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){
            UserDao.getUser(firebaseUser!!.uid).addOnSuccessListener { document->
                if(document.exists()){
                    Toast.makeText(this,"welcome Back!", Toast.LENGTH_SHORT).show()
                }
                    startActivity(Intent(this, MainActivity::class.java))
                finish()
            }.addOnFailureListener { exception-> Log.d("TAG","updateUI:onFailure:"+exception.localizedMessage) }
        }
    }

    fun allButtonsVisibility(visib: Int) {
        binding.google.visibility=visib

        binding.progressBar.visibility=when(visib== View.INVISIBLE){ true-> View.VISIBLE false-> View.INVISIBLE}
    }

}