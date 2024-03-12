package org.penakelex.database.extenstions

/**
 * Checks if sorted ascending array contains [element] using binarySearch
 * @param element numeric element that needs to check
 * @return true if array contains [element] else false
 * */
fun <Type : Number> Array<Type>.binaryContains(element: Type): Boolean = binarySearch(element) >= 0
