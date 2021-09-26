package com.webank.wedatasphere.warehouse.cqe.common;



import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
//@Builder
//@Accessors(chain = true)
public class PageCommand {
    private Integer page;
    private Integer size;
}
