package com.example.teamify.domain.model

import com.google.firebase.firestore.PropertyName

data class User(
    @PropertyName("uid") val uid: String = "",
    @PropertyName("email") val email: String = "",
    @PropertyName("name") val name: String = ""
)



