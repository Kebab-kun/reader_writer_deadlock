package sync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;



class Test {
    public static void main(String [] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ReadWriteLock RW = new ReadWriteLock();

        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));
        executorService.execute(new Writer(RW));

        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));
        executorService.execute(new Reader(RW));



    }
}
class ReadWriteLock{


    private Semaphore S=new Semaphore(1);
    private Semaphore readMutex=new Semaphore(1);
    private int readerCount=0; // active readers counter

    public void readLock() {

        try
        {
            readMutex.acquire();
        }
        catch(InterruptedException e) {}

        readerCount++;

        if(readerCount==1) // if reader exists
        {
            try
            {
                S.acquire(); // no writer can write while readers are reading
            }
            catch(InterruptedException e) {}
        }

        System.out.println(Thread.currentThread().getName() + " is reading. Active readers: " + readerCount);

        readMutex.release(); // for other readers could read

    }



    public void writeLock() {

        try {
            S.acquire(); // no writer can write
        }
        catch(InterruptedException e) {}

        System.out.println(Thread.currentThread().getName() + " is writing");

    }



    public void readUnLock() {

        try
        {
            readMutex.acquire();
        }
        catch(InterruptedException e) {}

        readerCount--;

        if(readerCount == 0) // if no reader exists writers may read
        {
            S.release();
        }

        System.out.println(Thread.currentThread().getName() + " has done reading. Active Readers: " + readerCount);

        readMutex.release();


    }



    public void writeUnLock() {

        System.out.println(Thread.currentThread().getName() + " has done writing");

        S.release();

    }
}





class Writer implements Runnable
{
    private ReadWriteLock RW_lock;


    public Writer(ReadWriteLock rw) {
        RW_lock = rw;
    }

    public void run() {
        while (true){

            try {
                Thread.sleep((long)(Math.random() * 1000)); //time for thread to do its execution
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            RW_lock.writeLock();

            try {
                Thread.sleep((long)(Math.random() * 1000));//time for thread to do its execution
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RW_lock.writeUnLock();

        }
    }


}



class Reader implements Runnable
{
    private ReadWriteLock RW_lock;


    public Reader(ReadWriteLock rw) {
        RW_lock = rw;
    }
    public void run() {
        while (true){

            try {
                Thread.sleep((long)(Math.random() * 1000)); //time for thread to do its execution
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RW_lock.readLock();

            try {
                Thread.sleep((long)(Math.random() * 1000)); //time for thread to do its execution
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RW_lock.readUnLock();

        }
    }


}