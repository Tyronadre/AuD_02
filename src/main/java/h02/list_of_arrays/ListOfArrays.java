package h02.list_of_arrays;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListOfArrays<T> implements List<T> {
  private static final int DEFAULT_LENGTH_OF_ALL_ARRAYS;

  static {
    DEFAULT_LENGTH_OF_ALL_ARRAYS = 256;
  }

  private int LENGTH_OF_ALL_ARRAYS;
  private ListOfArraysItem<T> head;
  private ListOfArraysItem<T> tail;


  public ListOfArrays(int LENGTH_OF_ALL_ARRAYS, ListOfArraysItem<T> head, ListOfArraysItem<T> tail) {
    this.LENGTH_OF_ALL_ARRAYS = LENGTH_OF_ALL_ARRAYS;
    this.head = head;
    this.tail = tail;
  }

  public void readArrayLength(String string) throws Exception {
    File file = new File(string);
    if (file.exists() || file.canRead()) {
      int i;
      try {
        var read = new BufferedReader(Files.newBufferedReader(file.toPath());
        i = Integer.parseInt(read.readLine());
      } catch (IOException e) {
        throw e;
      } catch (Exception e) {
        throw new IOException();
      }
      if (i < 0)
        throw new NegativeArraySizeException();
    }
  }


  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    var nTail = head;
    while (nTail.next != null)
    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return null;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    return null;
  }

  @Override
  public boolean add(T t) {
    return false;
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    return false;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    return false;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return false;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return false;
  }

  @Override
  public void clear() {

  }

  @Override
  public T get(int index) {
    return null;
  }

  @Override
  public T set(int index, T element) {
    return null;
  }

  @Override
  public void add(int index, T element) {

  }

  @Override
  public T remove(int index) {
    return null;
  }

  @Override
  public int indexOf(Object o) {
    return 0;
  }

  @Override
  public int lastIndexOf(Object o) {
    return 0;
  }

  @Override
  public ListIterator<T> listIterator() {
    return null;
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    return null;
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    return null;
  }
}
