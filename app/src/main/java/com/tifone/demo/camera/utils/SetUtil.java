package com.tifone.demo.camera.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetUtil {

    public static <T> List<T> mergeArrays(T[] arrayFirst, T[] arraySecond) {
        // merge array
        ArrayList<T> result = new ArrayList<>();
        result.addAll(Arrays.asList(arrayFirst));
        result.addAll(Arrays.asList(arraySecond));
        return result;
    }
}
