# ArraySet

* Необходимо создать класс `ArraySet`, реализующий интерфейс `java.util.NavigableSet` (включая методы из `java.util.SortedSet`)

```java
public class ArraySet<E extends Comparable<? super E>> implements NavigableSet<E> {
    // Минимальный набор конструкторов
    public ArraySet() {...}
    
    // Указывает компаратор, с помощью которого необходимо сравнивать значения
    // В качестве значения по-умолчанию можно использовать `Comparator.naturalOrder()`
    public ArraySet(Comparator<? super E> comparator) {...}

    public ArraySet(Collection<E> collection) {...}

    public ArraySet(Collection<E> collection, Comparator<? super E> comparator) {...}
    
    // Остальные методы
}
```

## Условия

* В множестве все элементы хранятся в отсортированном списке
* Сложность операций модификации множества: _O(n)_
* Сложность всех остальных операций, связанных с поиском в множестве: _O(log n)_ (обратите внимание на методы из класса `Collections`)
* Обратите внимание, что различные методы, которые возвращают подмножества (например, `tailSet(E fromElement)`) должны возвращать _view_ на основное множество. Это значит, что если изменить подмножество, то и основное множество также изменится
* При реализации рекомендуется наследовать ваш класс от `AbstractSet`, и нельзя наследоваться от `TreeSet`
* Для того, чтобы вам не мешали warning'и "unchecked cast" используйте на соответствующих методах аннотацию `@SuppressWarnings("unchecked")`
* Метод `spliterator()` реализовывать не надо, пусть он бросает `UnsupportedOperationException`
