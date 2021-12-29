package com.example.acedropseller.utill

fun validPass(password: String): String? {
    return when {
        password.length < 8 -> {
            "Password must contains 8 Characters"
        }
        !password.matches(".*[A-Z].*".toRegex()) && (!password.matches(".*[\$#%@&*/+_=?^!].*".toRegex())) -> {
            "Must contain 1 Special character and 1 upper case character (\$#%@&*/+_=?^!)"
        }
        !password.matches(".*[a-z].*".toRegex()) -> {
            "Must contain 1 Lower case character"
        }
        !password.matches(".*[\$#%@&*/+_=?^!].*".toRegex()) -> {
            "Must contain 1 Special character (\$#%@&*/+_=?^!)"
        }
        !password.matches(".*[A-Z].*".toRegex()) -> {
            "Must contain 1 upper case character"
        }
        else -> null
    }
}