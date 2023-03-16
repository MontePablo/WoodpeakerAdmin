package com.example.woodpeakeradmin.models

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class User() {
    var name=""
    var id=""
    var email=""
    var mobile=""
    var sizes=HashMap<String,String>()
    var pack="Customer"
    var packBuyDate=""
    var packExpiryDate=""
    var Adresses=ArrayList<String>()
    var packagePaymentId=""

}