package com.chornsby.touristtracker.navigationdrawer;

public class NavDrawerListItem {

    private String title;
    private int notifications;

    public NavDrawerListItem(String title, int notifications) {
        this.title = title;
        this.notifications = notifications;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }
}
