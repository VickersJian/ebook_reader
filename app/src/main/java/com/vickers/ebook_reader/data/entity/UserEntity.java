/* Created by Vickers Jian on 2019/07 */
package com.vickers.ebook_reader.data.entity;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 * 用户名(userId)是唯一标识
 */
public class UserEntity extends LitePalSupport {

    private long id;
    private String userId;//唯一标识
    private String dispalyName;
    private String password;
    private String order="";

    private List<UserLibraryBookEntity> userLibraryBookEntityList = new ArrayList<>();

    private UserEntity() {
    }

    public UserEntity(String userId, String dispalyName, String password) {
        this.userId = userId;
        this.dispalyName = dispalyName;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public String getUserID() {
        return userId;
    }

    public void setUserID(String userID) {
        this.userId = userID;
    }

    public String getDispalyName() {
        return dispalyName;
    }

    public void setDispalyName(String dispalyName) {
        this.dispalyName = dispalyName;
    }

    public String getPassword() {
        return password;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<UserLibraryBookEntity> getUserLibraryBookEntityList() {
        return userLibraryBookEntityList;
    }

}