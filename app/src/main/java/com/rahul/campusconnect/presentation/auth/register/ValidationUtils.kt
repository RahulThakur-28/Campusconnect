package com.rahul.campusconnect.presentation.auth.register

object ValidationUtils {

    fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Full name is required."
            name.length < 3 -> "Name must be at least 3 characters."
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email is required."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Enter a valid email address."
            else -> null
        }
    }

    fun validateStudentId(studentId: String): String? {
        return when {
            studentId.isBlank() -> "Student ID is required."
            else -> null
        }
    }

    fun validateDepartment(department: String): String? {
        return when {
            department.isBlank() -> "Department is required."
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Password is required."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }
    }

    fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            confirmPassword.isBlank() -> "Confirm Password is required."
            password != confirmPassword -> "Passwords do not match."
            else -> null
        }
    }
}