package com.iwombat.rollbacks.persist.model

enum class Marque {
    ARIEL,
    BSA,
    LAMBRETTA,
    YAMAHA,
    HONDA,
    HARLEY_DAVIDSON,
    TRIUMPH,
    NSA,
    VINCENT,
    VICTOR,
    UNKNOWN;

    companion object {
        fun from (type:String?) : Marque = values().find {it.name == type} ?: UNKNOWN
    }
}
