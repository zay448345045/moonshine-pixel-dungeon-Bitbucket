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
    public static<T> T[] concat(T[] a, T[] b) {
        Class cl = a.getClass().getComponentType();

        int aLen = a.length;
        int bLen = b.length;
        T[] c= (T[]) Array.newInstance(cl,aLen+bLen);
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

    public static Object simplify(Object src){
        try {
            int length = src.getClass().isArray() ? getLength(src) : 0;
            if (length == 0)
                return src;
            Object dest = newInstance(typeCastBack(simplify(get(src, 0))), length);
            for (int i = 0; i < length; i++)
                set(dest, i, simplify(get(src, i)));
            return dest;
        } catch (Exception e) {
            throw new ClassCastException("Object to simplify must be an array of non-primitives with no 0 dimensions");
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
    private static Class<?> typeCastBack(Object obj) {
        Class<?> type = obj.getClass();
        if(type.equals(Boolean.class)) return boolean.class;
        if(type.equals(Byte.class)) return byte.class;
        if(type.equals(Character.class)) return char.class;
        if(type.equals(Double.class)) return double.class;
        if(type.equals(Float.class)) return float.class;
        if(type.equals(Integer.class)) return int.class;
        if(type.equals(Long.class)) return long.class;
        if(type.equals(Short.class)) return short.class;
        if(type.equals(Void.class)) return void.class;
        return type;
    }
}
