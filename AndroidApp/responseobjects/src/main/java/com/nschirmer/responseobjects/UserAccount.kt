package com.nschirmer.responseobjects

import java.io.Serializable

class UserAccount (val userId: Long?, val name: String?, val bankAccount: String?, val agency: String?, val balance: Double?): Serializable