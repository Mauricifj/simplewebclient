package com.mauricifj.simplewebclientapp

data class User(
    val gender: String,
    val name: Name,
    val email: String,
    val dob: DateOfBirth,
    val phone: String,
    val cell: String,
    val picture: Picture
)