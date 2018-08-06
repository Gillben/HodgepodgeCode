package com.gillben.hodgepodgecode.algorithm;


public class QuickSortAlgorithm {

    private static void quickSort(int[] array, int left, int right) {
        if (array == null || array.length < 2) {
            return;
        }

        if (left < right) {
            //实现随机快排
            utils.swap(array, (int) (left + Math.random() * (right - left + 1)), right);
            int position[] = partition(array, left, right);

            //递归调用
            quickSort(array, left, position[0] - 1);
            quickSort(array, position[1] + 1, right);
        }
    }

    private static int[] partition(int[] array, int left, int right) {
        //用于记录左边数据的下标起始位置
        int less = left - 1;
        //用于记录右边数据的下标起始位置
        int more = right;


        //约束条件
        while (left < more) {
            //小于基准值，放在数组左边
            if (array[left] < array[right]) {
                utils.swap(array, ++less, left++);
            }
            //大于基准值，放在数组右边
            else if (array[left] > array[right]) {
                utils.swap(array, --more, left);
            }
            //等于基准值，跳到下一个数
            else {
                left++;
            }
        }
        //整个数组完成后把最后一位放在中间位置，即和右边的第一数据交换
        utils.swap(array, more, right);

        return new int[]{less + 1, more};  //返回等于范围的左右两端位置
    }


    public static void main(String[] args) {
        int[] array = utils.generateArray(10, 1000);
        int[] temp = new int[array.length];
        System.arraycopy(array, 0, temp, 0, array.length);

        quickSort(array, 0, array.length - 1);
        for (int data : array) {
            System.out.print(data + " ");
        }

        System.out.println("\r\n");
        utils.solidSort(temp);
        for (int data1 : temp) {
            System.out.print(data1 + " ");
        }
    }
}
