package h02.list_of_arrays;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

import static h02.Utils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class ListOfArraysTest {

    private static Class<?> listOfArraysClass, listOfArraysItemClass;
    private static Field numberOfElements, elements, next, previous;

    public ListOfArraysTest() {
        requireTest(new ListOfArraysItemTest(), "classDefinitionCorrect",
                "This test requires that ListOfArraysItem is defined correctly");

        try {
            listOfArraysClass = Class.forName("h02.list_of_arrays.ListOfArrays");
            listOfArraysItemClass = Class.forName("h02.list_of_arrays.ListOfArraysItem");

            numberOfElements = listOfArraysItemClass.getDeclaredField("numberOfListElemsInArray");
            elements = listOfArraysItemClass.getDeclaredField("arrayOfElems");
            next = listOfArraysItemClass.getDeclaredField("next");
            previous = listOfArraysItemClass.getDeclaredField("previous");
        } catch (ClassNotFoundException e) {
            assumeTrue(false, "Class " + e.getMessage() + " not found");
        } catch (ReflectiveOperationException e) {
            fail(e);
        }
    }

    /**
     * Sets the head and tail fields in the ListOfArrays instance to the given values
     * @param instance an instance of ListOfArrays to set the fields in
     * @param head     the value to set head to
     * @param tail     the value to set tail to
     * @throws ReflectiveOperationException shouldn't happen since all involved classes need to be correct
     * for this method to be called but will be thrown if any step fails
     */
    private void setListOfArrayItems(Object instance, Object head, Object tail) throws ReflectiveOperationException {
        Field fieldHead = listOfArraysClass.getDeclaredField("head"),
              fieldTail = listOfArraysClass.getDeclaredField("tail");

        fieldHead.setAccessible(true);
        fieldTail.setAccessible(true);

        fieldHead.set(instance, head);
        fieldTail.set(instance, tail);
    }

    @Test
    public void classDefinitionCorrect() {
        try {
            // is public
            assertTrue(Modifier.isPublic(listOfArraysClass.getModifiers()));

            // is generic
            assertEquals(1, listOfArraysClass.getTypeParameters().length, "Class is not generic");
            assertEquals(
                    "T",
                    listOfArraysClass.getTypeParameters()[0].getName(),
                    "Type parameter is not named 'T'"
            );

            // constructor
            assertTrue(() -> {
                try {
                    return listOfArraysClass.getDeclaredConstructor().getParameterCount() == 0;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }, "Class doesn't have an empty constructor");

            Object instance = listOfArraysClass.getDeclaredConstructor().newInstance();

            // implements List<T>
            assertTrue(
                    listOfArraysClass.getGenericInterfaces().length > 0,
                    "Class doesn't implement any generic interfaces"
            );
            assertTrue(() -> {
                for (Type genericInterface : listOfArraysClass.getGenericInterfaces())
                    if (((ParameterizedType) genericInterface).getRawType().equals(List.class) &&
                            ((ParameterizedType) genericInterface).getActualTypeArguments()[0].getTypeName().equals("T"))
                        return true;

                return false;
            }, "Class doesn't implement generic interface List<T> correctly");

            // check class isn't abstract
            assertFalse(Modifier.isAbstract(listOfArraysClass.getModifiers()), "Class mustn't be abstract");

            // fields
            try {
                Field LENGTH_OF_ALL_ARRAYS = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS"),
                      DEFAULT_LENGTH_OF_ALL_ARRAYS = listOfArraysClass.getDeclaredField("DEFAULT_LENGTH_OF_ALL_ARRAYS"),
                      head = listOfArraysClass.getDeclaredField("head"),
                      tail = listOfArraysClass.getDeclaredField("tail");

                // LENGTH_OF_ALL_ARRAYS
                assertTrue(Modifier.isPrivate(LENGTH_OF_ALL_ARRAYS.getModifiers()));
                assertEquals(int.class, LENGTH_OF_ALL_ARRAYS.getType());

                LENGTH_OF_ALL_ARRAYS.setAccessible(true);
                assertEquals(256, LENGTH_OF_ALL_ARRAYS.get(instance));

                // DEFAULT_LENGTH_OF_ALL_ARRAYS
                assertTrue(Modifier.isPrivate(DEFAULT_LENGTH_OF_ALL_ARRAYS.getModifiers()));
                assertTrue(Modifier.isStatic(DEFAULT_LENGTH_OF_ALL_ARRAYS.getModifiers()));
                assertTrue(Modifier.isFinal(DEFAULT_LENGTH_OF_ALL_ARRAYS.getModifiers()));
                assertEquals(int.class, DEFAULT_LENGTH_OF_ALL_ARRAYS.getType());

                DEFAULT_LENGTH_OF_ALL_ARRAYS.setAccessible(true);
                assertEquals(256, DEFAULT_LENGTH_OF_ALL_ARRAYS.get(null));

                // head
                assertTrue(Modifier.isPrivate(head.getModifiers()));
                assertEquals(listOfArraysItemClass, head.getType());

                head.setAccessible(true);
                assertNull(head.get(instance));

                // tail
                assertTrue(Modifier.isPrivate(tail.getModifiers()));
                assertEquals(listOfArraysItemClass, tail.getType());

                tail.setAccessible(true);
                assertNull(tail.get(instance));
            } catch (NoSuchFieldException e) {
                fail("Required field not found", e);
            }

            // readArrayLength(String)
            assertTrue(() -> {
                try {
                    Method method = listOfArraysClass.getMethod("readArrayLength", String.class);
                    boolean hasIOException = false, hasNegativeArraySizeException = false;

                    for (Class<?> exceptionClass : method.getExceptionTypes())
                        if (!hasIOException && exceptionClass.equals(IOException.class))
                            hasIOException = true;
                        else if (!hasNegativeArraySizeException && exceptionClass.equals(NegativeArraySizeException.class))
                            hasNegativeArraySizeException = true;

                    return method.getReturnType().equals(void.class) &&
                            Modifier.isPublic(method.getModifiers()) &&
                            hasIOException &&
                            hasNegativeArraySizeException;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }, "Method 'readArrayLength' is not declared correctly");

            // other methods inherited from List<T> must all throw an UnsupportedOperationException
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("add", int.class, Object.class), instance, 0, null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("addAll", int.class, Collection.class), instance, 0, null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("clear"), instance));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("containsAll", Collection.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("indexOf", Object.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("isEmpty"), instance));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("lastIndexOf", Object.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("listIterator"), instance));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("listIterator", int.class), instance, 0));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("remove", int.class), instance, 0));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("remove", Object.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("removeAll", Collection.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("retainAll", Collection.class), instance, (Object) null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("set", int.class, Object.class), instance, 0, null));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("size"), instance));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("subList", int.class, int.class), instance, 0, 0));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("toArray"), instance));
            // assertThrows(UnsupportedOperationException.class, () -> getActualException(listOfArraysClass.getMethod("toArray", Object[].class), instance, new Object[]{new Object[0]}));
        } catch (ReflectiveOperationException e) {
            fail("Class ListOfArrays is not defined correctly", e);
        }
    }

    @Nested
    class ReadArrayLengthTests {

        Method readArrayLength;
        Object instance;

        public ReadArrayLengthTests() {
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                    "ReadArrayLengthTests requires that ListOfArrays is defined correctly");

            try {
                readArrayLength = listOfArraysClass.getMethod("readArrayLength", String.class);
                instance = listOfArraysClass.getDeclaredConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                "hello world!\n",
                "282.298\n",
                "https://oshgnacknak.de",
                "TDD ist toll!\n",
                "\n123"
        })
        public void testIOException(String content) throws IOException {
            withTempFile(content, path -> assertThrows(
                    IOException.class,
                    () -> getActualException(readArrayLength, instance, path.toString()),
                    "Did not throw IOException on invalid string"
            ));
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "-123",
                "-424",
                "-1"
        })
        public void testNegativeArraySizeException(String content) throws IOException {
            withTempFile(content, path -> assertThrows(
                    NegativeArraySizeException.class,
                    () -> getActualException(readArrayLength, instance, path.toString()),
                    "Did not throw NegativeArraySizeException on negative integer"
            ));
        }

        @ParameterizedTest
        @MethodSource("h02.Utils#provideRandomIntArguments")
        public void testRandomValidNumber(String content, int expected) throws IOException {
            withTempFile(content, path -> {
                try {
                    Field lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");

                    lengthOfAllArrays.setAccessible(true);

                    assertDoesNotThrow(() -> readArrayLength.invoke(instance, path.toString()), "Threw exception on valid number");
                    assertEquals(expected, lengthOfAllArrays.get(instance), "LENGTH_OF_ALL_ARRAYS not set to expected value");
                    assertDoesNotThrow(() -> new FileReader(path.toString()).close(), "File was not closed after reading");
                } catch (ReflectiveOperationException e) {
                    fail(e);
                }
            });
        }
    }

    @Nested
    class ContainsTests {

        private Method contains;
        private Object instance;

        /*
         * emptyList:  (0, { | null, null})
         * singleList: (2, {1, null | })
         * hiddenList: (0, { | 1, null})
         * splitList:  (1, {1 | 2}) <-> (0, { | null, null}) <-> (2, {null, 3 | })
         */
        private Object emptyList, singleList, hiddenList, splitListHead, splitListTail;

        public ContainsTests() {
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                    "ContainsTests requires that ListOfArrays is defined correctly");

            try {
                contains = listOfArraysClass.getMethod("contains", Object.class);

                instance = listOfArraysClass.getDeclaredConstructor().newInstance();

                emptyList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                hiddenList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListHead = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                Object splitListMid = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListTail = listOfArraysItemClass.getDeclaredConstructor().newInstance();

                Field lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");

                lengthOfAllArrays.setAccessible(true);
                lengthOfAllArrays.set(instance, 2);

                // empty list
                numberOfElements.set(emptyList, 0);
                elements.set(emptyList, new Integer[2]);

                // single array list
                numberOfElements.set(singleList, 2);
                elements.set(singleList, new Integer[] {1, null});

                // single array list with hidden elements
                numberOfElements.set(hiddenList, 0);
                elements.set(hiddenList, new Integer[] {1, null});

                // split list
                numberOfElements.set(splitListHead, 1);
                numberOfElements.set(splitListMid, 0);
                numberOfElements.set(splitListTail, 2);
                elements.set(splitListHead, new Integer[] {1, 2});
                elements.set(splitListMid, new Integer[2]);
                elements.set(splitListTail, new Integer[] {null, 3});

                // split list linking (1, {1, 2}) <-> (0, {null, null}) <-> (2, {null, 3})
                next.set(splitListHead, splitListMid);
                next.set(splitListMid, splitListTail);
                previous.set(splitListTail, splitListMid);
                previous.set(splitListMid, splitListHead);
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testNoList() {
            try {
                setListOfArrayItems(instance, null, null);

                assertFalse((Boolean) contains.invoke(instance, 1), "Did not return false when head and tail are null");
                assertFalse((Boolean) contains.invoke(instance, (Object) null), "Did not return false when head and tail are null");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testEmptyList() {
            try {
                setListOfArrayItems(instance, emptyList, emptyList);

                assertFalse((Boolean) contains.invoke(instance, 1), "Did not return false when list is empty");
                assertFalse((Boolean) contains.invoke(instance, (Object) null), "Did not return false when list is empty");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testSingleArrayList() {
            try {
                setListOfArrayItems(instance, singleList, singleList);

                assertTrue((Boolean) contains.invoke(instance, 1), "Did not return true when element is in list");
                assertTrue((Boolean) contains.invoke(instance, (Object) null), "Did not return true when element is in list");
                assertFalse((Boolean) contains.invoke(instance, 10), "Did not return false when element is not in list");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testHiddenElements() {
            try {
                setListOfArrayItems(instance, hiddenList, hiddenList);

                assertFalse((Boolean) contains.invoke(instance, 1), "Did not return false when element is in array but at index >= numberOfListElemsInArray");
                assertFalse((Boolean) contains.invoke(instance, (Object) null), "Did not return false when element is in array but at index >= numberOfListElemsInArray");
                assertFalse((Boolean) contains.invoke(instance, 10), "Did not return false when element is not in array");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testMultiItemList() {
            try {
                setListOfArrayItems(instance, splitListHead, splitListTail);

                assertTrue((Boolean) contains.invoke(instance, 1), "Did not return true when element is in list (first array)");
                assertFalse(
                        (Boolean) contains.invoke(instance, 2),
                        "Did not return false when element is in array but at index >= numberOfListElemsInArray (first array)"
                );
                assertTrue((Boolean) contains.invoke(instance, (Object) null), "Did not return true when element is in list (third array)");
                assertTrue((Boolean) contains.invoke(instance, 3), "Did not return true when element is in list (third array)");
                assertFalse((Boolean) contains.invoke(instance, 10), "Did not return false when element is not in list");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Nested
    class AddTests {

        Method add;
        Object instanceEven, instanceOdd;
        Field lengthOfAllArrays, head, tail;

        /*
         * singleList1:   (1, {0 | null})
         * singleList2:   (1, {0 | null})
         * singleList3:   (2, {0, 1 | })
         * singleList4:   (2, {0, null | })
         * singleListOdd: (7, {0, 1, 2, 3, 4, 5, 6 | })
         * splitList:     (1, {0 | null}) <-> (0, { | null, null})
         */
        Object singleList1, singleList2, singleList3, singleList4, singleListOdd, splitListHead, splitListTail;

        public AddTests() {
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                    "testAdd() requires that ListOfArrays is defined correctly");

            try {
                add = listOfArraysClass.getMethod("add", Object.class);

                instanceEven = listOfArraysClass.getDeclaredConstructor().newInstance();
                instanceOdd = listOfArraysClass.getDeclaredConstructor().newInstance();

                singleList1 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList2 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList3 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList4 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleListOdd = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListHead = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListTail = listOfArraysItemClass.getDeclaredConstructor().newInstance();

                lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");
                head = listOfArraysClass.getDeclaredField("head");
                tail = listOfArraysClass.getDeclaredField("tail");

                lengthOfAllArrays.setAccessible(true);
                head.setAccessible(true);
                tail.setAccessible(true);

                lengthOfAllArrays.set(instanceEven, 2);
                lengthOfAllArrays.set(instanceOdd, 7);

                // single lists
                numberOfElements.set(singleList1, 1);
                numberOfElements.set(singleList2, 1);
                numberOfElements.set(singleList3, 2);
                numberOfElements.set(singleList4, 2);
                numberOfElements.set(singleListOdd, 7);
                elements.set(singleList1, new Integer[]{0, null});
                elements.set(singleList2, new Integer[]{0, null});
                elements.set(singleList3, new Integer[]{0, 1});
                elements.set(singleList4, new Integer[]{0, null});
                elements.set(singleListOdd, new Integer[]{0, 1, 2, 3, 4, 5, 6});

                // split list
                numberOfElements.set(splitListHead, 1);
                numberOfElements.set(splitListTail, 0);
                elements.set(splitListHead, new Integer[]{0, null});
                elements.set(splitListTail, new Integer[]{null, null});

                // split list linking (1, {0, null}) <-> (0, {null, null})
                next.set(splitListHead, splitListTail);
                previous.set(splitListTail, splitListHead);
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testNoList() {
            try {
                setListOfArrayItems(instanceEven, null, null);

                assertTrue((Boolean) add.invoke(instanceEven, 0), "Did not return true on invoking add with valid element (integer)");
                assertNotNull(head.get(instanceEven), "Did not initialize list (head)");
                assertNotNull(tail.get(instanceEven), "Did not initialize list (tail)");
                assertSame(head.get(instanceEven), tail.get(instanceEven), "Did not initialize list correctly (should be head == tail)");
                assertEquals(1, numberOfElements.get(tail.get(instanceEven)), "Did not increment numberOfListElemsInArray");
                assertEquals(2, ((Object[]) elements.get(tail.get(instanceEven))).length, "Did not initialize array in list item correctly");
                assertEquals(0, ((Object[]) elements.get(tail.get(instanceEven)))[0], "First element in list is not the one added (should be 0)");

                // head: (1, {0 | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instanceEven, null, null);

                assertTrue((Boolean) add.invoke(instanceEven, (Object) null), "Did not return true on invoking add with valid element (null)");
                assertNotNull(head.get(instanceEven), "Did not initialize list (head)");
                assertNotNull(tail.get(instanceEven), "Did not initialize list (tail)");
                assertSame(head.get(instanceEven), tail.get(instanceEven), "Did not initialize list correctly (should be head == tail)");
                assertEquals(1, numberOfElements.get(tail.get(instanceEven)), "Did not increment numberOfListElemsInArray");
                assertEquals(2, ((Object[]) elements.get(tail.get(instanceEven))).length, "Did not initialize array in list item correctly");
                assertNull(((Object[]) elements.get(tail.get(instanceEven)))[0], "First element in list is not the one added (should be null)");

                // head: (1, {null | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testSingleArrayList() {
            try {
                setListOfArrayItems(instanceEven, singleList1, singleList1);

                assertTrue((Boolean) add.invoke(instanceEven, 1), "Did not return true on invoking add with valid element (integer)");
                assertEquals(2, numberOfElements.get(singleList1), "Did not increment numberOfListElemsInArray");
                assertEquals(0, ((Object[]) elements.get(singleList1))[0], "First element in list is not the one already there (0)");
                assertEquals(1, ((Object[]) elements.get(singleList1))[1], "Second element in list is not the one added (1)");

                // head: (2, {0, 1 | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instanceEven, singleList2, singleList2);

                assertTrue((Boolean) add.invoke(instanceEven, (Object) null), "Did not return true on invoking add with valid element (null)");
                assertEquals(2, numberOfElements.get(singleList2), "Did not increment numberOfListElemsInArray");
                assertEquals(0, ((Object[]) elements.get(singleList2))[0], "First element in list is not the one already there (0)");
                assertNull(((Object[]) elements.get(singleList2))[1], "Second element in list is not the one added (null)");

                // head: (2, {0, null | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testArrayFull() {
            try {
                setListOfArrayItems(instanceEven, singleList3, singleList3);

                assertTrue((Boolean) add.invoke(instanceEven, 2), "Did not return true on invoking add with valid element (2)");
                assertSame(singleList3, head.get(instanceEven), "head is not the same object as before");
                assertNotSame(head.get(instanceEven), tail.get(instanceEven), "head and tail are still the same object");
                assertSame(next.get(singleList3), tail.get(instanceEven), "Field 'next' of head is not referencing the new tail");
                assertSame(singleList3, previous.get(tail.get(instanceEven)), "Field 'previous' of tail is not referencing head");

                Object list3Head = singleList3, list3Tail = tail.get(instanceEven);

                assertEquals(0, ((Object[]) elements.get(list3Head))[0]);
                assertNull(((Object[]) elements.get(list3Head))[1]);
                assertEquals(1, ((Object[]) elements.get(list3Tail))[0]);
                assertEquals(2, ((Object[]) elements.get(list3Tail))[1]);
                assertEquals(1, numberOfElements.get(list3Head));
                assertEquals(2, numberOfElements.get(list3Tail));

                // head: (1, {0 | null}) <-> (2, {1, 2 | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instanceEven, singleList4, singleList4);

                assertTrue((Boolean) add.invoke(instanceEven, (Object) null), "Did not return true on invoking add with valid element (null)");
                assertSame(singleList4, head.get(instanceEven), "head is not the same object as before");
                assertNotSame(head.get(instanceEven), tail.get(instanceEven), "head and tail are still the same object");
                assertSame(next.get(singleList4), tail.get(instanceEven), "Field 'next' of head is not referencing the new tail");
                assertSame(singleList4, previous.get(tail.get(instanceEven)), "Field 'previous' of tail is not referencing head");

                Object list4Head = singleList4, list4Tail = tail.get(instanceEven);

                assertEquals(0, ((Object[]) elements.get(list4Head))[0]);
                assertNull(((Object[]) elements.get(list4Head))[1]);
                assertNull(((Object[]) elements.get(list4Tail))[0]);
                assertNull(((Object[]) elements.get(list4Tail))[1]);
                assertEquals(1, numberOfElements.get(list4Head));
                assertEquals(2, numberOfElements.get(list4Tail));

                // head: (1, {0 | null}) <-> (2, {null, null | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testMultiItemList() {
            try {
                setListOfArrayItems(instanceEven, splitListHead, splitListTail);

                assertTrue((Boolean) add.invoke(instanceEven, 1), "Did not return true on invoking add with valid element (1)");
                assertEquals(1, numberOfElements.get(splitListHead),
                        "Number of elements in first list item was changed (perhaps something was added to the first list item?)");
                assertEquals(1, numberOfElements.get(splitListTail), "Number of elements in second list item was not incremented");
                assertEquals(0, ((Object[]) elements.get(splitListHead))[0]);
                assertNull(((Object[]) elements.get(splitListHead))[1]);
                assertEquals(1, ((Object[]) elements.get(splitListTail))[0]);
                assertNull(((Object[]) elements.get(splitListTail))[1]);

                assertTrue((Boolean) add.invoke(instanceEven, (Object) null), "Did not return true on invoking add with valid element (null)");
                assertEquals(1, numberOfElements.get(splitListHead),
                        "Number of elements in first list item was changed (perhaps something was added to the first list item?)");
                assertEquals(2, numberOfElements.get(splitListTail), "Number of elements in second list item was not incremented");
                assertEquals(0, ((Object[]) elements.get(splitListHead))[0]);
                assertNull(((Object[]) elements.get(splitListHead))[1]);
                assertEquals(1, ((Object[]) elements.get(splitListTail))[0]);
                assertNull(((Object[]) elements.get(splitListTail))[1]);

                // head: (1, {0 | null}) <-> (2, {1, null | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testOddArrayLength() {
            try {
                setListOfArrayItems(instanceOdd, singleListOdd, singleListOdd);
                boolean variant1, variant2;

                assertTrue((Boolean) add.invoke(instanceOdd, 7));

                variant1 = numberOfElements.getInt(head.get(instanceOdd)) == 3 && numberOfElements.getInt(tail.get(instanceOdd)) == 5;
                variant2 = numberOfElements.getInt(head.get(instanceOdd)) == 4 && numberOfElements.getInt(tail.get(instanceOdd)) == 4;
                assertTrue(
                        variant1 || variant2,
                        "Since LENGTH_OF_ALL_ARRAYS is odd the amount of elements in these list items must either " +
                                "be Math.floor(<length of array> / 2) in head and Math.floor(<length of array> / 2) + 1 + " +
                                "the added element in tail, or Math.floor(<length of array> / 2) + 1 in head and " +
                                "Math.floor(<length of array> / 2) + the element, assuming the list has been split evenly " +
                                "(in this case 3 and 5, or 4 and 4)"
                );

                if (variant1) {
                    assertEquals(0, ((Object[]) elements.get(head.get(instanceOdd)))[0]);
                    assertEquals(1, ((Object[]) elements.get(head.get(instanceOdd)))[1]);
                    assertEquals(2, ((Object[]) elements.get(head.get(instanceOdd)))[2]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[3]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[4]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[5]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[6]);

                    assertEquals(3, ((Object[]) elements.get(tail.get(instanceOdd)))[0]);
                    assertEquals(4, ((Object[]) elements.get(tail.get(instanceOdd)))[1]);
                    assertEquals(5, ((Object[]) elements.get(tail.get(instanceOdd)))[2]);
                    assertEquals(6, ((Object[]) elements.get(tail.get(instanceOdd)))[3]);
                    assertEquals(7, ((Object[]) elements.get(tail.get(instanceOdd)))[4]);
                    assertNull(((Object[]) elements.get(tail.get(instanceOdd)))[5]);
                    assertNull(((Object[]) elements.get(tail.get(instanceOdd)))[6]);
                } else {
                    assertEquals(0, ((Object[]) elements.get(head.get(instanceOdd)))[0]);
                    assertEquals(1, ((Object[]) elements.get(head.get(instanceOdd)))[1]);
                    assertEquals(2, ((Object[]) elements.get(head.get(instanceOdd)))[2]);
                    assertEquals(3, ((Object[]) elements.get(head.get(instanceOdd)))[3]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[4]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[5]);
                    assertNull(((Object[]) elements.get(head.get(instanceOdd)))[6]);

                    assertEquals(4, ((Object[]) elements.get(tail.get(instanceOdd)))[0]);
                    assertEquals(5, ((Object[]) elements.get(tail.get(instanceOdd)))[1]);
                    assertEquals(6, ((Object[]) elements.get(tail.get(instanceOdd)))[2]);
                    assertEquals(7, ((Object[]) elements.get(tail.get(instanceOdd)))[3]);
                    assertNull(((Object[]) elements.get(tail.get(instanceOdd)))[4]);
                    assertNull(((Object[]) elements.get(tail.get(instanceOdd)))[5]);
                    assertNull(((Object[]) elements.get(tail.get(instanceOdd)))[6]);
                }
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Nested
    class AddAllTests {

        private Method addAll;
        private Object instance;
        private Field head, tail;

        /*
         * emptyList1:  (0, { | null, null})
         * emptyList2:  (0, { | null, null})
         * singleList1: (1, {0 | null})
         * singleList2: (1, {null | null})
         * splitList:   (2, {0, 1 | })
         */
        private Object emptyList1, emptyList2, singleList1, singleList2, splitListHead;

        public AddAllTests() {
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                    "AddAllTests requires that ListOfArrays is defined correctly");

            try {
                addAll = listOfArraysClass.getMethod("addAll", Collection.class);

                instance = listOfArraysClass.getDeclaredConstructor().newInstance();

                emptyList1 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                emptyList2 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList1 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList2 = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListHead = listOfArraysItemClass.getDeclaredConstructor().newInstance();

                Field lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");
                head = listOfArraysClass.getDeclaredField("head");
                tail = listOfArraysClass.getDeclaredField("tail");

                lengthOfAllArrays.setAccessible(true);
                head.setAccessible(true);
                tail.setAccessible(true);

                lengthOfAllArrays.set(instance, 2);

                // empty lists
                numberOfElements.set(emptyList1, 0);
                numberOfElements.set(emptyList2, 0);
                elements.set(emptyList1, new Object[2]);
                elements.set(emptyList2, new Object[2]);

                // single array lists
                numberOfElements.set(singleList1, 1);
                numberOfElements.set(singleList2, 1);
                elements.set(singleList1, new Object[] {0, null});
                elements.set(singleList2, new Object[2]);

                // split list
                numberOfElements.set(splitListHead, 2);
                elements.set(splitListHead, new Object[] {0, 1});
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testNullCollection() {
            try {
                setListOfArrayItems(instance, null, null);

                assertThrows(
                        NullPointerException.class,
                        () -> getActualException(addAll, instance, (Object) null),
                        "addAll(Collection) must throw a NullPointerException if the given collection is null"
                );
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testNoList() {
            try {
                setListOfArrayItems(instance, null, null);

                assertTrue((Boolean) addAll.invoke(instance, List.of(0)), "Did not return true on invoking addAll with valid collection ({0})");
                assertNotNull(head.get(instance), "Did not initialize list (head)");
                assertNotNull(tail.get(instance), "Did not initialize list (tail)");
                assertSame(head.get(instance), tail.get(instance), "Did not initialize list correctly (should be head == tail)");
                assertEquals(1, numberOfElements.get(tail.get(instance)), "Did not increment numberOfListElemsInArray");
                assertEquals(2, ((Object[]) elements.get(tail.get(instance))).length, "Did not initialize array in list item correctly");
                assertEquals(0, ((Object[]) elements.get(tail.get(instance)))[0], "First element in list is not the one added (should be 0)");

                // head: (1, {0 | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instance, null, null);

                assertTrue((Boolean) addAll.invoke(instance, new ArrayList<>() {{add(null);}}),
                        "Did not return true on invoking addAll with valid collection ({null})");
                assertNotNull(head.get(instance), "Did not initialize list (head)");
                assertNotNull(tail.get(instance), "Did not initialize list (tail)");
                assertSame(head.get(instance), tail.get(instance), "Did not initialize list correctly (should be head == tail)");
                assertEquals(1, numberOfElements.get(tail.get(instance)), "Did not increment numberOfListElemsInArray");
                assertEquals(2, ((Object[]) elements.get(tail.get(instance))).length, "Did not initialize array in list item correctly");
                assertNull(((Object[]) elements.get(tail.get(instance)))[0], "First element in list is not the one added (should be null)");

                // head: (1, {null | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instance, null, null);

                assertTrue((Boolean) addAll.invoke(instance, new ArrayList<>() {{add(null); add(1); add(-3);}}),
                        "Did not return true on invoking addAll with valid collection ({null, 1, -3})");
                assertNotNull(head.get(instance), "Did not initialize list (head)");
                assertNotNull(tail.get(instance), "Did not initialize list (tail)");
                assertNotSame(head.get(instance), tail.get(instance), "Did not initialize list correctly (should be head != tail)");
                assertEquals(2, numberOfElements.get(head.get(instance)), "Did not set numberOfListElemsInArray to correct value");
                assertEquals(1, numberOfElements.get(tail.get(instance)), "Did not set numberOfListElemsInArray to correct value");
                assertEquals(2, ((Object[]) elements.get(head.get(instance))).length, "Did not initialize array in list item correctly");
                assertEquals(2, ((Object[]) elements.get(tail.get(instance))).length, "Did not initialize array in list item correctly");
                assertNull(((Object[]) elements.get(head.get(instance)))[0], "First element in list is not the one added (should be null)");
                assertEquals(1, ((Object[]) elements.get(head.get(instance)))[1], "Second element in list is not the one added (should be 1)");
                assertEquals(-3, ((Object[]) elements.get(tail.get(instance)))[0], "Third element in list is not the one added (should be -3)");

                // head: (2, {null, 1 | }) <-> (1, {-3 | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testEmptyList() {
            try {
                setListOfArrayItems(instance, emptyList1, emptyList1);

                assertFalse((Boolean) addAll.invoke(instance, List.of()), "Should return false since the list wasn't modified");
                assertEquals(0, numberOfElements.get(emptyList1), "Should still be 0 since the list wasn't modified");
                assertNull(((Object[]) elements.get(emptyList1))[0]);
                assertNull(((Object[]) elements.get(emptyList1))[1]);

                // head: (0, { | null, null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instance, emptyList1, emptyList1);

                assertTrue((Boolean) addAll.invoke(instance, List.of(0, 1)), "Did not return true on invoking addAll with valid collection ({0, 1})");
                assertEquals(2, numberOfElements.get(emptyList1), "Did not set numberOfListElemsInArray to correct value");
                assertEquals(0, ((Object[]) elements.get(emptyList1))[0]);
                assertEquals(1, ((Object[]) elements.get(emptyList1))[1]);

                // head: (2, {0, 1 | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instance, emptyList2, emptyList2);

                assertTrue((Boolean) addAll.invoke(instance, new ArrayList<>() {{add(null); add(null);}}),
                        "Did not return true on invoking addAll with valid collection ({null, null})");
                assertEquals(2, numberOfElements.get(emptyList2), "Did not set numberOfListElemsInArray to correct value");
                assertNull(((Object[]) elements.get(emptyList2))[0]);
                assertNull(((Object[]) elements.get(emptyList2))[1]);

                // head: (2, {null, null | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testSingleArrayList() {
            try {
                setListOfArrayItems(instance, singleList1, singleList1);

                assertTrue((Boolean) addAll.invoke(instance, List.of(1)),
                        "Did not return true on invoking addAll with valid collection ({1})");
                assertEquals(2, numberOfElements.get(singleList1), "Did not increment numberOfListElemsInArray");
                assertEquals(0, ((Object[]) elements.get(singleList1))[0]);
                assertEquals(1, ((Object[]) elements.get(singleList1))[1]);
                assertNull(next.get(singleList1), "Array is not full, no need to create a new ListOfArraysItem");

                // head: (2, {0, 1 | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }

            try {
                setListOfArrayItems(instance, singleList2, singleList2);

                assertTrue((Boolean) addAll.invoke(instance, new ArrayList<>() {{add(null);}}),
                        "Did not return true on invoking addAll with valid collection ({null})");
                assertEquals(2, numberOfElements.get(singleList2), "Did not increment numberOfListElemsInArray");
                assertNull(((Object[]) elements.get(singleList2))[0]);
                assertNull(((Object[]) elements.get(singleList2))[1]);
                assertNull(next.get(singleList2), "Array is not full, no need to create a new ListOfArraysItem");

                // head: (2, {null, null | })
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testArrayFull() {
            try {
                setListOfArrayItems(instance, splitListHead, splitListHead);

                assertTrue((Boolean) addAll.invoke(instance, List.of(2, 3, 4)),
                        "Did not return true on invoking addAll with valid collection ({2, 3, 4})");
                assertSame(splitListHead, head.get(instance));
                assertNotSame(splitListHead, tail.get(instance));
                assertNotNull(next.get(splitListHead));
                assertNotNull(next.get(next.get(splitListHead)));
                assertNull(next.get(next.get(next.get(splitListHead))));
                assertNull(previous.get(splitListHead));
                assertNotNull(previous.get(next.get(splitListHead)));
                assertNotNull(previous.get(next.get(next.get(splitListHead))));
                assertSame(splitListHead, previous.get(next.get(splitListHead)));

                Object splitListMid = next.get(splitListHead), splitListTail = next.get(splitListMid);

                assertEquals(2, numberOfElements.get(splitListHead));
                assertEquals(2, numberOfElements.get(splitListMid));
                assertEquals(1, numberOfElements.get(splitListTail));
                assertEquals(0, ((Object[]) elements.get(splitListHead))[0]);
                assertEquals(1, ((Object[]) elements.get(splitListHead))[1]);
                assertEquals(2, ((Object[]) elements.get(splitListMid))[0]);
                assertEquals(3, ((Object[]) elements.get(splitListMid))[1]);
                assertEquals(4, ((Object[]) elements.get(splitListTail))[0]);
                assertNull(((Object[]) elements.get(splitListTail))[1]);

                // head: (2, {0, 1 | }) <-> (2, {2, 3 | }) <-> (1, {4 | null})
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Nested
    class GetTests {

        private Method get;
        private Object instance;

        /*
         * emptyList:  (0, { | null, null})
         * singleList: (2, {0, null | })
         * splitList:  (2, {0, null |}) <-> (0, { | null, null}) <-> (2, {1, null | })
         */
        private Object emptyList, singleList, splitListHead, splitListTail;

        public GetTests() {
            requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                    "GetTests requires that ListOfArrays is defined correctly");

            try {
                get = listOfArraysClass.getMethod("get", int.class);

                instance = listOfArraysClass.getDeclaredConstructor().newInstance();

                emptyList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                singleList = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListHead = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                Object splitListMid = listOfArraysItemClass.getDeclaredConstructor().newInstance();
                splitListTail = listOfArraysItemClass.getDeclaredConstructor().newInstance();

                Field lengthOfAllArrays = listOfArraysClass.getDeclaredField("LENGTH_OF_ALL_ARRAYS");
                lengthOfAllArrays.setAccessible(true);
                lengthOfAllArrays.set(instance, 2);

                // empty list
                numberOfElements.set(emptyList, 0);
                elements.set(emptyList, new Integer[2]);

                // single array list
                numberOfElements.set(singleList, 2);
                elements.set(singleList, new Integer[] {0, null});

                // split list
                numberOfElements.set(splitListHead, 2);
                numberOfElements.set(splitListMid, 0);
                numberOfElements.set(splitListTail, 2);
                elements.set(splitListHead, new Integer[] {0, null});
                elements.set(splitListMid, new Integer[2]);
                elements.set(splitListTail, new Integer[] {1, null});

                // split list linking (2, {0, null}) <-> (0, {null, null}) <-> (2, {1, null})
                next.set(splitListHead, splitListMid);
                next.set(splitListMid, splitListTail);
                previous.set(splitListTail, splitListMid);
                previous.set(splitListMid, splitListHead);
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testBadIndices() {
            try {
                setListOfArrayItems(instance, singleList, singleList);

                assertThrows(IndexOutOfBoundsException.class, () -> getActualException(get, instance, -1), "Negative numbers are not valid indices");
                assertThrows(IndexOutOfBoundsException.class, () -> getActualException(get, instance, 2),
                        "2 is not a valid index for a 2-element list (indices start at 0)");
                assertThrows(IndexOutOfBoundsException.class, () -> getActualException(get, instance, 10), "10 is out of range for a 2-element list");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testEmptyList() {
            try {
                setListOfArrayItems(instance, emptyList, emptyList);

                assertThrows(IndexOutOfBoundsException.class, () -> getActualException(get, instance, 0), "0 is not a valid index for a 0-element list");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testSingleArrayList() {
            try {
                setListOfArrayItems(instance, singleList, singleList);

                assertEquals(0, get.invoke(instance, 0));
                assertNull(get.invoke(instance, 1));
                assertEquals(0, get.invoke(instance, 0), "Result is not consistent");
                assertNull(get.invoke(instance, 1), "Result is not consistent");
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }

        @Test
        public void testMultiItemList() {
            try {
                setListOfArrayItems(instance, splitListHead, splitListTail);

                assertEquals(0, get.invoke(instance, 0));
                assertNull(get.invoke(instance, 1));
                assertEquals(1, get.invoke(instance, 2), "Middle list item doesn't have any items, must be skipped");
                assertNull(get.invoke(instance, 3));
            } catch (ReflectiveOperationException e) {
                fail(e);
            }
        }
    }

    @Test
    public void testIterator() {
        requireTest(new ListOfArraysTest(), "classDefinitionCorrect",
                "testIterator() requires that ListOfArrays is defined correctly");
        requireTest(new ListOfArraysIteratorTest(), "classDefinitionCorrect",
                "testIterator() requires that ListOfArraysIterator is defined correctly");

        try {
            Object result = listOfArraysClass.getMethod("iterator").invoke(listOfArraysClass.getDeclaredConstructor().newInstance());

            assertNotNull(result, "Method returned null");
            assertEquals(Class.forName("h02.list_of_arrays.ListOfArraysIterator"), result.getClass(), "Result is not a ListOfArraysIterator");
        } catch (ReflectiveOperationException e) {
            fail(e);
        }
    }
}
