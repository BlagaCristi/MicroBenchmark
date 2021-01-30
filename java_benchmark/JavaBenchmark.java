import sun.misc.Unsafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Random;

public class JavaBenchmark {

    private static final String PYTHON_PLOT = "/home/cristi/Desktop/scs_project/python_plot";
    private static final String MEM_ALLOCATION = PYTHON_PLOT + "/java/mem_allocation/memAllocation.txt";
    private static final String MEM_DEALLOCATION_GC_OFF = PYTHON_PLOT + "/java/mem_deallocation/memDeallocation_gcOff.txt";
    private static final String MEM_DEALLOCATION_GC_ON = PYTHON_PLOT + "/java/mem_deallocation/memDeallocation_gcOn.txt";
    private static final String MEM_ACCESS = PYTHON_PLOT + "/java/mem_access/memAccess.txt";
    private static final String THREAD_CREATION = PYTHON_PLOT + "/java/thread_creation/threadCreation.txt";
    private static final String THREAD_SWITCH_CONTEXT_STOP = PYTHON_PLOT + "/java/thread_switch_context/threadSwitchContext_SUSPEND.txt";
    private static final String THREAD_SWITCH_CONTEXT_START = PYTHON_PLOT + "/java/thread_switch_context/threadSwitchContext_RESUME.txt";
    private static final String THREAD_MIGRATION = PYTHON_PLOT + "/java/thread_migration/threadMigration.txt";

    private static volatile boolean isThreadSwitchContextRunning;

    public static native long threadMigrationTime();

    static {

    	try {
    		System.loadLibrary("javaBenchmark");
    	} catch(UnsatisfiedLinkError ex) {
    		ex.printStackTrace();
    		System.exit(1);
    	}
    }

    public static void main(String[] args) {
        if (args.length != 6) {
            System.out.println("Six parameters are needed!");
            System.exit(1);
        }
        int nrTriesMemAllocation, allocationSize, nrTriesMemoryAccess, nrTriesThreadCreation, nrTriesThreadSwitchContext, nrTriesThreadMigration;
        nrTriesMemAllocation = Integer.parseInt(args[0]);
        allocationSize = Integer.parseInt(args[1]);
        nrTriesMemoryAccess = Integer.parseInt(args[2]);
        nrTriesThreadCreation = Integer.parseInt(args[3]);
        nrTriesThreadSwitchContext = Integer.parseInt(args[4]);
        nrTriesThreadMigration = Integer.parseInt(args[5]);

        long elapsedTimeMemoryAllocationNoGarbageCollector = getMemoryAllocationAndDeallocationTime(nrTriesMemAllocation, allocationSize, false);
        long elapsedTimeMemoryAllocationGarbageCollector = getMemoryAllocationAndDeallocationTime(nrTriesMemAllocation, allocationSize, true);

        long elapsedTimeMemoryAccess = getMemoryAccessTime(nrTriesMemoryAccess);

        long elapsedTimeThreadCreation = getThreadCreationTime(nrTriesThreadCreation);

        getThreadSwitchContextTime(nrTriesThreadSwitchContext);

        long elapsedTimeThreadMigration = getThreadMigrationTime(nrTriesThreadMigration);

        try {
            Runtime.getRuntime().exec("./python_plot.py java", null, new File(PYTHON_PLOT));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static long getThreadMigrationTime(int nrTriesThreadMigration) {
        long[] tries = new long[nrTriesThreadMigration];
        long finalTime = 0;

        for(int i =0;i<nrTriesThreadMigration;i++) {
            tries[i] = threadMigrationTime();
        }
        
        for(int i =0;i<nrTriesThreadMigration;i++) {
            finalTime += tries[i];
        }

        printToFile(tries, nrTriesThreadMigration, THREAD_MIGRATION);
        return finalTime / nrTriesThreadMigration;

    }

    private static void getThreadSwitchContextTime(int nrTriesThreadSwitchContext) {
        long[] stopThreadTries = new long[nrTriesThreadSwitchContext];
        long[] startThreadTries = new long[nrTriesThreadSwitchContext];
        for(int i = 0;i<nrTriesThreadSwitchContext;i++) {
            isThreadSwitchContextRunning = true;
            try {
                Thread thread = new Thread(() -> {
                    while(isThreadSwitchContextRunning) {}
                });
                thread.start();

                stopThreadTries[i] = System.nanoTime();
                thread.suspend();
                stopThreadTries[i] = System.nanoTime() - stopThreadTries[i];

                startThreadTries[i] = System.nanoTime();
                thread.resume();
                startThreadTries[i] = System.nanoTime() - startThreadTries[i];

                isThreadSwitchContextRunning = false;

                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        printToFile(stopThreadTries, nrTriesThreadSwitchContext, THREAD_SWITCH_CONTEXT_STOP);
        printToFile(startThreadTries, nrTriesThreadSwitchContext, THREAD_SWITCH_CONTEXT_START);

    }

    private static long getThreadCreationTime(int nrTriesThreadCreation) {
        long[] tries = new long[nrTriesThreadCreation];
        long finalTime = 0L;
        for (int i = 0; i < nrTriesThreadCreation; i++) {
            tries[i] = System.nanoTime();
            try {
                Thread thread = new Thread(() -> {
                });
                thread.start();
                thread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            tries[i] = System.nanoTime() - tries[i];
        }

        for (int i = 0; i < nrTriesThreadCreation; i++) {
            finalTime += tries[i];
        }

        printToFile(tries, nrTriesThreadCreation, THREAD_CREATION);
        return finalTime / nrTriesThreadCreation;
    }

    /*
        Memory access time defined as the time
        to read a byte from memory.
    */
    public static long getMemoryAccessTime(int nrTries) {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Unsafe unsafe = (Unsafe) field.get(null);
            long finalTime = 0l;
            long[] tries = new long[nrTries];
            Random random = new Random(System.nanoTime());
            long memLocation = unsafe.allocateMemory(1024);
            for (int i = 0; i < nrTries; i++) {
                tries[i] = System.nanoTime();
                Byte dummy = unsafe.getByte(memLocation + random.nextInt(1024));
                tries[i] = System.nanoTime() - tries[i];
            }

            printToFile(tries, nrTries, MEM_ACCESS);
            for (int i = 0; i < nrTries; i++) {
                finalTime += tries[i];
            }

            return finalTime / nrTries;

        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    public static long getMemoryAllocationAndDeallocationTime(int nrTries, int size, boolean gcOn) {
        long finalTime = 0L;
        long[] memoryAllocationElapsedTime = new long[nrTries];
        long[] memoryDeallocationElapsedTime = new long[nrTries];
        for (int i = 0; i < nrTries; i++) {
            memoryAllocationElapsedTime[i] = System.nanoTime();
            int[] dummy = new int[size];
            memoryAllocationElapsedTime[i] = System.nanoTime() - memoryAllocationElapsedTime[i];

            memoryDeallocationElapsedTime[i] = System.nanoTime();
            dummy = null;
            //Even if there is no reference to the object
            //We call the GC to make sure the memory is deleted
            if (gcOn)
                Runtime.getRuntime().gc();
            memoryDeallocationElapsedTime[i] = System.nanoTime() - memoryDeallocationElapsedTime[i];

        }
        for (int i = 0; i < nrTries; i++) {
            finalTime += memoryAllocationElapsedTime[i];
        }
        if (gcOn){
            printToFile(memoryAllocationElapsedTime, nrTries, MEM_ALLOCATION);
            printToFile(memoryDeallocationElapsedTime, nrTries, MEM_DEALLOCATION_GC_ON);
        }
        else
            printToFile(memoryDeallocationElapsedTime, nrTries, MEM_DEALLOCATION_GC_OFF);
        return finalTime / nrTries;
    }

    public static void printToFile(long[] values, int size, String fileLocation) {
        try {
            File file = new File(fileLocation);
            //file = new File();
            System.out.println(file.getPath());
            if (file.exists())
                file.delete();
            PrintWriter printWriter = new PrintWriter(file);
            for (int i = 0; i < size; i++) {
                printWriter.println(values[i]);
            }
            printWriter.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
