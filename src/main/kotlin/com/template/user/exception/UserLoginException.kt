package com.template.user.exception

import com.template.common.exception.NotFoundException

class UserLoginException : NotFoundException("이메일 또는 비밀번호가 잘못되었습니다.")
