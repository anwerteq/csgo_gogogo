
package com.chenerzhu.crawler.proxy.pool.csgo.steamentity;
public class Searchdata
{
    private String query;

    private boolean search_descriptions;

    private int total_count;

    private int pagesize;

    private String prefix;

    private String class_prefix;

    public void setQuery(String query){
        this.query = query;
    }
    public String getQuery(){
        return this.query;
    }
    public void setSearch_descriptions(boolean search_descriptions){
        this.search_descriptions = search_descriptions;
    }
    public boolean getSearch_descriptions(){
        return this.search_descriptions;
    }
    public void setTotal_count(int total_count){
        this.total_count = total_count;
    }
    public int getTotal_count(){
        return this.total_count;
    }
    public void setPagesize(int pagesize){
        this.pagesize = pagesize;
    }
    public int getPagesize(){
        return this.pagesize;
    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public String getPrefix(){
        return this.prefix;
    }
    public void setClass_prefix(String class_prefix){
        this.class_prefix = class_prefix;
    }
    public String getClass_prefix(){
        return this.class_prefix;
    }
}
