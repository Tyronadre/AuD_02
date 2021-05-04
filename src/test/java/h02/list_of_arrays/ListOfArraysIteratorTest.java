package h02.list_of_arrays;

import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static h02.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ListOfArraysIteratorTest {

    private static Class<?> listOfArraysClass;
    private static Object listOfArrays;
    private Iterator<?> iterator;

    /*
     * emptyList:      (0, { | null, null})
     * nonNullElement: (1, {1 | null})
     * nullElement:    (1, {null | null})
     * splitList:      (1, {0 | null}) <-> (2, {1, null | }) <-> (0, { | null, null}) <-> (1, {null | null})
     */
    private static Object emptyList, nonNullElement, nullElement, splitListHead, splitListTail;

    public ListOfArraysIteratorTest() {
        requireTest(new ListOfArraysItemTest(), "classDefinitionCorrect",
                "This test requires that ListOfArraysItem is defined correctly");
        requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                "This test requires that ListOfArrays is defined correctly");

        try {
            Object splitList2, splitList3;

            listOfArraysClass = Class.forName("h02.list_of_arrays.ListOfArrays");
            Class<?> listOfArraysItemClass = Class.forName("h02.list_of_arrays.ListOfArraysItem");

            // ListOfArrays instance
            listOfArrays = listOfArraysClass.getDeclaredConstructor().newInstance();

            // ListOfArraysItem instances
            emptyList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            nonNullElement = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            nullElement = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            splitListHead = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            splitList2 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            splitList3 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
            splitListTail = listOfArraysItemClass.getDeclaredConstructor().newInstance();

            // ListOfArraysItem fields
            Field numberOfElements = listOfArraysItemClass.getDeclaredField("numberOfListElemsInArray"),
                  elements = listOfArraysItemClass.getDeclaredField("arrayOfElems"),
                  next = listOfArraysItemClass.getDeclaredField("next"),
                  previous = listOfArraysItemClass.getDeclaredField("previous");

            // max length of all arrays = 2
            Field lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");

            lengthOfAllArrays.setAccessible(true);
            lengthOfAllArrays.set(listOfArrays, 2);

            // emptyList setup
            numberOfElements.set(emptyList, 0);
            elements.set(emptyList, new Integer[2]);

            // nonNullElement setup
            numberOfElements.set(nonNullElement, 1);
            elements.set(nonNullElement, new Integer[]{0, null});

            // nullElement setup
            numberOfElements.set(nullElement, 1);
            elements.set(nullElement, new Integer[]{null, null});

            // split list setup
            numberOfElements.set(splitListHead, 1);
            numberOfElements.set(splitList2, 2);
            numberOfElements.set(splitList3, 0);
            numberOfElements.set(splitListTail, 1);
            elements.set(splitListHead, new Integer[]{0, null});
            elements.set(splitList2, new Integer[]{1, null});
            elements.set(splitList3, new Integer[]{null, null});
            elements.set(splitListTail, new Integer[]{null, null});

            // split list linking
            next.set(splitListHead, splitList2);
            next.set(splitList2, splitList3);
            next.set(splitList3, splitListTail);
            previous.set(splitListTail, splitList3);
            previous.set(splitList3, splitList2);
            previous.set(splitList2, splitListHead);
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            assumeTrue(false);
        }
    }

    /**
     * Sets the head and tail fields in the ListOfArrays instance to the given values and updates the iterator
     * @param head the value to set head to
     * @param tail the value to set tail to
     * @throws ReflectiveOperationException shouldn't happen since all involved classes need to be correct
     * for this method to be called but will be thrown if any step fails
     */
    private void setListOfArrayItems(Object head, Object tail) throws ReflectiveOperationException {
        Field fieldHead = listOfArraysClass.getDeclaredField("head"),
              fieldTail = listOfArraysClass.getDeclaredField("tail");

        fieldHead.setAccessible(true);
        fieldTail.setAccessible(true);

        fieldHead.set(listOfArrays, head);
        fieldTail.set(listOfArrays, tail);

        iterator = (Iterator<?>) listOfArraysClass.getMethod("iterator").invoke(listOfArrays);
    }

    @Test
    public void classDefinitionCorrect() {
        try {
            Class<?> listOfArrayIteratorClass = Class.forName("h02.list_of_arrays.ListOfArraysIterator");

            // is public
            assertTrue(Modifier.isPublic(listOfArrayIteratorClass.getModifiers()), "Class is not public");

            // is generic
            assertEquals(1, listOfArrayIteratorClass.getTypeParameters().length, "Class is not generic");
            assertEquals(
                    "T",
                    listOfArrayIteratorClass.getTypeParameters()[0].getName(),
                    "Type parameter is not named 'T'"
            );

            // implements Iterator<T>
            assertTrue(
                    listOfArrayIteratorClass.getGenericInterfaces().length > 0,
                    "Class doesn't implement any generic interfaces"
            );
            assertTrue(() -> {
                for (Type genericInterface : listOfArrayIteratorClass.getGenericInterfaces())
                    if (((ParameterizedType) genericInterface).getRawType().equals(Iterator.class) &&
                            ((ParameterizedType) genericInterface).getActualTypeArguments()[0].getTypeName().equals("T"))
                        return true;

                return false;
            }, "Class doesn't implement generic interface Iterator<T> correctly");

            // check class isn't abstract
            assertFalse(Modifier.isAbstract(listOfArrayIteratorClass.getModifiers()), "Class mustn't be abstract");
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " could not be found");
        }
    }

    @Nested
    class NextTests {

        public NextTests() {
            requireTest(new ListOfArraysIteratorTest(), "classDefinitionCorrect", "testNext() requires that ListOfArraysIterator is defined correctly");
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect", "testNext() requires that ListOfArrays is defined correctly");
        }

        @SuppressWarnings("unused")
        public void invokeAll() {
            testNoList();
            testEmptyList();
            testOneElementIterator();
            testMultiItemList();
        }

        @Test
        public void testNoList() {
            try {
                setListOfArrayItems(null, null);

                assertThrows(NoSuchElementException.class, iterator::next, "An uninstantiated list can't return any elements");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testEmptyList() {
            try {
                setListOfArrayItems(emptyList, emptyList);

                assertThrows(NoSuchElementException.class, iterator::next, "An empty list can't return any elements");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testOneElementIterator() {
            try {
                setListOfArrayItems(nonNullElement, nonNullElement);

                assertEquals(0, iterator.next(), "Element is not equal to first element in list (should be 0)");
                assertThrows(NoSuchElementException.class, iterator::next, "No more elements according to numberOfListElemsInArray");


                setListOfArrayItems(nullElement, nullElement);

                assertNull(iterator.next(), "Element is not equal to first element in list (should be null)");
                assertThrows(NoSuchElementException.class, iterator::next, "No more elements according to numberOfListElemsInArray");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testMultiItemList() {
            try {
                setListOfArrayItems(splitListHead, splitListTail);

                assertEquals(0, iterator.next(), "Element is not equal to first element in list (should be 0)");
                assertEquals(1, iterator.next(), "Element is not equal to second element in list (should be 1)");
                assertNull(iterator.next(), "Element is not equal to third element in list (should be null)");
                assertNull(iterator.next(), "Element is not equal to fourth element in list (should be null)");
                assertThrows(NoSuchElementException.class, iterator::next, "No more elements according to numberOfListElemsInArray");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Nested
    class HasNextTests {

        public HasNextTests() {
            requireTest(new ListOfArraysIteratorTest(), "classDefinitionCorrect",
                    "testHasNext() requires that ListOfArraysIterator is defined correctly");
            requireTest(new ListOfArraysTest(), "testIterator",
                    "testHasNext() requires that ListOfArrays.iterator() is implemented correctly");
            requireTest(new ListOfArraysIteratorTest().new NextTests(), "invokeAll",
                    "testHasNext() requires that ListOfArraysIterator.next() is implemented correctly");
        }

        @Test
        public void testNoList() {
            try {
                setListOfArrayItems(null, null);

                assertFalse(iterator.hasNext(), "An uninitialized list doesn't any elements");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testEmptyList() {
            try {
                setListOfArrayItems(emptyList, emptyList);

                assertFalse(iterator.hasNext(), "An empty list doesn't any elements");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testOneElementIterator() {
            try {
                setListOfArrayItems(nonNullElement, nonNullElement);

                assertTrue(iterator.hasNext(), "Iterator should have exactly one element left");
                iterator.next();
                assertFalse(iterator.hasNext(), "Iterator shouldn't have any elements left");


                setListOfArrayItems(nullElement, nullElement);

                assertTrue(iterator.hasNext(), "Iterator should have exactly one element left");
                iterator.next();
                assertFalse(iterator.hasNext(), "Iterator shouldn't have any elements left");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testMultiItemList() {
            try {
                setListOfArrayItems(splitListHead, splitListTail);

                assertTrue(iterator.hasNext(), "Iterator should have exactly four elements left"); iterator.next();
                assertTrue(iterator.hasNext(), "Iterator should have exactly three elements left"); iterator.next();
                assertTrue(iterator.hasNext(), "Iterator should have exactly two elements left"); iterator.next();
                assertTrue(iterator.hasNext(), "Iterator should have exactly one element left"); iterator.next();
                assertFalse(iterator.hasNext(), "Iterator shouldn't have any elements left");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Nested
    class UnsupportedOperationsTests {

        Class<?> listOfArraysIteratorClass;

        public UnsupportedOperationsTests() {
            requireTest(new ListOfArraysIteratorTest(), "classDefinitionCorrect",
                    "testUnsupportedOperations() requires that ListOfArraysIterator is defined correctly");
            requireTest(new ListOfArraysTest(), "testIterator",
                    "testUnsupportedOperations() requires that ListOfArrays.iterator() is implemented correctly");

            try {
                setListOfArrayItems(null, null);

                listOfArraysIteratorClass = Class.forName("h02.list_of_arrays.ListOfArraysIterator");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testForEachRemaining() {
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> getActualException(listOfArraysIteratorClass.getMethod("forEachRemaining", Consumer.class), iterator, (Object) null),
                    "ListOfArraysIterator#forEachRemaining(Consumer) must only throw an UnsupportedOperationException on invocation"
            );
        }

        @Test
        public void testRemove() {
            assertThrows(
                    UnsupportedOperationException.class,
                    () -> getActualException(listOfArraysIteratorClass.getMethod("remove"), iterator),
                    "ListOfArraysIterator#remove() must only throw an UnsupportedOperationException on invocation"
            );
        }
    }
}
