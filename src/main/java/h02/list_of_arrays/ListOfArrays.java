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
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
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


  /*
  public boolean add_old(T t) {
    //Erste freie Stelle finden

    if (tail == null) {
      tail = head = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
      tail.arrayOfElems[0] = t;
      tail.numberOfListElemsInArray++;
      return true;
    }
    var array = tail.arrayOfElems;
    for (int i = 0; i < array.length; i++) {
      //wenn freie stelle im array
      if (array[i] == null) {
        //objekt einfügen, counter hochzählen
        array[i] = t;
        tail.numberOfListElemsInArray++;
        return true;
      }
    }
    //Array spalten und element einfügen
    var oldListOfArraysTail = tail;
    tail = tail.next = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
    tail.previous = oldListOfArraysTail;
    var newArray = tail.arrayOfElems;
    //Array halbieren
    for (int i = 0, j = LENGTH_OF_ALL_ARRAYS / 2; i < LENGTH_OF_ALL_ARRAYS / 2; i++, j++) {
      newArray[j] = array[j];
      array[j] = null;
    }
    //neues element einfügen
    newArray[LENGTH_OF_ALL_ARRAYS / 2] = t;
    tail.numberOfListElemsInArray++;
    return true;
  }
*/

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
    return false;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return false;
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


/*
  public boolean addAll_half_filling(Collection<? extends T> c) {
    if (c != null && head == null) {
      head = tail = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
    } else if (c == null)
      throw new NullPointerException();
    //Menge der nötigen neuen arrays finden und alle erzeugen


    //wenn alle elemente aus c nicht mehr ins aktuelle array passen und das jetzige schon mehr als halb voll ist, dann aufspalten
    int countNewArrays = 0;
    if (c.size() + tail.numberOfListElemsInArray > LENGTH_OF_ALL_ARRAYS) {
      //anzahl der neuen zusätzlichen arrays berechnen
      boolean firstArray = true;
      int newElements = c.size();
      while (newElements > 0) {
        if (firstArray) {
          if (tail.numberOfListElemsInArray + newElements > LENGTH_OF_ALL_ARRAYS) {
            var oldListOfArraysTail = tail;
            tail = tail.next = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
            tail.previous = oldListOfArraysTail;
            for (int i = 0, j = LENGTH_OF_ALL_ARRAYS / 2; i < LENGTH_OF_ALL_ARRAYS / 2; i++, j++) {
              tail.arrayOfElems[j] = oldListOfArraysTail.arrayOfElems[j];
              oldListOfArraysTail.arrayOfElems[j] = null;
            }
          }
          newElements -= tail.numberOfListElemsInArray;
          firstArray = false;
        } else {
          if (newElements - LENGTH_OF_ALL_ARRAYS > 0) {
            countNewArrays++;
            newElements = -LENGTH_OF_ALL_ARRAYS / 2;
          } else {
            newElements -= LENGTH_OF_ALL_ARRAYS;
          }
        }
      }
    }
    var items = c.stream().iterator();
    //Elemente hinzufügen
    while (countNewArrays > 0) {
      //halbes array füllen
      for (int i = 0; i < LENGTH_OF_ALL_ARRAYS / 2 && items.hasNext(); i++) {
        tail.arrayOfElems[i] = items.next();
        tail.numberOfListElemsInArray++;
      }
      var temp = tail;
      tail = tail.next = new ListOfArraysItem<>(LENGTH_OF_ALL_ARRAYS);
      tail.previous = temp;
      countNewArrays--;
    }

    //letztes array mit restlichen elementen füllen

    for (int i = 0; i < LENGTH_OF_ALL_ARRAYS && items.hasNext(); i++) {
      tail.arrayOfElems[i] = items.next();
      tail.numberOfListElemsInArray++;
    }


    return true;
  }
*/


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


  @Override
  public String toString() {
    return "ListOfArrays{" +
      "LENGTH_OF_ALL_ARRAYS=" + LENGTH_OF_ALL_ARRAYS +
      ", head=" + head +
      ", tail=" + tail +
      '}';
  }
}
