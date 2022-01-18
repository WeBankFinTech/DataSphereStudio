package com.webank.wedatasphere.warehouse.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageInfo<T> {
    private List<T> items;
    private Long current;
    private Long pageSize;
    private Long total;
}
