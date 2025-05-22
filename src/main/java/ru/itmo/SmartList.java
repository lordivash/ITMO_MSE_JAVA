package ru.itmo;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SmartList<E> extends AbstractList<E> {
	private int size;
	private Object data;

	public SmartList() {
		size = 0;
		data = null;
	}

	public SmartList(Collection<? extends E> c) {
		if (c.isEmpty()) {
			size = 0;
			data = null;
		} else if (c.size() == 1) {
			size = 1;
			data = c.iterator().next();
		} else if (c.size() <= 5) {
			size = c.size();
			data = new Object[5];
			int i = 0;
			for (E e : c) {
				((Object[]) data)[i++] = e;
			}
		} else {
			data = new ArrayList<>(c);
			size = c.size();
		}
	}

	@Override
	public int size() {
		return size;
	}

    @Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		checkIndex(index);

		if (data instanceof ArrayList) {
			return ((ArrayList<E>) data).get(index);
		} else if (data instanceof Object[]) {
			return (E) ((Object[]) data)[index];
		} else {
			return (E) data;
		}
	}

    @Override
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		checkIndex(index);
		E old = get(index);

		if (data instanceof ArrayList) {
			((ArrayList<E>) data).set(index, element);
		} else if (data instanceof Object[]) {
			((Object[]) data)[index] = element;
		} else {
			data = element;
		}

		return old;
	}

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
	public void add(int index, E element) {
		if (index < 0 || index > size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

        switch (data) {
            case null -> data = element;
            case ArrayList arrayList -> ((ArrayList<E>) data).add(index, element);
            case Object[] arr -> {
                if (size < 5) {
                    System.arraycopy(arr, index, arr, index + 1, size - index);
                    arr[index] = element;
                } else {
                    ArrayList<Object> list = new ArrayList<>(2 * size);
                    list.addAll(Arrays.asList(arr).subList(0, index));
                    list.add(element);
                    list.addAll(Arrays.asList(arr).subList(index, size));
                    data = list;
                }
            }
			// Single element
            default -> {
                Object[] arr = new Object[5];
                if (index == 0) {
                    arr[0] = element;
                    arr[1] = data;
                } else {
                    arr[0] = data;
                    arr[1] = element;
                }

                data = arr;
            }
        }

		size++;
		restore();
	}

    @Override
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		checkIndex(index);
		E old = get(index);

		if (data instanceof ArrayList) {
			((ArrayList<E>) data).remove(index);
		} else if (data instanceof Object[] arr) {
            System.arraycopy(arr, index + 1, arr, index, size - index - 1);
			arr[size - 1] = null;
		} else {
			data = null;
		}

		size--;
		restore();
		return old;
	}

	private void checkIndex(int index) {
		if (index < 0 || index >= size) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	private void restore() {
		if (size == 0) {
			data = null;
			return;
		}
		if (size == 1) {
			if (data instanceof ArrayList) {
				data = ((ArrayList<?>) data).getFirst();
			} else if (data instanceof Object[]) {
				data = ((Object[]) data)[0];
			}
			return;
		}
		if (size >= 2 && size <= 5) {
			if (data instanceof ArrayList<?> list) {
				Object[] arr = new Object[5];
                for (int i = 0; i < size; i++) {
					arr[i] = list.get(i);
				}
				data = arr;
			} else if (!(data instanceof Object[])) {
				Object[] arr = new Object[5];
				arr[0] = data;
				data = arr;
			}

			return;
		}
		if (size >= 6) {
			if (!(data instanceof ArrayList)) {
				ArrayList<Object> list = new ArrayList<>(2 * size);
				Object[] arr = (Object[]) data;
				list.addAll(Arrays.asList(arr).subList(0, size));
				data = list;
			}
		}
	}
}
