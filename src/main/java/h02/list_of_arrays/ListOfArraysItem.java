package h02.list_of_arrays;

import java.util.Arrays;

class ListOfArraysItem<T> {
  public int numberOfListElemsInArray;
  public T[] arrayOfElems;
  public ListOfArraysItem<T> next;
  public ListOfArraysItem<T> previous;

  public ListOfArraysItem() {
  }

  @SuppressWarnings("unchecked")
  public ListOfArraysItem(int length) {
    this.arrayOfElems = (T[]) new Object[length];
  }

  @SuppressWarnings("unchecked")
  public ListOfArraysItem(ListOfArraysItem<T> previous, int length){
    this.previous = previous;
    arrayOfElems = (T[]) new Object[length];
  }

  @Override
  public String toString() {
    return "ListOfArraysItem{" +
      "numberOfListElemsInArray=" + numberOfListElemsInArray +
      ", arrayOfElems=" + Arrays.toString(arrayOfElems) +
      ", next=" + next +
      ", wont show previous because laggines" +
      '}';
  }
}
