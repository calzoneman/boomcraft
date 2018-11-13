package net.calzoneman.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class Collections {
    public static <T> void sort(List<T> list, Comparator<? super T> comparator) {
        List<T> copy = new ArrayList<T>(list);

        mergeSort(copy, 0, copy.size(), list, comparator);
    }

    private static <T> void mergeSort(List<T> src, int start, int end, List<T> dst, Comparator<? super T> comparator) {
        if (end - start < 2) {
            return;
        }

        int mid = (start + end)/2;

        mergeSort(dst, start, mid, src, comparator);
        mergeSort(dst, mid, end, src, comparator);

        int left = start;
        int right = mid;
        for (int i = start; i < end; i++) {
            if (left < mid && (right >= end || comparator.compare(src.get(left), src.get(right)) <= 0)) {
                dst.set(i, src.get(left));
                left++;
            } else {
                dst.set(i, src.get(right));
                right++;
            }
        }
    }
}
