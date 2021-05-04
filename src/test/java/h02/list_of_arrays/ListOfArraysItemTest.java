package h02.list_of_arrays;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ListOfArraysItemTest {

    @Test
    public void classDefinitionCorrect() {
        try {
            Class<?> listOfArraysItemClass = Class.forName("h02.list_of_arrays.ListOfArraysItem");

            // is not public
            assertFalse(Modifier.isPublic(listOfArraysItemClass.getModifiers()), "Class is public");

            // is generic
            assertEquals(1, listOfArraysItemClass.getTypeParameters().length, "Class is not generic");
            assertEquals(
                    "T",
                    listOfArraysItemClass.getTypeParameters()[0].getName(),
                    "Type parameter is not named 'T'"
            );

            // constructor
            assertTrue(() -> {
                try {
                    return listOfArraysItemClass.getDeclaredConstructor().getParameterCount() == 0;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }, "ListOfArrays is not required by the assignment to have an empty constructor, however, these tests rely on it and won't run unless one exists");

            // fields
            assertTrue(() -> {
                try {
                    Field field = listOfArraysItemClass.getDeclaredField("numberOfListElemsInArray");

                    return field.getType().equals(int.class) && Modifier.isPublic(field.getModifiers());
                } catch (NoSuchFieldException e) {
                    return false;
                }
            }, "Field 'numberOfListElemsInArray' is not declared correctly");
            assertTrue(() -> {
                try {
                    Field field = listOfArraysItemClass.getDeclaredField("arrayOfElems");

                    return field.getType().equals(Object[].class) && Modifier.isPublic(field.getModifiers());
                } catch (NoSuchFieldException e) {
                    return false;
                }
            }, "Field 'arrayOfElems' is not declared correctly");
            assertTrue(() -> {
                try {
                    Field field = listOfArraysItemClass.getDeclaredField("next");

                    return field.getType().equals(listOfArraysItemClass) && Modifier.isPublic(field.getModifiers());
                } catch (NoSuchFieldException e) {
                    return false;
                }
            }, "Field 'next' is not declared correctly");
            assertTrue(() -> {
                try {
                    Field field = listOfArraysItemClass.getDeclaredField("previous");

                    return field.getType().equals(listOfArraysItemClass) && Modifier.isPublic(field.getModifiers());
                } catch (NoSuchFieldException e) {
                    return false;
                }
            }, "Field 'previous' is not declared correctly");

            // fields after instantiation
            Object instance = listOfArraysItemClass.getDeclaredConstructor().newInstance();

            assertEquals(
                    0,
                    listOfArraysItemClass.getDeclaredField("numberOfListElemsInArray").get(instance),
                    "Field 'numberOfListElemsInArray' is not 0 after instantiation with empty constructor"
            );
            assertNull(
                    listOfArraysItemClass.getDeclaredField("arrayOfElems").get(instance),
                    "Field 'arrayOfElems' is not null after instantiation with empty constructor"
            );
            assertNull(
                    listOfArraysItemClass.getDeclaredField("next").get(instance),
                    "Field 'next' is not null after instantiation with empty constructor"
            );
            assertNull(
                    listOfArraysItemClass.getDeclaredField("previous").get(instance),
                    "Field 'previous' is not null after instantiation with empty constructor"
            );
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " could not be found");
        } catch (ReflectiveOperationException e) {
            fail(e);
        }
    }
}
