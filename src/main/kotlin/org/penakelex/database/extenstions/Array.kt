package org.penakelex.database.extenstions

fun <Type : Number> Array<Type>.binaryContains(element: Type): Boolean = binarySearch(element) >= 0
