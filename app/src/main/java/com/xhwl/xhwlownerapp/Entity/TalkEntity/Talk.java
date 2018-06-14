package com.xhwl.xhwlownerapp.Entity.TalkEntity;

/**
 * Created by Administrator on 2018/3/26.
 */

public class Talk {
    private int total;
    private int pageSize;
    private int pageNumber;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }
}
