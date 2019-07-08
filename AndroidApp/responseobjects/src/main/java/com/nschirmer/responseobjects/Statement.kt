package com.nschirmer.responseobjects

import java.io.Serializable
import java.sql.Date

class Statement (val title: String?, val desc: String?, val date: Date?, val value: Double?): Serializable