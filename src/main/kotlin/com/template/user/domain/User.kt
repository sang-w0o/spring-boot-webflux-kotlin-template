package com.template.user.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(value = "users")
class User(name: String, email: String, password: String) {
    @Id
    @Column(value = "user_id")
    var id: Int? = null

    @Column
    var name: String = name

    @Column
    var password: String? = password

    @Column
    var email: String = email
}
