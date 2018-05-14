package util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class QueueBlock<T> {

    private Semaphore countElements = new Semaphore(0);
    private Semaphore mutexElement = new Semaphore(1);
    private List<T> list = new ArrayList<>();

    public List<T> getOriginalQueue() { return list; } //TODO leer siempre y cuando se lockee desde afuera

    public void add(T e) {
        block();
        list.add(e);
        unBlock();
        upCount();
    }

    public T poll() {
        downCount();
        block();
        T element = list.remove(0);
        unBlock();
        return element;
    }

    public void removeElement(T e) { //TODO documentar que se recomienda solo lockeando y deslockeando a mano
        list = list.stream()
                .filter(elem -> elem != e)
                .collect(Collectors.toList());
    }

    public int size() {
        block();
        int size = list.size();
        unBlock();
        return size;
    }

    public void blockingBeforeGet() {
        downCount();
        block();
    }

    public void unBlock() {
        mutexElement.release();
    }

    private void block() { acquireWithoutCheckedExeption(mutexElement); }

    private void upCount() { countElements.release(); }

    private void downCount() { acquireWithoutCheckedExeption(countElements); }

    private void acquireWithoutCheckedExeption(Semaphore sem) {
        try {
            sem.acquire();
        } catch(InterruptedException v) {
            throw new RuntimeException(v);
        }
    }

}
