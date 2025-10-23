package com.example.teamify.navigation

import kotlinx.serialization.Serializable


interface NavRoute {
    val route: String
}

@Serializable
data object HomeRoute: NavRoute {
    override val route: String = "HomeRoute"
}

@Serializable
data object LoginRoute: NavRoute {
    override val route: String = "LoginRoute"
}

@Serializable
data object SignupRoute: NavRoute {
    override val route: String = "SignupRoute"
}
