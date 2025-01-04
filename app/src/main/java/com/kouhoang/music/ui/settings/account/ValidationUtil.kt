package com.kouhoang.music.ui.settings.account

import android.util.Patterns

class ValidationUtils {

    companion object {

        fun checkValidEmail(email: String): String? {
            return if (email.isBlank()) {
                "Email can't blank"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Email is wrong format"
            } else {
                null
            }
        }

        fun checkValidSignInInput(email: String, password: String): String? {
            return if (email.isBlank()) {
                "Email can't blank"
            } else if (password.isBlank()) {
                "Password can't blank"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Email is wrong format"
            } else if (password.length < 6) {
                "Password must contain at least 6 characters"
            } else {
                null
            }
        }

        fun checkValidSignUpInput(
            email: String,
            name: String,
            password: String,
            confirmPass: String
        ): String? {
            return if (email.isBlank()) {
                "Email can't blank"
            } else if (name.isBlank()) {
                "Name can't blank"
            } else if (password.isBlank()) {
                "Password can't blank"
            } else if (confirmPass.isBlank()) {
                "Confirm password can't blank"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                "Email is wrong format"
            } else if (password.length < 6) {
                "Password must contain at least 6 characters"
            } else if (password != confirmPass) {
                "Those passwords did’t match. Try again."
            } else {
                null
            }
        }

        fun checkValidChangePasswordInput(
            oldPass: String,
            newPass: String,
            confirmPass: String
        ): String? {
            return if (oldPass.isBlank()) {
                "Password can't blank"
            } else if (newPass.isBlank()) {
                "New password can't blank"
            } else if (confirmPass.isBlank()) {
                "Confirm password can't blank"
            } else if (newPass.length < 6) {
                "New password must contain at least 6 characters"
            } else if (newPass != confirmPass) {
                "Those passwords did’t match. Try again."
            } else {
                null
            }
        }
    }
}