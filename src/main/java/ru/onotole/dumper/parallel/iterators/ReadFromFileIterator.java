package ru.onotole.dumper.parallel.iterators;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

/**
 * Created by onotole on 28/05/2017.
 */
public class ReadFromFileIterator implements Iterator<Integer>, ProcessStatus {
    private List<String> lines;
    private int initialSize;

    @SneakyThrows
    public ReadFromFileIterator(Path file) {
        lines = Files.readAllLines(file);
        initialSize = lines.size();
    }

    @Override
    public boolean hasNext() {
        return ! lines.isEmpty();
    }

    @Override
    public Integer next() {
        return Integer.valueOf(lines.remove(0));
    }

    @Override
    public double getProcess() {
        return (double)(initialSize - lines.size()) / initialSize;
    }
}
