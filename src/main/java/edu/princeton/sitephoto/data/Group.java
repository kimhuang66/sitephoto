package edu.princeton.sitephoto.data;

import java.util.HashMap;
import java.util.Map;

public class Group implements Comparable<Group> {
    public String id;
    public String type; // ssrComponent: PRE, LAB, STUDIO, CLASS, etc.
    public Map<String, String> meetings = new HashMap<String, String>();
    public String coursename;
    public String meeting;

    public String toString() {
        StringBuffer mstr = new StringBuffer();
        this.meetings.forEach((key, value) -> {
            mstr.append((key + " => " + value + "\t"));
        });
        return ("id: " + this.id + ", type: " + this.type + ", coursename: " + this.coursename + ", meetings: "
                + mstr.toString());
    }

    public String getId() {
        return this.id;
    }

    @Override
    public int compareTo(Group g) {
        return this.getId().compareTo(g.getId());
    }
}
