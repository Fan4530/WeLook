package com.laioffer.eventreporter;

/**
 * Created by program on 7/4/2017.
 */


public class User {
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private long time;

    public User(final String username, final String password, final long timeStamp) {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        this.username = username;
        this.password = password;
        this.time = timeStamp;
    }
}
