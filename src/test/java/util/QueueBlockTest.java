package util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

/**
 * Created by javierzolotarchuk on 14/05/18.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest( { QueueBlockTest.class })
public class QueueBlockTest {

    private QueueBlock<String> queueBlock = null;

    @Before
    public void executedBeforeEach() {
        queueBlock = new QueueBlock<>();
    }

    @Test
    public void pollElement() throws InterruptedException {
        Semaphore mutexElementMock = mock(Semaphore.class);
        Whitebox.setInternalState(queueBlock, "mutexElement", mutexElementMock);

        Semaphore countElements = Whitebox.getInternalState(queueBlock,"countElements");

        loadLetters(queueBlock,"A","B","C");

        String element = queueBlock.poll();

        List<String> result = lettersExpected("B","C");

        verify(mutexElementMock,times(4)).acquire(); //Debido a que tmb lockea cuando hace el poll
        verify(mutexElementMock,times(4)).release();

        Assert.assertEquals(countElements.availablePermits(),2);
        Assert.assertEquals("A",element);
        Assert.assertEquals(result,queueBlock.getOriginalQueue());
    }

    @Test
    public void removeElement() throws InterruptedException {
        Semaphore mutexElementMock = mock(Semaphore.class);
        Whitebox.setInternalState(queueBlock, "mutexElement", mutexElementMock);

        Semaphore countElements = Whitebox.getInternalState(queueBlock,"countElements");
        loadLetters(queueBlock,"A","B","C");

        queueBlock.removeElement("B");

        List<String> result = lettersExpected("A","C");

        verify(mutexElementMock,times(3)).acquire(); //Debido a que este es un metodo custom para lockear desde afuera
        verify(mutexElementMock,times(3)).release();

        Assert.assertEquals(countElements.availablePermits(),3); //Ya que el remove no se hacer cargo ni del mutex ni del count
        Assert.assertEquals(result,queueBlock.getOriginalQueue());
    }

    @Test
    public void addElement() throws InterruptedException {
        Semaphore countElementsMock = mock(Semaphore.class);
        Whitebox.setInternalState(queueBlock, "countElements", countElementsMock);

        Semaphore mutexElementMock = mock(Semaphore.class);
        Whitebox.setInternalState(queueBlock, "mutexElement", mutexElementMock);

        loadLetters(queueBlock,"A","B","C");

        List<String> result = lettersExpected("A","B","C");

        verify(countElementsMock,times(3)).release();
        verify(countElementsMock,never()).acquire();

        verify(mutexElementMock,times(3)).acquire();
        verify(mutexElementMock,times(3)).release();

        Assert.assertEquals(queueBlock.getOriginalQueue(),result);
    }

    @Test
    public void addOneElement() throws InterruptedException {
        Semaphore mutexElementMock = mock(Semaphore.class);
        Whitebox.setInternalState(queueBlock, "mutexElement", mutexElementMock);

        Semaphore countElements = Whitebox.getInternalState(queueBlock,"countElements");

        loadLetters(queueBlock,"A");

        verify(mutexElementMock,times(1)).acquire();
        verify(mutexElementMock,times(1)).release();
        Assert.assertEquals(countElements.availablePermits(),1);
    }

    private void loadLetters(QueueBlock<String> queueBlock, String... letters) {
        Arrays.stream(letters).forEach(w -> queueBlock.add(w));
    }

    private List<String> lettersExpected(String... letters) {
        return Arrays.stream(letters).collect(Collectors.toList());
    }
}
