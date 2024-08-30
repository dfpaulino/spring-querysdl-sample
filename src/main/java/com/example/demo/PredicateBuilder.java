package com.example.demo;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredicateBuilder {

    static java.util.function.Predicate<Object> nonNullOrNonEmpty = o ->{
        if (o instanceof Integer && (Integer) o >0){
            return true;
        } else if (o instanceof Long && (Long) o >0) {
            return true;
        } else if (o instanceof String && !((String) o).isEmpty()) {
            return true;
        }
        return false;
    };

    public static <T> Predicate build(T model) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        PathBuilder<T> entityPath = pathBuilder(model);
        BooleanBuilder booleanBuilder= new BooleanBuilder();

        getFieldValues(model,null).forEach((k,v) ->{
            System.out.println("eval key "+k+" value "+v);
            if(nonNullOrNonEmpty.test(v)){
                System.out.println("Adding key "+k+" value "+v);
                booleanBuilder.and(entityPath.get(k).eq(v));
            }

        });
        return booleanBuilder;
    }



    static public <T> Map<String,Object> getFieldValues(T model,String prefix) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        String auxPrefix = prefix!=null?prefix+".":"";

        HashMap<String,Object> fieldValPair = new HashMap<>();
        List<Field> allFields = Arrays.asList(model.getClass().getDeclaredFields());
        for (Field field:allFields) {

            Object value = getValueFromGetter(field,model);
            if(field.getType().isPrimitive() || field.getType().getName().equals("java.lang.String") ) {
                System.out.println("Field "+auxPrefix+field.getName()+" value "+value);
                if (value!= null ) {
                    fieldValPair.put(auxPrefix+field.getName(), value);
                }
            }else {
                //its a class
                if (value !=null) {
                    System.out.println("Field " + auxPrefix + field.getName() + " is Object " + value);
                    Map<String, Object> fv = getFieldValues(value, field.getName());
                    fieldValPair.putAll(fv);
                }
            }
        }
        return fieldValPair;
    }

    private static <T> Object getValueFromGetter(Field field,T model) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), model.getClass());
            Method getter = pd.getReadMethod();
            return getter.invoke(model);
        }catch (IntrospectionException introspectionException) {
            System.out.println("Ignoring field "+field.getName());
            return null;
        }

    }

     public static <T> PathBuilder<T> pathBuilder(T model) {

         Class<?> myEntity = null;
            try {
                myEntity = Class.forName(model.getClass().getName());
                // check for @Entity Annotation
                System.out.println("Entity PathBuilder created + " + myEntity.getSimpleName());
                return new PathBuilder(myEntity, Introspector.decapitalize(myEntity.getSimpleName()));

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
     }
}
