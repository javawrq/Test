package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity;

/**
 * Created by Administrator on 2018/3/17.
 */

public class PageInfo {
    private int pageSize;//每页数量
    private int currentPage;//当前页码
    private int totalNum;//总数
    private int totalPage;//总页数

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
