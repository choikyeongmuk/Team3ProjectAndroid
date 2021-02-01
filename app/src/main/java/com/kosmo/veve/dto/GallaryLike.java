package com.kosmo.veve.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GallaryLike {

    private String gallary_no;
    private String userID;
    private boolean heartCheck;

    public boolean isHeartCheck() {
        return heartCheck;
    }

    public void setHearCheck(boolean heartCheck) {
        this.heartCheck = heartCheck;
    }
}
