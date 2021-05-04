package h02.osh;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ListTest {

  // TODO: Change to the list you want to test
  private final List<Integer> list = new ArrayList<>();

    @Test
    void testThat_addNullWorks() {
        var n = 5;

        for (var i = 0; i < n; i++) {
            assertTrue(list.add(null));
        }

        for (var i = 0; i < n; i++) {
            assertNull(list.get(i));
        }
    }


    @Test
    void testThat_addWorks() {
        assertTrue(list.add(3));
        assertTrue(list.add(1));
        assertTrue(list.add(2));
        assertTrue(list.add(0));

        assertEquals(3, list.get(0));
        assertEquals(1, list.get(1));
        assertEquals(2, list.get(2));
        assertEquals(0, list.get(3));
    }

    @Test
    void testThat_emptySetIteratorHasNoEements() {
        assertFalse(list.iterator().hasNext());

        for (@SuppressWarnings("unused") Integer streicher : list) {
            fail("There was an element");
        }

        assertThrows(NoSuchElementException.class, list.iterator()::next);
    }

    @Test
    void testThat_iteratorWorks() {
        assertTrue(list.add(1));
        assertTrue(list.add(4));
        assertTrue(list.add(0));
        assertTrue(list.add(2));
        assertTrue(list.add(3));

        var it = list.iterator();
        assertTrue(it.hasNext());
        assertEquals(1, it.next());
        assertTrue(it.hasNext());
        assertEquals(4, it.next());
        assertTrue(it.hasNext());
        assertEquals(0, it.next());
        assertTrue(it.hasNext());
        assertEquals(2, it.next());
        assertTrue(it.hasNext());
        assertEquals(3, it.next());
        assertFalse(it.hasNext());

        assertThrows(NoSuchElementException.class, it::next);
    }

  @Test
  void testThat_iteratorWorksWithLargeNumbers() {
    var n = 100000;

    for (int i = 0; i < n; i++) {
      assertTrue(list.add(i));
    }

    var it = list.iterator();
    for (int i = 0; i < n; i++) {
      assertTrue(it.hasNext());
      assertEquals(i, it.next());
    }

    assertFalse(it.hasNext());
    assertThrows(NoSuchElementException.class, it::next);
  }

  @Test
  void testThat_containsOfEmptyIsFalse() {
    var n = 100;
    for (var i = -n; i < n; i++) {
      assertFalse(list.contains(i));
    }
  }

    @Test
    void testThat_containsIsTrueAfterAdding() {
        list.add(0);
        list.add(5);
        list.add(4);
        list.add(2);
        assertTrue(list.contains(4));
        assertTrue(list.contains(0));
    }

    @Test
    void testThat_containsIsFalseAfterAdding() {
        list.add(0);
        list.add(5);
        list.add(4);
        list.add(2);
        assertFalse(list.contains(3));
        assertFalse(list.contains(1));
    }

    @Test
    void testThat_addAllWorks() {
        assertTrue(list.addAll(List.of(5, 7, 2)));
        assertTrue(list.addAll(List.of(5, 2)));

        assertEquals(5, list.get(0));
        assertEquals(7, list.get(1));
        assertEquals(2, list.get(2));
        assertEquals(5, list.get(3));
        assertEquals(2, list.get(4));
    }

  @Test
  void testThat_addAllWorksWithLargeNumbers() {
    var n = 100;
    for (int i = 0; i < n; i++) {
      list.addAll(makeBigList(i*n, n));
    }

    for (int i = 0; i < n*n; i++) {
      assertEquals(i, i);
    }
  }

  private List<Integer> makeBigList(int begin, int size) {
    var list = new ArrayList<Integer>();
    for (int i = 0; i < size; i++) {
      list.add(begin+i);
    }
    return list;
  }

  @Test
  void testThat_addWorksWithLargeNumbers() {
      var n = 10_0000;
      for (int i = 0; i < n; i++) {
        list.add(i);
      }
      for (int i = 0; i < n; i++) {
        assertEquals(i,  list.get(i));
      }
    }

    @Test
    void testThat_getRetunsAfterAdd() {
        list.add(4);
        list.add(2);
        list.add(0);
        assertEquals(2, list.get(1));
        assertEquals(0, list.get(2));
        assertEquals(4, list.get(0));
    }

}
