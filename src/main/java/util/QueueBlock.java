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

/*
 * Este metodo no maneja concurrencia, para usarlo se debe usar:
 * el par de metodos block() (o su variante blockingBeforeGet()) y unBlock()
 */
 public List<T> getOriginalQueue() { return list; }

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

 /*
 * Este metodo no maneja concurrencia, para usarlo se debe usar:
 * el par de metodos block() (o su variante blockingBeforeGet()) y unBlock()
 */
    public void removeElement(T e) {
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

    public void block() { acquireWithoutCheckedExeption(mutexElement); }

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
