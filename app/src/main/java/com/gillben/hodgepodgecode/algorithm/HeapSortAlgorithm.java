package com.gillben.hodgepodgecode.algorithm;

/**
 * 思路：
 * 1、数组抽象化一个完全二叉树
 * 2、实现一个大根堆，即每个子数最大值都是其头节点
 * 3、（根据大根堆的特性）在heapSize（堆的大小）中遍历，每交换一次数据 heapSize-1（每次取最大数，然后从堆中分离），
 */


public class HeapSortAlgorithm {

    private static void heapSort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        //数组抽象成一颗完全二叉树
        for (int i = 0; i < array.length; i++) {
            heapInsert(array, i);
        }
        int heapSize = array.length;
        while (heapSize > 0) {
            utils.swap(array, 0, --heapSize);
            heapify(array, 0, heapSize);
        }
    }

    //实现大根堆过程
    private static void heapInsert(int[] array, int index) {
        while (array[index] > array[(index - 1) / 2]) {
            utils.swap(array, index, (index - 1) / 2);
            index = (index - 1) / 2;
        }
    }


    //当数组的某个元素的值发生变化后，做出对应调整
    private static void heapify(int[] array, int index, int heapSize) {
        int left = index * 2 + 1;
        while (left < heapSize) {
            //对比左右子节点的大小，left + 1 > heapSize 表示右节点不存在
            int largest = left + 1 < heapSize && array[left + 1] > array[left]
                    ? left + 1
                    : left;

            //与当前的节点进行对比
            largest = array[largest] > array[index] ? largest : index;
            if (largest == index) {
                break;
            }

            utils.swap(array, index, largest);
            index = largest;
            left = index * 2 + 1;
        }
    }


    public static void main(String[] args) {
        int[] data = utils.generateArray(30, 1000);
        int[] temp = new int[data.length];
        System.arraycopy(data, 0, temp, 0, data.length);


        heapSort(data);
        for (int d : data) {
            System.out.print(d + " ");
        }
        System.out.println("\r\n");

        utils.solidSort(temp);
        for (int t : temp) {
            System.out.print(t + " ");
        }
    }
}
