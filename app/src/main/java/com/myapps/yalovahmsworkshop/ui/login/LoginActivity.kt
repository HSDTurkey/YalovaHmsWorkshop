package com.myapps.yalovahmsworkshop.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.account.AccountAuthManager
import com.huawei.hms.support.account.service.AccountAuthService
import com.huawei.hms.support.api.entity.common.CommonConstant
import com.myapps.yalovahmsworkshop.databinding.ActivityLoginBinding
import com.myapps.yalovahmsworkshop.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var accountAuthService: AccountAuthService

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setListeners()
    }

    private fun setListeners() {
        binding.HuaweiIdAuthButton.setOnClickListener {
            val task = accountAuthService.silentSignIn()
            task.addOnSuccessListener {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("ProfilePicture", it.avatarUriString)
                    putExtra("Name", it.displayName)
                    putExtra("Email", it.email)
                }
                startActivity(intent)
                finish()
            }
            task.addOnFailureListener { e ->
                if (e is ApiException) {
                    val signInIntent = accountAuthService.signInIntent
                    signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true)
                    signInWithHuaweiID.launch(signInIntent)
                }
            }
        }
    }

    private var signInWithHuaweiID =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val authAccountTask = AccountAuthManager.parseAuthResultFromIntent(result.data)
                if (authAccountTask.isSuccessful) {
                    val authAccount = authAccountTask.result
                    Log.i("LoginFragment", "onActivityResult of sigInInIntent")

                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("ProfilePicture", authAccount.avatarUriString)
                        putExtra("Name", authAccount.displayName)
                        putExtra("Email", authAccount.email)
                    }
                    startActivity(intent)
                    finish()

                } else {
                    Log.e("LoginFragment", "sign in failed : " + (authAccountTask.exception as ApiException).statusCode)
                }
            }
        }
}
