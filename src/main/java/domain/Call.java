package domain;

import java.util.Random;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class Call {

    private static final int MIN_TIME_CALL = 5;
    private static final int MAX_TIME_CALL = 10;

    private int id;
    private CallStatus status;

    public int getId() {
        return this.id;
    }

    public CallStatus getStatus() { return this.status; }

    public Call(int id) {
        this.id = id;
        changeStatus(CallStatus.PENDING);
    }

    public void answer() throws InterruptedException {
        changeStatus(CallStatus.TALKING);
        int duration = callDuration();
        System.out.println("ID: " + id + " Hablando x " + duration/1000 + " segundos");
        try {
            Thread.sleep(duration);
            changeStatus(CallStatus.FINISHED);
            System.out.println("ID: " + id + " Termino");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int callDuration() {
        Random rand = new Random();
        int randomNum =  (rand.nextInt((MAX_TIME_CALL - MIN_TIME_CALL) + 1) + MIN_TIME_CALL) * 1000;
        return randomNum;
    }

    private void changeStatus(CallStatus status) {
        this.status = status;
    }
}
