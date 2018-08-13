package com.gillben.hodgepodgecode.kotlinRecord


private lateinit var int: String

var number = 3.0
val fruits = listOf("apple","banana","avocado","kiwifruit")


fun main(args: Array<String>) {
    val data = KotlinData("ZhangSan",20)
    println(data.name+"   "+data.age)
    fruits.filter { !it.startsWith("a") }.sortedBy { it }.map { it.toUpperCase() }.forEach { print("$it   ") }
}



