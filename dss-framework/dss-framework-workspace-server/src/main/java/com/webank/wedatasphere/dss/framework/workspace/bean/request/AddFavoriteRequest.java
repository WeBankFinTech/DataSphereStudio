package com.webank.wedatasphere.dss.framework.workspace.bean.request;

import java.io.Serializable;

public class AddFavoriteRequest implements Serializable {
   private Long menuApplicationId;

    public Long getMenuApplicationId() {
        return menuApplicationId;
    }

    public void setMenuApplicationId(Long menuApplicationId) {
        this.menuApplicationId = menuApplicationId;
    }
}
