package com.ttenushko.androidmvi.demo.presentation.utils

import java.lang.reflect.Field


fun Any.findField(predicate: (Field) -> Boolean): Field? {
    var clazz: Class<*>? = this::class.java
    while (clazz != Any::class.java) {
        clazz!!.declaredFields.forEach { field ->
            if (predicate(field)) {
                return field
            }
        }
        clazz = clazz.superclass
    }
    return null
}
