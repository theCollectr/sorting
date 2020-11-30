package ca.bcit;

import edu.princeton.cs.algs4.Stopwatch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Homework - Sorting
 * Sort the list of doubles in the fastest possible way.
 * The only method you can change is the sort() method.
 * You can add additional methods if needed, without changing the load() and test() methods.
 */
@SuppressWarnings({"rawtypes", "unchecked", "FieldCanBeLocal", "ConstantConditions", "SameParameterValue"})
public class Sorting {
    protected List list = new ArrayList<Integer>();
    private final Random random = new Random(100);
    private final int COLLECTIONS_SORT = 0;
    private final int MERGE_SORT = 1;
    private final int MERGE_SORT_LIST = 2;
    private final int QUICK_SORT = 3;
    private final int RADIX_SORT = 4;
    private final int FIRST_ELEMENT_PIVOT = 0;
    private final int LAST_ELEMENT_PIVOT = 1;
    private final int MIDDLE_ELEMENT_PIVOT = 2;
    private final int RANDOM_PIVOT = 3;

    // options
    private final boolean CHECKING = false;
    private final int SORT_ALGO = RADIX_SORT;
    private final int RADIX = 16;
    private final int QUICKSORT_PIVOT = RANDOM_PIVOT;

    // for debugging purposes
    public static void main(String[] args) {
        Sorting sorting = new Sorting();
        sorting.load();
        sorting.sort(sorting.list);
        sorting.test();
        System.out.println(sorting.check());
    }

    // for debugging purposes
    protected boolean check() {
        List list_copy = new ArrayList(list);
        Collections.sort(list_copy);
        return list_copy.equals(list);
    }

    /**
     * Loading the text files with double numbers
     */
    protected void load() {
        try (Stream<String> stream = Files.lines(Paths.get("numbers.txt"))) {
            stream.forEach(x -> list.add(Integer.parseInt(x)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Testing of your solution, using 100 shuffled examples
     *
     * @return execution time
     */
    protected double test() {
        Stopwatch watch = new Stopwatch();
        for (int i = 0; i < 100; i++) {
            Collections.shuffle(list, new Random(100));
            sort(list);
            assert !CHECKING || (check()); // for debugging purposes
        }
        return watch.elapsedTime();
    }

    /**
     * Sorting method - add your code in here
     *
     * @param list - list to be sorted
     */
    private void sort(List list) {
        if (SORT_ALGO == COLLECTIONS_SORT)
            Collections.sort(list);
        else if (SORT_ALGO == MERGE_SORT_LIST)
            mergeSort(list, 0, list.size() - 1);
        else {
            // these work on arrays so list has to be converted into an array first
            int[] arr = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                arr[i] = (int) list.get(i);
            }

            if (SORT_ALGO == MERGE_SORT)
                mergeSort(arr, 0, arr.length - 1);
            else if (SORT_ALGO == QUICK_SORT)
                quickSort(arr, 0, arr.length - 1);
            else if (SORT_ALGO == RADIX_SORT)
                radixSort(arr, RADIX);

            // copy the sorted arr into our list
            for (int i = 0; i < list.size(); i++) {
                list.set(i, arr[i]);
            }
        }
    }


    private void quickSort(int[] arr, int low, int high) {
        // keeps recursively splitting until size of segment is <= 1
        if (low < high) {
            // splits the segment into two parts then sorts each segment on its own
            int pivot = split(arr, low, high);
            quickSort(arr, low, pivot - 1);
            quickSort(arr, pivot + 1, high);
        }
    }

    private int split(int[] arr, int low, int high) {
        int pivot = choosePivot(low, high); // gets the pivot
        swap(arr, pivot, high); // puts the pivot at the end of the segment
        // puts values smaller than the pivot at the beginning of the segment
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] < arr[high]) {
                i++;
                swap(arr, i, j);
            }
        }
        // puts the pivot in the middle of the segment
        swap(arr, i + 1, high);
        return i + 1;
    }

    private int choosePivot(int low, int high) {
        if (QUICKSORT_PIVOT == FIRST_ELEMENT_PIVOT)
            return low;
        else if (QUICKSORT_PIVOT == LAST_ELEMENT_PIVOT)
            return high;
        else if (QUICKSORT_PIVOT == MIDDLE_ELEMENT_PIVOT)
            return (high + low) / 2;
        else if (QUICKSORT_PIVOT == RANDOM_PIVOT)
            return random.nextInt(high - low + 1) + low;
        return high; // default case
    }

    private void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }


    private void radixSort(int[] arr, int radix) {
        // finds the max value and then gets the count of digits it has
        int max = max(arr);
        int digitCount = (int) Math.floor((Math.log10(max) / Math.log10(radix) + 1));
        int[] original = arr.clone(); // values in arr will be overwritten so we need a copy of it
        for (int digit = 0; digit < digitCount; digit++) {
            // uses count sort to sort the array according to each digit
            countingSort(arr, original, radix, digit);
        }
    }

    private void countingSort(int[] arr, int[] original, int radix, int digit) {
        int[] digitFreq = new int[radix];
        int div = (int) Math.pow(radix, digit);

        // finds the area each value will cover in the result array
        for (int element : arr) {
            int number = (element / div) % radix;
            digitFreq[number]++;
        }
        for (int i = 1; i < radix; i++)
            digitFreq[i] += digitFreq[i - 1];

        // does the actual sorting
        for (int i = arr.length - 1; i >= 0; i--) {
            int number = (original[i] / div) % radix;
            digitFreq[number]--;
            arr[digitFreq[number]] = number;
        }
    }

    private int max(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (arr[i] > max)
                max = arr[i];
        return max;
    }


    private void mergeSort(int[] arr, int low, int high) {
        // keeps recursively splitting until size of segment is <= 1
        if (low < high) {
            int pivot = (high + low) / 2;
            mergeSort(arr, low, pivot);
            mergeSort(arr, pivot + 1, high);
            merge(arr, low, pivot, high);
        }
    }

    private void merge(int[] arr, int low, int pivot, int high) {
        // values in the original array will be overwritten so we need a copy of it
        int[] lowerPart = new int[pivot - low + 1];
        int[] upperPart = new int[high - pivot];
        System.arraycopy(arr, low, lowerPart, 0, pivot + 1 - low);
        System.arraycopy(arr, pivot + 1, upperPart, 0, high - pivot);

        // merges both parts
        int i = low;
        for (int lowerItr = 0, upperItr = 0; i <= high; ) {
            if (lowerItr == lowerPart.length) { // in case we reach the end of the lower segment
                arr[i] = upperPart[upperItr];
                upperItr++;
            } else if (upperItr == upperPart.length) { // in case we reach the end of the upper segment
                arr[i] = lowerPart[lowerItr];
                lowerItr++;
            } else if (lowerPart[lowerItr] <= upperPart[upperItr]) {
                arr[i] = lowerPart[lowerItr];
                lowerItr++;
            } else {
                arr[i] = upperPart[upperItr];
                upperItr++;
            }
            i++;
        }
    }

    private void mergeSort(List list, int low, int high) {
        if (low >= high) return;
        int pivot = (high + low) / 2;
        mergeSort(list, low, pivot);
        mergeSort(list, pivot + 1, high);
        merge(list, low, pivot, high);
    }

    private void merge(List list, int low, int pivot, int high) {
        List lowerPart = new ArrayList(pivot - low + 1);
        List upperPart = new ArrayList(high - pivot);
        for (int i = low; i <= pivot; i++)
            lowerPart.add(i - low, list.get(i));
        for (int i = pivot + 1; i <= high; i++)
            upperPart.add(i - pivot - 1, list.get(i));

        int i = low;
        for (int lowerItr = 0, upperItr = 0; i <= high; ) {
            if (lowerItr == lowerPart.size()) {
                list.set(i, upperPart.get(upperItr));
                upperItr++;
            } else if (upperItr == upperPart.size()) {
                list.set(i, lowerPart.get(lowerItr));
                lowerItr++;
            } else if ((int) lowerPart.get(lowerItr) <= (int) upperPart.get(upperItr)) {
                list.set(i, lowerPart.get(lowerItr));
                lowerItr++;
            } else {
                list.set(i, upperPart.get(upperItr));
                upperItr++;
            }
            i++;
        }
    }
}