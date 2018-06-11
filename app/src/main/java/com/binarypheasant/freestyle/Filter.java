package com.binarypheasant.freestyle;

public class Filter {
    private int filterImage;
    private String filterName;
    public Filter(String name,int imageId){
        filterName = name;
        filterImage = imageId;
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

