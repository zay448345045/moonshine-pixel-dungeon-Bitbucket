package com.watabou.utils;

import java.lang.reflect.Array;
import static java.lang.reflect.Array.*;
import java.util.Arrays;

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
    public static Object wrap(Object src) {
        try {
            int length = src.getClass().isArray() ? getLength(src) : 0;
            if (length == 0)
                return src;
            Object dest = newInstance(typeCastTo(wrap(get(src, 0))), length);
            for (int i = 0; i < length; i++)
                set(dest, i, wrap(get(src, i)));
            return dest;
        } catch (Exception e) {
            throw new ClassCastException("Object to wrap must be an array of primitives with no 0 dimensions");
        }
    }
    private static Class<?> typeCastTo(Object obj) {
        Class<?> type = obj.getClass();
        if(type.equals(boolean.class)) return Boolean.class;
        if(type.equals(byte.class)) return Byte.class;
        if(type.equals(char.class)) return Character.class;
        if(type.equals(double.class)) return Double.class;
        if(type.equals(float.class)) return Float.class;
        if(type.equals(int.class)) return Integer.class;
        if(type.equals(long.class)) return Long.class;
        if(type.equals(short.class)) return Short.class;
        if(type.equals(void.class)) return Void.class;
        return type;
    }
}
