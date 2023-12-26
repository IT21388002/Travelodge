//package com.example.mad_project
//
//class ReadWriteUserDetails(textDob: String, textGender: String, textMobile: String) {
//
////    //constructor
////    class ReadWriteUserDetails(){
////
////    }
//
//    init {
//        // Code for any additional setup or initialization can be included here
//    }
//
//    var doB: String = textDob
//    var gender: String = textGender
//    var mobile: String = textMobile
//
//
//}
//




package com.example.mad_project

class ReadWriteUserDetails(textDob: String, textGender: String, textMobile: String) {

    var doB: String = textDob
    var gender: String = textGender
    var mobile: String = textMobile

    // Secondary constructor with default values
    constructor() : this("", "", "") {
        // Code for any additional setup or initialization can be included here
    }

    // Additional code and functions can be included here
}
