package com.myapps.yalovahmsworkshop.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.huawei.hms.support.account.service.AccountAuthService
import com.myapps.yalovahmsworkshop.databinding.FragmentProfileBinding
import com.myapps.yalovahmsworkshop.ui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var accountAuthService: AccountAuthService

    private lateinit var binding: FragmentProfileBinding
    private var userPicture = ""
    private var userName = ""
    private var userEmail = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        userPicture = requireActivity().intent.getStringExtra("ProfilePicture").toString()
        userName = requireActivity().intent.getStringExtra("Name").toString()
        userEmail = requireActivity().intent.getStringExtra("Email").toString()

        initializeProfileInformation()
        setListeners()

        return binding.root
    }

    private fun initializeProfileInformation() {
        Glide.with(requireContext()).load(userPicture).into(binding.profilePicture)
        binding.profileName.text = userName
        binding.userEmail.text = userEmail
    }

    private fun setListeners() {
        binding.signOut.setOnClickListener {
            val signOutTask = accountAuthService.signOut()

            signOutTask.addOnSuccessListener {
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

                Log.i("ProfileFragment", "signOut Success")
            }.addOnFailureListener {
                Log.i("ProfileFragment", "signOut fail")
            }
        }
    }
}