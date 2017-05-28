package ru.onotole.dumper.parallel.iterators;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

/**
 * Created by onotole on 28/05/2017.
 */
//@RequiredArgsConstructor
public class IdsBanchIterator implements Iterator<Integer>, ProcessStatus {
    private final Integer startId;
    private final Integer finishId;
    private final Integer step;

    private Integer currentId;

    public IdsBanchIterator(Integer startId, Integer finishId, Integer step) {
        this.startId = startId;
        this.finishId = finishId;
        this.step = step;
        currentId = startId;
    }

    @Override
    public boolean hasNext() {
        return currentId < finishId;
    }

    @Override
    public Integer next() {
        return currentId += step;
    }

    @Override
    public double getProcess() {
        return (double)(currentId - startId) / (finishId - startId);
    }
}
