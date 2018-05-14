package domain;

import java.util.Random;

/**
 * Created by javierzolotarchuk on 12/05/18.
 */
public class Call {
    private int id;

    public int getId() {
        return this.id;
    }

    public Call(int id) {
        this.id = id;
    }

    public void answer() throws InterruptedException {
        Random rand = new Random();
        int randomNum = 100; //(rand.nextInt((15 - 10) + 1) + 10) * 1000;
        System.out.println("ID: " + id + " Me voy a dormir x " + randomNum/1000 + " segundos");
        try {
            Thread.sleep(randomNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("ID: " + id + " Me desperte");
    }
}
