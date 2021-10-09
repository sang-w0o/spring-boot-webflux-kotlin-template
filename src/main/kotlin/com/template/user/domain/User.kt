package com.template.user.domain

import org.springframework.data.annotation.Id

class User(name: String, email: String, password: String) {
    @Id
    var id: String? = null

    var name: String = name

    var password: String? = password

    var email: String = email
}
