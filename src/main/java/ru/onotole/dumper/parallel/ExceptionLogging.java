package ru.onotole.dumper.parallel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionLogging {
    public static Runnable withExceptionLogging(Runnable task) {
        return () -> {
            try {
                log.info("Thread " + Thread.currentThread().getName() + " running.");
                task.run();
            } catch (Throwable e) {
                log.error("Thread " + Thread.currentThread().getName() + " is dead.", e);
            }
        };
    }
}
