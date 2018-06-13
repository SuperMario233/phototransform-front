package com.binarypheasant.freestyle;

public class Filter {
    private int filterImage;
    private String filterName;
    public String filterDes,photo_url,tag;
    public int ID;
    public Filter(String name,int imageId,String des,String url,String tag,int id){
        filterName = name;
        filterImage = imageId;
        filterDes = des;
        photo_url = url;
        this.tag = tag;
        ID = id;
    }
    public int getFilterImage(){
        return filterImage;
    }
    public void setFilterImage(int filterImage){
        this.filterImage = filterImage;
    }
    public void setFilterName(String filterName){
        this.filterName = filterName;
    }
    public String getFilterName(){
        return filterName;
    }
}

