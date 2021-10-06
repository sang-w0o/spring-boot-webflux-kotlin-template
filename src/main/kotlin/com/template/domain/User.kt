package com.template.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class User(name: String, email: String, password: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "user_id")
    var id: Int? = null

    @Column(nullable = false, length = 100)
    var name: String = name

    @Column(length = 60)
    var password: String? = password

    @Column(nullable = false, length = 100, unique = true)
    var email: String = email
}
