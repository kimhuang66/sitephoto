package edu.princeton.sitephoto.data;

import java.util.ArrayList;
import java.util.List;

public class User implements Comparable<User> {
    public String puid;
    public String netid;
    public String prefer_name;
    public String sort_name;
    public String res_college;
    public String major;
    // public String class_year;
    public List<Group> precept = new ArrayList<Group>();
    public String photo;

    public String toString() {
        return "emplid: " + puid + " ,netid: " + netid + " ,prefer_name: " + prefer_name + " ,sort_name: " + sort_name
                + " ,major: " + major + " ,precept: " + preceptToString() + ":end";
    }

    public String preceptToString() {
        StringBuffer strb = new StringBuffer();
        for (Group item : this.precept) {
            strb.append(item.toString());
            strb.append(" ");
        }
        return strb.toString().trim();
    }

    public String getSortName() {
        return sort_name;
    }

    @Override
    public int compareTo(User u) {
        return this.getSortName().compareTo(u.getSortName());
    }
}