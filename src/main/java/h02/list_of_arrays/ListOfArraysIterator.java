package h02.list_of_arrays;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class ListOfArraysIterator<T> implements Iterator<T> {
  ListOfArraysItem<T> currentItem;
  int index;

  public ListOfArraysIterator(ListOfArraysItem<T> currentItem) {
    this.currentItem = currentItem;
    this.index = 0;
  }

  @Override
  public boolean hasNext() {
    return (currentItem != null && (index < currentItem.numberOfListElemsInArray || currentItem.next != null));
  }

  @Override
  public T next() {
    if (currentItem == null)
      throw new NoSuchElementException();
    if (index >= currentItem.numberOfListElemsInArray) {
      index = 0;
      currentItem = currentItem.next;
      return next();
    }
    index++;
    return currentItem.arrayOfElems[index - 1];
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forEachRemaining(Consumer<? super T> action) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ListOfArraysIterator<?> that = (ListOfArraysIterator<?>) o;
    return index == that.index && Objects.equals(currentItem, that.currentItem);
  }

  @Override
  public int hashCode() {
    return Objects.hash(currentItem, index);
  }

  @Override
  public String toString() {
    return "ListOfArraysIterator{" +
      "currentItem=" + currentItem +
      ", index=" + index +
      '}';
  }
}
