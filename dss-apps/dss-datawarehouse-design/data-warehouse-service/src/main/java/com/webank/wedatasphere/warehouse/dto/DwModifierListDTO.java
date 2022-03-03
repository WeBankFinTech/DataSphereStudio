package com.webank.wedatasphere.warehouse.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class DwModifierListDTO {
   private Long id;
   private Long modifierId;
   private String name;
   private String identifier;
   private String formula;
}
