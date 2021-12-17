package com.app.rtodmm.callbacks;

import com.app.rtodmm.models.Images;
import com.app.rtodmm.models.News;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CallbackPostDetail implements Serializable {

    public String status = "";
    public News post = null;
    public List<Images> images = new ArrayList<>();
    public List<News> related = new ArrayList<>();

}
