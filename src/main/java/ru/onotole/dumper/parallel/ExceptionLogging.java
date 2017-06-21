package ru.onotole.dumper.parallel;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class ExceptionLogging {
    public static Runnable withExceptionLogging(Runnable task) {
        return () -> {
            while (true) {
                if (Thread.currentThread().isInterrupted()) return;
                try {
                    log.info("Thread " + Thread.currentThread().getName() + " running.");
                    task.run();
                } catch (Throwable e) {
                    e.printStackTrace();
                    log.error("Thread " + Thread.currentThread().getName() + " is dead. " + Arrays.toString(e.getStackTrace()));
                }
            }
        };
    }
}
