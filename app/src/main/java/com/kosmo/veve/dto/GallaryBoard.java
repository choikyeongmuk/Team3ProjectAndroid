package com.kosmo.veve.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GallaryBoard {
    private String gallary_no;
    private String title;
    private String content;
    private String postDate;
    private String userID;
    private int scrapCount;
    private int heartCount;
    private String commentCount;
    private String gallary_file_no;
    private String f_path;
    private String f_name;
    private String fileOne;

}