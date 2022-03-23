package pcd.ass01.parallel;

import java.util.AbstractList;
import java.util.List;

public final class Partitions<T> extends AbstractList<List<T>> {

    private final List<T> list;
    private final int chunkSize;

    public Partitions(List<T> list, int chunkSize) {
        this.list = list;
        this.chunkSize = chunkSize;
    }

    public static <T> Partitions<T> ofSize(List<T> list, int chunkSize) {
        return new Partitions<>(list, chunkSize);
    }

    @Override
    public List<T> get(int index) {
        int start = index * chunkSize;
        int end = Math.min(start + chunkSize, list.size());

        if (start > end) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of the list range <0," + (size() - 1) + ">");
        }

        return list.subList(start, end);
    }

    @Override
    public int size() {
        return (int) Math.ceil((double) list.size() / (double) chunkSize);
    }
}