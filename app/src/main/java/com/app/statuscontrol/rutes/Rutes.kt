package com.app.statuscontrol.rutes

sealed class Routes(val route: String) {
    object Login : Routes("Login")
    object Register : Routes("Register")
}