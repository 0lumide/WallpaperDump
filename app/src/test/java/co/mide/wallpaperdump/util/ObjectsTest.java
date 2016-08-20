package co.mide.wallpaperdump.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import co.mide.wallpaperdump.util.Objects;

import static org.junit.Assert.*;

/**
 * Unit test class to test the logic in the Objects class
 * Created by Olumide on 8/3/2016.
 */
public class ObjectsTest {
    final float SEVEN_ONES_FLOAT = 0.1111111F;
    final float EIGHT_ONES_FLOAT = 0.11111111F;

    final double SEVEN_ONES_DOUBLE = 0.1111111F;
    final double EIGHT_ONES_DOUBLE = 0.11111111F;

    @Test
    public void linkedlist_is_equal_to_arraylist() throws Exception {
        LinkedList<Integer> linkedList = new LinkedList<>();
        ArrayList<Integer> arrayList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            linkedList.add(i);
            arrayList.add(i);
        }
        assertTrue(Objects.equals(linkedList, arrayList));
    }

    @Test
    public void test_list_is_different_order() throws Exception {
        List<Integer> list1 = new LinkedList<>();
        List<Integer> list2 = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            list1.add(i);
            list2.add(9-i);
        }
        assertFalse(Objects.equals(list1, list2));
    }

    @Test
    public void test_list_with_null() throws Exception {
        List<Integer> list = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            list.add(i);
        }
        assertFalse(Objects.equals(list, null));
        assertFalse(Objects.equals(null, list));
        assertTrue(Objects.equals((List)null, null));
    }

    @Test
    public void list_is_equal() throws Exception {
        List<Integer> list1 = new LinkedList<>();
        List<Integer> list2 = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            list1.add(i);
            list2.add(i);
        }
        assertTrue(Objects.equals(list1, list2));
    }


    @Test
    public void list_is_not_same_length() throws Exception {
        List<Integer> list1 = new LinkedList<>();
        List<Integer> list2 = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            list1.add(i);
            if(i != 9)
                list2.add(i);
        }
        assertFalse(Objects.equals(list1, list2));
    }


    @Test
    public void list_is_not_equal() throws Exception {
        List<Integer> list1 = new LinkedList<>();
        List<Integer> list2 = new LinkedList<>();
        for(int i = 0; i < 10; i++){
            list1.add(i);
            if(i == 9)
                i = 12;
            list2.add(i);
        }
        assertFalse(Objects.equals(list1, list2));
    }

    @Test
    public void string_null_is_equal() throws Exception {
        assertTrue(Objects.equals((String)null, null));
    }

    @Test
    public void string_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, "hello"));
        assertFalse(Objects.equals("hello", null));
    }

    @Test
    public void boolean_null_is_equal() throws Exception {
        assertTrue(Objects.equals((Boolean) null, null));
    }

    @Test
    public void boolean_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, false));
        assertFalse(Objects.equals(false, null));
    }

    @Test
    public void integer_null_is_equal() throws Exception {
        assertTrue(Objects.equals((Integer) null, null));
    }

    @Test
    public void integer_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, 1));
        assertFalse(Objects.equals(1, null));
    }

    @Test
    public void long_null_is_equal() throws Exception {
        assertTrue(Objects.equals((Long) null, null));
    }

    @Test
    public void long_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, 1L));
        assertFalse(Objects.equals(1L, null));
    }

    @Test
    public void float_null_is_equal() throws Exception {
        assertTrue(Objects.equals((Float) null, null));
    }

    @Test
    public void float_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, 1.0F));
        assertFalse(Objects.equals(1.0F, null));
    }

    @Test
    public void double_null_is_equal() throws Exception {
        assertTrue(Objects.equals((Double) null, null));
    }

    @Test
    public void double_null_is_not_equal() throws Exception {
        assertFalse(Objects.equals(null, 1.0));
        assertFalse(Objects.equals(1.0, null));
    }

    @Test
    public void string_is_not_equal() throws Exception {
        assertFalse(Objects.equals("hello", "world"));
        assertFalse(Objects.equals("hello world", ""));
    }

    @Test
    public void string_is_equal() throws Exception {
        assertTrue(Objects.equals("", ""));
        assertTrue(Objects.equals("hello", "hello"));
    }

    @Test
    public void boolean_is_not_equal() throws Exception {
        assertFalse(Objects.equals(false, true));
        assertFalse(Objects.equals(true, false));
    }

    @Test
    public void boolean_is_equal() throws Exception {
        assertTrue(Objects.equals(true, true));
        assertTrue(Objects.equals(false, false));
    }

    @Test
    public void integer_is_not_equal() throws Exception {
        assertFalse(Objects.equals(1, -1));
        assertFalse(Objects.equals(Integer.MAX_VALUE, Integer.MIN_VALUE));
    }

    @Test
    public void integer_is_equal() throws Exception {
        assertTrue(Objects.equals(Integer.MAX_VALUE, Integer.MAX_VALUE));
        assertTrue(Objects.equals(Integer.MIN_VALUE, Integer.MIN_VALUE));
    }

    @Test
    public void long_is_not_equal() throws Exception {
        assertFalse(Objects.equals(1L, -1L));
        assertFalse(Objects.equals(Long.MAX_VALUE, Integer.MIN_VALUE));
    }

    @Test
    public void long_is_equal() throws Exception {
        assertTrue(Objects.equals(Long.MAX_VALUE, Long.MAX_VALUE));
        assertTrue(Objects.equals(Long.MIN_VALUE, Long.MIN_VALUE));
    }

    @Test
    public void float_is_not_equal() throws Exception {
        assertFalse(Objects.equals(SEVEN_ONES_FLOAT, EIGHT_ONES_FLOAT));
        assertFalse(Objects.equals(-EIGHT_ONES_FLOAT, EIGHT_ONES_FLOAT));
        assertFalse(Objects.equals(Float.MAX_VALUE, Float.MIN_VALUE));
    }

    @Test
    public void float_is_equal() throws Exception {
        assertTrue(Objects.equals(EIGHT_ONES_FLOAT, EIGHT_ONES_FLOAT));
        assertTrue(Objects.equals(Float.MAX_VALUE, Float.MAX_VALUE));
        assertTrue(Objects.equals(Float.MIN_VALUE, Float.MIN_VALUE));
    }

    @Test
    public void double_is_not_equal() throws Exception {
        assertFalse(Objects.equals(SEVEN_ONES_DOUBLE, EIGHT_ONES_DOUBLE));
        assertFalse(Objects.equals(-SEVEN_ONES_DOUBLE, EIGHT_ONES_DOUBLE));
        assertFalse(Objects.equals(Double.MAX_VALUE, Double.MIN_VALUE));
    }

    @Test
    public void double_is_equal() throws Exception {
        assertTrue(Objects.equals(EIGHT_ONES_DOUBLE, EIGHT_ONES_DOUBLE));
        assertTrue(Objects.equals(Double.MAX_VALUE, Double.MAX_VALUE));
        assertTrue(Objects.equals(Double.MIN_VALUE, Double.MIN_VALUE));
    }

    @Test
    public void hash_equality() throws Exception {
        assertEquals(Objects.hash(1, 1f, 1.0, 1L), Objects.hash(1, 1f, 1.0, 1L));
        assertNotEquals(Objects.hash(0, 1f, 1.0, 1L), Objects.hash(1, 1f, 1.0, 1L));
    }
}