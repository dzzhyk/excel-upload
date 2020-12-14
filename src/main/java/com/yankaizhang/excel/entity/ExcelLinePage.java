package com.yankaizhang.excel.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@AllArgsConstructor
@NoArgsConstructor
public class ExcelLinePage implements Pageable {

    private Integer pageNumber = 1;
    private Integer pageSize = 30;

    private Sort sort;

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return this.pageNumber;
    }

    @Override
    public int getPageSize() {
        return this.pageSize;
    }

    @Override
    public long getOffset() {
        return (getPageNumber() - 1) * getPageSize();
    }

    @Override
    public Sort getSort() {
        return this.sort;
    }

    @Override
    public Pageable next() {
        return null;
    }

    @Override
    public Pageable previousOrFirst() {
        return null;
    }

    @Override
    public Pageable first() {
        return null;
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
