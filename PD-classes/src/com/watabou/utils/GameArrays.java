package com.watabou.utils;

import java.lang.reflect.Array;

public class GameArrays {
    public static int findMaxInt(Iterable<Integer> iterable){
        int max=Integer.MIN_VALUE;
        for (int num : iterable){
            max = Math.max(num,max);
        }
        return max;
    }
    public static<T extends Comparable> T  findMax(Iterable<T> iterable){
        T max = null;
        boolean init = false;
        for (T num : iterable){
            if (!init) {
                max = num;
                init = true;
            }
            if (num.compareTo(max)>0){
                max=num;
            }
        }
        return max;
    }
    public static<T extends Comparable> T  findMin(Iterable<T> iterable){
        T min = null;
        boolean init = false;
        for (T num : iterable){
            if (!init) {
                min = num;
                init = true;
            }
            if (num.compareTo(min)<0){
                min=num;
            }
        }
        return min;
    }
    public static<T> boolean contain(T[] array, T element){
        for (int i = 0; i<array.length;i++){
            if (element.equals(array[i])){
                return true;
            }
        }
        return false;
    }
    public static<T> int indexOf(T[] array, T element){
        for (int i = 0; i<array.length;i++){
            if (element.equals(array[i])){
                return i;
            }
        }
        return -1;
    }
    public static<T> T[] concat(T[] a, T[] b, Class<T> cl) {
        int aLen = a.length;
        int bLen = b.length;
        T[] c= (T[]) Array.newInstance(cl,aLen+bLen);/*new Object[aLen+bLen];*/
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
