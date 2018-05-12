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

    public List<T> getOriginalQueue() { return list; }

    public void add(T e) throws InterruptedException {
        mutexElement.acquire();
        list.add(e);
        mutexElement.release();
        countElements.release();
    }

    public T poll() throws InterruptedException {
        countElements.acquire();
        mutexElement.acquire();
        T element = list.remove(0);
        mutexElement.release();
        return element;
    }

    public void removeElement(T e) { //TODO documentar que se recomienda solo lockeando y deslockeando a mano
        list = list.stream()
                .filter(elem -> elem != e)
                .collect(Collectors.toList());
    }

    public void blockingBeforeGet() throws InterruptedException {
        countElements.acquire();
        mutexElement.acquire();
    }

    public void unLocking() {
        mutexElement.release();
    }

}
