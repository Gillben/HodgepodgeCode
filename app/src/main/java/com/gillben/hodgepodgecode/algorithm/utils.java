package com.gillben.hodgepodgecode.algorithm;

import java.util.Arrays;
import java.util.Random;

public class utils {

    //同于对比自定义排序是否正确
    public static void solidSort(int[] array) {
        Arrays.sort(array);
    }


    public static void swap(int[] array, int before, int after) {
        int temp = array[before];
        array[before] = array[after];
        array[after] = temp;
    }

    public static int[] generateArray(int length, int range) {
        if (length < 1) {
            throw new RuntimeException("length < 1");
        }
        Random random = new Random();
        int[] array = new int[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextInt(range);
        }
        return array;
    }

}
