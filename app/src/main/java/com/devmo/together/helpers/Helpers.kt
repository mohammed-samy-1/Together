package com.devmo.together.helpers

class Helpers{
    companion object{
        const val preferencesName = "MyPref"
        fun String.validateEmail(): Boolean {
            return if (this.isEmpty()) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
            }
        }

        fun String.validatePass(): Boolean {
            return !(this.isEmpty() || this.length < 8)
        }

        fun String.validateName(): Boolean {
            return !(this.isEmpty() || this.length < 2)
        }


    }
}