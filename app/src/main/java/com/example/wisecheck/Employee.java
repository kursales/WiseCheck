package com.example.wisecheck;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Employee {
    @PrimaryKey(autoGenerate = true)
    public long id;
    private String name;
    private String birthday;
    private String profession;
    private String pathToFile;
    private String url;

    public void setName(String name){
        this.name=name;
    }
    public void setBirthday(String birthday){
        this.birthday=birthday;
    }
    public void setProfession(String profession){
        this.profession = profession;
    }
    public void setPathToFile(String pathToFile){this.pathToFile=pathToFile;}
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public String getName(){
        return name;
    }
    public String getBirthday(){
        return birthday;
    }
    public String getProfession(){
        return profession;
    }
    public String getPathToFile(){return pathToFile;}


}
