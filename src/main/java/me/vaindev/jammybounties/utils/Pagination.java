package me.vaindev.jammybounties.utils;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Pagination<T> extends ArrayList<T> {

    private final int pageSize;
    private int pageNumber;

    public Pagination(Player player, int pageSize) {
        this(player, pageSize, new ArrayList<>());
    }

    @SafeVarargs
    public Pagination(Player player, int pageSize, T... objects) {
        this(player, pageSize, Arrays.asList(objects));
    }

    public Pagination(Player player, int pageSize, List<T> objects) {
        this.pageSize = pageSize;
        this.pageNumber = 1;
        addAll(objects);
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public void setPageNumber(int pageNum) {
        this.pageNumber = pageNum;
    }

    public int pageSize() {
        return this.pageSize;
    }

    public int totalPages() {
        return (int) Math.floor((double) size() / pageSize) + 1;
    }

    public List<T> getPage(int page) {
        if(page < 0 || page > totalPages()) throw new IndexOutOfBoundsException("Index: " + page + ", Size: " + totalPages());

        List<T> objects = new ArrayList<>();

        int min = (page - 1) * pageSize;
        int max = page * pageSize;

        if(max > size()) max = size();

        for(int i = min; max > i; i++)
            objects.add(get(i));

        return objects;
    }

    public List<List<T>> getPages() {
        List<List<T>> pages = new ArrayList<>();

        for (int page = 1; page <= totalPages(); page++) {
            List<T> items = new ArrayList<>();

            int min = page * pageSize;
            int max = ((page * pageSize) + pageSize);

            if(max > size()) max = size();

            for(int i = min; max > i; i++)
                items.add(get(i));

            pages.add(items);
        }

        return pages;
    }
}