package com.example.managementuikit.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.managementuikit.Activity.SplashActivity.Companion.myRefUser
import com.example.managementuikit.Model.User
import com.example.managementuikit.databinding.ActivitySignInBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    @Suppress("DEPRECATION")
    @SuppressLint("PackageManagerGetSignatures")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences= getSharedPreferences("SHARED_PREFERENCES_USER", MODE_PRIVATE)
        callbackManager = CallbackManager.Factory.create()
        setButtonOnClick()
        try {
            val info = packageManager.getPackageInfo(
                "com.example.managementuikit",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            }
        } catch (_: PackageManager.NameNotFoundException) {
        } catch (_: NoSuchAlgorithmException) {
        }
    }

    private fun setButtonOnClick() {
        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.signInWithFacebookButton.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
        loginWithGoogle()
        loginWithFacebook()
    }

    private fun loginWithGoogle() {
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("8978968882-fmjhj7s0ams6puvdo19a41noup85i8i6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.signInWithGoogleButton.setOnClickListener {
            signInGoogle()
        }
    }

    @Suppress("DEPRECATION")
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun loginWithFacebook() {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Toast.makeText(this@SignInActivity, "Login Successfully", Toast.LENGTH_LONG).show()
                getUserProfile(result.accessToken, result.accessToken.userId)
            }
            override fun onCancel() {
                Toast.makeText(this@SignInActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }
            override fun onError(error: FacebookException) {
                Toast.makeText(this@SignInActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        binding.progressBarLayout.visibility = View.VISIBLE
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (_: ApiException) {
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val editor = sharedPreferences.edit()
                editor.putString("name", user!!.displayName)
                editor.putString("avatar", user.photoUrl.toString())
                editor.apply()
                SplashActivity.auth = auth
                val newUser = User(user.photoUrl.toString(),SplashActivity.auth.uid.toString() , user.displayName.toString())
                myRefUser.child("${SplashActivity.auth.uid}").setValue(newUser)
                startHomeActivity()
                Toast.makeText(this@SignInActivity, "Login With Google Successfully", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@SignInActivity, "Login With Google Failed", Toast.LENGTH_LONG).show()
            }
            binding.progressBarLayout.visibility = View.GONE
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {
        val parameters = Bundle()
        parameters.putString("fields", "id, first_name, middle_name, last_name, name, picture, email")
        GraphRequest(token,"/$userId/", parameters, HttpMethod.GET, GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject ?: return@Callback

                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

//                if (jsonObject.has("id")) {
//                    val facebookId = jsonObject.getString("id")
//                }
//                if (jsonObject.has("first_name")) {
//                    val facebookFirstName = jsonObject.getString("first_name")
//                }
//                if (jsonObject.has("middle_name")) {
//                    val facebookMiddleName = jsonObject.getString("middle_name")
//                }
//                if (jsonObject.has("last_name")) {
//                    val facebookLastName = jsonObject.getString("last_name")
//                }
//            if (jsonObject.has("email")) {
//                val facebookEmail = jsonObject.getString("email")
//            }
//            if (jsonObject.has("picture")) {
//                val facebookPictureObject = jsonObject.getJSONObject("picture")
//                if (facebookPictureObject.has("data")) {
//                    val facebookDataObject = facebookPictureObject.getJSONObject("data")
//                    if (facebookDataObject.has("url")) {
//                        val facebookProfilePicURL = facebookDataObject.getString("url")
//                    }
//                }
//            }

                if (jsonObject.has("name")) {
                    val facebookName= jsonObject.getString("name")
                    val editor = sharedPreferences.edit()
                    editor.putString("name", facebookName)
                    editor.apply()
                }

                startHomeActivity()
                binding.progressBarLayout.visibility = View.GONE
            }).executeAsync()
    }

    private fun startHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        const val RC_SIGN_IN = 1001
      //  const val EXTRA_NAME = "EXTRA_NAME"

    }
}