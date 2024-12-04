
package com.xiaojuzi.st.csgo.steamentity;
import java.util.List;
public class SteamSearchRoot
{
    private boolean success;

    private int start;

    private int pagesize;

    private int total_count;

    private SteamSearchdata steamSearchdata;

    private List<SteamItem> results;

    public void setSuccess(boolean success){
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
    public void setStart(int start){
        this.start = start;
    }
    public int getStart(){
        return this.start;
    }
    public void setPagesize(int pagesize){
        this.pagesize = pagesize;
    }
    public int getPagesize(){
        return this.pagesize;
    }
    public void setTotal_count(int total_count){
        this.total_count = total_count;
    }
    public int getTotal_count(){
        return this.total_count;
    }
    public void setSearchdata(SteamSearchdata steamSearchdata){
        this.steamSearchdata = steamSearchdata;
    }
    public SteamSearchdata getSearchdata(){
        return this.steamSearchdata;
    }
    public void setResults(List<SteamItem> results){
        this.results = results;
    }
    public List<SteamItem> getResults(){
        return this.results;
    }
}
