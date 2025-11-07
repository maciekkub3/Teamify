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

@Serializable
data object ChatRoute: NavRoute {
    override val route: String = "ChatRoute"
}

@Serializable
data object CalendarRoute: NavRoute {
    override val route: String = "CalendarRoute"
}

@Serializable
data object AnnouncementRoute: NavRoute {
    override val route: String = "AnnouncementRoute"
}

@Serializable
data class ConversationRoute(
    val friendId: String? = null,
    val chatId: String? = null
): NavRoute {
    override val route: String = "ConversationRoute"
}
