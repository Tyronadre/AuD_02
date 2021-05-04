package h02.list_of_arrays;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ListOfArrays<T> implements List<T> {
  private static final int DEFAULT_LENGTH_OF_ALL_ARRAYS = 256;

  private int LENGTH_OF_ALL_ARRAYS;
  private ListOfArraysItem<T> head;
  private ListOfArraysItem<T> tail;


  public ListOfArrays() {
    this.LENGTH_OF_ALL_ARRAYS = DEFAULT_LENGTH_OF_ALL_ARRAYS;
    this.head = null;
    this.tail = null;
  }

  public void readArrayLength(String string) throws IOException, NegativeArraySizeException {
    File file = new File(string);
    if (file.exists() || file.canRead()) {
      int i;
      try {
        var read = new BufferedReader(Files.newBufferedReader(file.toPath()));
        i = Integer.parseInt(read.readLine());
      } catch (IOException e) {
        throw e;
      } catch (Exception e) {
        throw new IOException();
      }
      if (i < 0)
        throw new NegativeArraySizeException();
      LENGTH_OF_ALL_ARRAYS = i;
    }
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Object o) {


    var nTail = head;
    while (nTail != null) {
      //Array durchlaufen
      int counter = 0;
      for (T component : nTail.arrayOfElems) {
        if (component != null && nTail.numberOfListElemsInArray > counter) {
          counter++;
          if (component.equals(o))
            return true;
        }
      }
      if (o == null && counter < nTail.numberOfListElemsInArray){
        return true;
      }
      nTail = nTail.next;
    }
    return false;
  }

  @Override
  public Iterator<T> iterator() {
    return new ListOfArraysIterator<>(head);
  }

  @Override
  public Object[] toArray() {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T1> T1[] toArray(T1[] a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(T t) {
    //Erste freie Stelle finden
    if (tail == null) {
      tail = head = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
      tail.arrayOfElems[0] = t;
      tail.numberOfListElemsInArray++;
      return true;
    }
    if (tail.numberOfListElemsInArray == LENGTH_OF_ALL_ARRAYS) {
      //Array splitten
      tail = tail.next = new ListOfArraysItem<>(tail, LENGTH_OF_ALL_ARRAYS);
      for (int i = 0, j = LENGTH_OF_ALL_ARRAYS / 2; i < LENGTH_OF_ALL_ARRAYS / 2; i++, j++) {
        if (LENGTH_OF_ALL_ARRAYS % 2 != 0 && i == 0)
          j++;
        tail.arrayOfElems[i] = tail.previous.arrayOfElems[j];
        tail.previous.arrayOfElems[j] = null;
        tail.numberOfListElemsInArray++;
        tail.previous.numberOfListElemsInArray--;
      }
      //Neues Element ans Ende des neuen zweiten Arrays
      tail.arrayOfElems[LENGTH_OF_ALL_ARRAYS / 2] = t;
      tail.numberOfListElemsInArray++;
      return true;
    }
    //Neues Element ans Ende des ersten Arrays
    for (int i = 0; i < tail.arrayOfElems.length; i++) {
      //wenn freie stelle im array
      if (tail.arrayOfElems[i] == null) {
        //objekt einfügen, counter hochzählen
        tail.arrayOfElems[i] = t;
        tail.numberOfListElemsInArray++;
        return true;
      }
    }
    //return false;
    return true;
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    if (c != null && head == null) {
      head = tail = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
    } else if (c == null)
      throw new NullPointerException();
    else if (c.size() == 0)
      return false;

    //Menge der nötigen arrays finden und erzeugen
    var oldTail = tail;
    if (c.size() + tail.numberOfListElemsInArray > LENGTH_OF_ALL_ARRAYS) {
      int newElements = c.size();
      newElements -= LENGTH_OF_ALL_ARRAYS - tail.numberOfListElemsInArray;
      while (newElements > 0) {
        newElements -= LENGTH_OF_ALL_ARRAYS;
        tail = tail.next = new ListOfArraysItem<>(tail, LENGTH_OF_ALL_ARRAYS);
      }
    }
    var item = c.stream().iterator();
    while (item.hasNext()) {
      if (oldTail.numberOfListElemsInArray < LENGTH_OF_ALL_ARRAYS) {
        oldTail.arrayOfElems[oldTail.numberOfListElemsInArray] = item.next();
        oldTail.numberOfListElemsInArray++;
      } else {
        oldTail = oldTail.next;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(int index, Collection<? extends T> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public T get(int index) {
    var nHead = head;
    //bis zum richtigen array
    while (index >= nHead.numberOfListElemsInArray) {
      if (nHead.next == null) {
        throw new IndexOutOfBoundsException();
      }
      index -= nHead.numberOfListElemsInArray;
      nHead = nHead.next;
    }
    for (int i = 0; i < nHead.numberOfListElemsInArray; i++) {
      if (index == i) {
        return nHead.arrayOfElems[i];
      }
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public T set(int index, T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, T element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public T remove(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int indexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int lastIndexOf(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<T> listIterator() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ListIterator<T> listIterator(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<T> subList(int fromIndex, int toIndex) {
    throw new UnsupportedOperationException();
  }


  @Override
  public String toString() {
    return "ListOfArrays{" +
      "LENGTH_OF_ALL_ARRAYS=" + LENGTH_OF_ALL_ARRAYS +
      ", head=" + head +
      ", tail=" + tail +
      '}';
  }
}
