package com.example.medicationadherenceapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.medicationadherenceapp.MainActivity
import com.example.medicationadherenceapp.R
import com.google.android.material.card.MaterialCardView

class LoginActivity : AppCompatActivity() {

    private lateinit var userTypeSelectionView: ConstraintLayout
    private lateinit var loginFormView: ConstraintLayout
    private lateinit var patientCard: MaterialCardView
    private lateinit var familyCard: MaterialCardView
    private lateinit var backButton: Button
    private lateinit var loginButton: Button
    private lateinit var loginTitle: TextView
    private lateinit var loginIcon: ImageView
    private lateinit var passwordInput: EditText
    private lateinit var passwordToggle: ImageView
    private lateinit var forgotPasswordButton: TextView
    private lateinit var signupButton: TextView

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        userTypeSelectionView = findViewById(R.id.user_type_selection_view)
        loginFormView = findViewById(R.id.login_form_view)
        patientCard = findViewById(R.id.patient_card)
        familyCard = findViewById(R.id.family_card)
        backButton = findViewById(R.id.back_button)
        loginButton = findViewById(R.id.login_button)
        loginTitle = findViewById(R.id.login_title)
        loginIcon = findViewById(R.id.login_icon)
        passwordInput = findViewById(R.id.password_input)
        passwordToggle = findViewById(R.id.password_toggle)
        forgotPasswordButton = findViewById(R.id.forgot_password_button)
        signupButton = findViewById(R.id.signup_button)

        patientCard.setOnClickListener { showLoginForm("patient") }
        familyCard.setOnClickListener { showLoginForm("family") }
        backButton.setOnClickListener { showUserTypeSelection() }

        loginButton.setOnClickListener {
            // For now, just navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                passwordInput.transformationMethod = null
                passwordToggle.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            } else {
                passwordInput.transformationMethod = PasswordTransformationMethod()
                passwordToggle.setImageResource(android.R.drawable.ic_menu_view)
            }
        }

        forgotPasswordButton.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        signupButton.setOnClickListener {
            Toast.makeText(this, "Sign up clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoginForm(userType: String) {
        userTypeSelectionView.visibility = View.GONE
        loginFormView.visibility = View.VISIBLE

        if (userType == "patient") {
            loginTitle.text = getString(R.string.patient_login)
            loginIcon.setImageResource(R.drawable.ic_patient)
        } else {
            loginTitle.text = getString(R.string.family_member_login)
            loginIcon.setImageResource(R.drawable.ic_family)
        }
    }

    private fun showUserTypeSelection() {
        userTypeSelectionView.visibility = View.VISIBLE
        loginFormView.visibility = View.GONE
    }
}