package co.mide.wallpaperdump.util;

import android.os.Build;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper class for the Objects class that uses custom implementation for api level < 19
 */
@SuppressWarnings("unused")
public class Objects {

    private static boolean useFallback(){
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private static boolean isSameClass(Object object1, Object object2){
        if(object1 == null ^ object2 == null)//check if one but not the other is null
            return false;
        if(object1 == null)//if one is null then both are null are this point
            return true;
        return object1.getClass().equals(object2.getClass());
    }

    public static boolean equals(String val1, String val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.equals(val2);
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Boolean val1, Boolean val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.booleanValue() == val2.booleanValue();
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Integer val1, Integer val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.intValue() == val2.intValue();
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Long val1, Long val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.longValue() == val2.longValue();
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Double val1, Double val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.doubleValue() == val2.doubleValue();
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Float val1, Float val2){
        //check for null values
        if(val1 == null ^ val2 == null)//check if one but not the other is null
            return false;
        if(val1 == null)//if one is null then both are null are this point
            return true;

        if(useFallback()){
            return val1.floatValue() == val2.floatValue();
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(List val1, List val2){
        //don't bother checking if same class, as it should work across classes

        //check for null values
        if(val1 == null && val2 == null)
            return true;

        //At this point if either is null then so is the other
        if(val1 == null || val2 == null)
            return false;

        if(useFallback()){
            if(val1.size() != val2.size()) {
                return false;
            }else{
                Iterator it1 = val1.iterator();
                Iterator it2 = val2.iterator();
                while(it1.hasNext()){
                    if(!Objects.equals(it1.next(), it2.next())){
                        return false;
                    }
                }
                return true;
            }
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static boolean equals(Object val1, Object val2){
        //check if same type
        if(!isSameClass(val1, val2))
            return false;

        //check if both are null
        //At this point if either is null then so is the other
        if(val1 == null || val2 == null)//Used or just because of lint
            return true;

        //check if objects are known types
        if(val1 instanceof Boolean)
            return Objects.equals((Boolean)val1, (Boolean)val2);
        if(val1 instanceof Integer)
            return Objects.equals((Integer)val1, (Integer)val2);
        if(val1 instanceof Long)
            return Objects.equals((Long)val1, (Long)val2);
        if(val1 instanceof Float)
            return Objects.equals((Float)val1, (Float)val2);
        if(val1 instanceof Double)
            return Objects.equals((Double)val1, (Double)val2);
        if(val1 instanceof List)
            return Objects.equals((List)val1, (List)val2);

        if(useFallback()){
            return val1.equals(val2);
        }else {
            return java.util.Objects.equals(val1, val2);
        }
    }

    public static int hash(Object... values){
        if(useFallback())
            return Arrays.hashCode(values);
        return java.util.Objects.hash(values);
    }
}
