package com.gillben.hodgepodgecode.algorithm;

public class Bubble_Select_InsertSort {


    private static void bubbleSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        int len = array.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len - i - 1; j++) {
                if (array[j] > array[j + 1]) {          //每完成一次找出数组中的最大值
                    utils.swap(array, j, j + 1);
                }
            }
        }
    }


    private static void selectSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        int len = array.length;
        for (int i = 0; i < len - 1; i++) {
            int pivotIndex = i;
            for (int j = i + 1; j < len; j++) {
                if (array[pivotIndex] > array[j]) {     //每次找出最小的值放在左边
                    pivotIndex = j;
                }
            }
            utils.swap(array, i, pivotIndex);
        }
    }


    private static void insertSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        int len = array.length;
        for (int i = 1; i < len; i++) {
            int target = array[i];
            int index = i;
            //抽取元素和前面的数据进行对比
            while (index > 0 && target < array[index - 1]) {
                array[index] = array[index - 1];
                index--;
            }
            array[index] = target;
        }
    }


    public static void main(String[] args) {
        int[] data = utils.generateArray(20, 100);
        int[] temp = new int[data.length];
        System.arraycopy(data, 0, temp, 0, data.length);

        insertSort(data);
        for (int d : data) {
            System.out.print(d + " ");
        }
        System.out.println("\r\n");

        insertSort(temp);
        for (int d : temp) {
            System.out.print(d + " ");
        }

    }

}
