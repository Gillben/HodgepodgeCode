package com.gillben.hodgepodgecode.kotlinRecord


private lateinit var int: String

var number = 3.0
val fruits = listOf("apple","banana","avocado","kiwifruit")
val map = mapOf(1 to "map1",2 to "map2")


fun main(args: Array<String>) {
    val data = KotlinData("ZhangSan",20)

    println(data.name+"   "+data.age)

    for (x in 10 downTo 1){
        print("$x   ")
    }
    fruits.filter { !it.startsWith("a") }.sortedBy { it }.map { it.toUpperCase() }.forEach { print("$it   ") }

    println(map[2])
}



