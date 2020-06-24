package edu.princeton.sitephoto.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.princeton.sitephoto.Constants;
import edu.princeton.sitephoto.Util;
import edu.princeton.sitephoto.data.CourseMemberRaw;
import edu.princeton.sitephoto.data.Group;

import edu.princeton.sitephoto.data.Token;
import edu.princeton.sitephoto.data.User;
import edu.princeton.sitephoto.service.DataFetcherService;

@Controller
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @Autowired
    DataFetcherService dataFetcherService;

    @RequestMapping(value = { "/roster" }, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String fetchRoster(HttpServletRequest request, HttpServletResponse response, Model model) {
        String crs_id = "";
        String members = "";

        crs_id = request.getParameter("crs_id");
        logger.debug("crs_id: " + crs_id);
        // StringBuffer sb = new StringBuffer();
        // String line = null;
        // try {
        // BufferedReader reader = request.getReader();
        // while (( line = reader.readLine()) != isNull()
        // sb.append(line);
        // } catch(Exception e) {};
        // try {
        // JSONObject jo = HTTP.toJSONObject(sb.toString());
        // }catch(JSONObject e){
        // throw new IOException("Error parsing JSON request string");
        // }

        try {
            Token token = dataFetcherService.authorize();
            if (token != null) {
                members = dataFetcherService.getRoster(token, crs_id);
                // logger.debug(members);
            }
        } catch (Exception e) {
            logger.error("Exceptions: ##### " + e.toString());
        }
        return members;
    }

    @RequestMapping(value = { "/images" }, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody JSONObject fetchImages(HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        String crs_id = "";
        String members = "";
        String images = "";
        Map<String, User> users = new LinkedHashMap<String, User>();
        ArrayList<String> emplids = new ArrayList<String>();
        Map<String, Group> groups = new LinkedHashMap<String, Group>();
        ArrayList<Group> resGroup = new ArrayList<Group>();
        JSONObject result = new JSONObject();

        crs_id = request.getParameter("crs_id");
        logger.debug("crs_id: " + crs_id);
        String termYear[] = crs_id.split("_");

        try {
            Token token = dataFetcherService.authorize();
            if (token != null) {
                members = dataFetcherService.getRoster(token, crs_id);
                // logger.debug(members);
            }
        } catch (Exception e) {
            logger.error("Exceptions: ##### " + e.toString());
        }
        // {"netid":"jww4","acad_career":"UGRD","subject":"AAS","section_name":"P02","end_time":"02:20
        // pm","sort_name":"Wiggins Wesley
        // jww4","meeting_number":"1","res_college":"Butler","class_year":"2021","start_time":"01:30
        // pm","section_type":"PRE","full_name":"Wesley
        // Wiggins","puid":"920148434","meeting_days":"M","catalog_number":"
        // 201","department":"Geosciences"},{"netid":"jlps6o45","acad_career":"GRAD","subject":"ELE","section_name":"L01","end_time":"10:50
        // am","sort_name":"Yu Yifan
        // jlps6o45","meeting_number":"1","res_college":null,"class_year":null,"start_time":"10:00
        // am","section_type":"LEC","full_name":"Yifan
        // Yu","puid":"960704786","meeting_days":"MWF","catalog_number":"
        // 435","department":"Economics"},
        // for combined courses. i.e. WRI111-WRI110, each course is a group.
        Map<String, List<String>> catalogs = new HashMap<String, List<String>>();
        JSONParser parser = new JSONParser();
        if (!"".equals(members) && members.length() > 2) {
            try {
                JSONArray json = (JSONArray) parser.parse(members);
                Iterator<JSONObject> x = json.iterator();
                while (x.hasNext()) {
                    JSONObject j = x.next();
                    String emplid = "";
                    emplid = (String) j.get("puid");
                    String groupid = "";
                    String ssrComponent = ((String) j.get("section_type")).trim();
                    if (!"LEC".equals(ssrComponent)) {
                        // remove all B99,S99,U99...
                        groupid = "99".equals(((String) j.get("section_name")).substring(1)) ? ""
                                : ((String) j.get("section_name")).trim();
                    }
                    if (!"".equals(groupid) && groups.get(groupid) == null) {

                        Group group = new Group();
                        group.id = groupid;
                        group.coursename = crs_id;
                        group.type = ToGroupNameFromSSRComponent(ssrComponent);
                        String days = ((String) j.get("meeting_days"));
                        String time = ((String) j.get("start_time")) + "-" + ((String) j.get("end_time"));
                        String m_time_days = time + " " + days + " ";

                        group.meetings.put(((String) j.get("meeting_number")), m_time_days);
                        group.meeting = m_time_days;
                        groups.put(groupid, group);
                    } else if (groups.get(groupid) != null) {
                        String mn = ((String) j.get("meeting_number"));
                        Group group = groups.get(groupid);
                        // different meting time
                        if (group.meetings.get(mn) == null) {
                            String days = ((String) j.get("meeting_days"));
                            String time = ((String) j.get("start_time")) + "-" + ((String) j.get("end_time"));
                            String m_time_days = time + " " + days + " ";
                            group.meetings.put(mn, m_time_days);
                            StringBuffer ssb = new StringBuffer();
                            ssb.append(group.meeting);
                            ssb.append(m_time_days);
                            group.meeting = ssb.toString();
                        }
                    }
                    // new record
                    if (!emplids.contains(emplid)) {
                        emplids.add(emplid);
                        User user = new User();
                        user.photo = "";
                        user.puid = emplid;
                        user.prefer_name = ((String) j.get("full_name")).trim() + Util.classyearConvert(
                                ((String) j.get("class_year")) == null ? null : ((String) j.get("class_year")));
                        ;
                        user.sort_name = ((String) j.get("sort_name")).trim();
                        user.netid = ((String) j.get("netid")).trim();
                        user.major = ((String) j.get("department"));
                        user.res_college = ((String) j.get("res_college")) == null ? ""
                                : ((String) j.get("res_college"));
                        // user.class_year = Util.classyearConvert(
                        // ((String) j.get("class_year")) == null ? null : ((String)
                        // j.get("class_year")));
                        if (!"".equals(groupid)) {
                            String id = groupid;
                            List<Group> toBeRemoved = new ArrayList<Group>();
                            List<Group> toBeAdded = new ArrayList<Group>();
                            user.precept.forEach(g -> {
                                if (id.equals(g.id)) {
                                    toBeRemoved.add(g);
                                    toBeAdded.add(groups.get(id));
                                    // user.precept.remove(g);
                                    // user.precept.add(groups.get(id));
                                }
                            });
                            if (toBeRemoved.size() > 0) {
                                user.precept.removeAll(toBeRemoved);
                            }
                            if (toBeAdded.size() > 0) {
                                toBeAdded.forEach(g -> {
                                    user.precept.add(g);
                                });
                            }
                            if (!user.precept.contains(groups.get(groupid))) {
                                user.precept.add(groups.get(groupid));
                            }
                        }
                        users.put(emplid, user);
                        // if (!"".equals(user.class_year)) {
                        // user.status = "UG";
                        // } else if ("".equals(user.major)) {
                        // user.status = "Others";
                        // } else {
                        // user.status = "G";
                        // }
                    } else {
                        if (!"".equals(groupid)) {
                            String id = groupid;
                            User u = users.get(emplid);
                            List<Group> toBeRemoved = new ArrayList<Group>();
                            List<Group> toBeAdded = new ArrayList<Group>();
                            u.precept.forEach(g -> {
                                if (id.equals(g.id)) {
                                    toBeRemoved.add(g);
                                    toBeAdded.add(groups.get(id));
                                    // u.precept.remove(g);
                                    // u.precept.add(groups.get(id));
                                }
                            });
                            if (toBeRemoved.size() > 0) {
                                u.precept.removeAll(toBeRemoved);
                            }
                            if (toBeAdded.size() > 0) {
                                toBeAdded.forEach(g -> {
                                    u.precept.add(g);
                                });
                            }
                            if (!u.precept.contains(groups.get(groupid))) {
                                u.precept.add(groups.get(groupid));
                            }
                        }
                    }
                    if (!"".equals(emplid)) {
                        String catalogKey = (String) j.get("subject") + ((String) j.get("catalog_number")).trim() + "_"
                                + termYear[1];
                        logger.debug("catalog key: " + catalogKey);
                        if (catalogs.containsKey(catalogKey)) {
                            List<String> list = catalogs.get(catalogKey);
                            if (!"".equals(emplid) && !list.contains(emplid)) {
                                list.add(emplid);
                            }
                        } else {
                            List<String> list = new ArrayList<String>();
                            list.add(emplid);
                            catalogs.put(catalogKey, list);
                        }
                    }
                }
            } catch (ParseException e) {
                logger.error("error coming from API call." + e.toString());
            }

        }
        // assign the meeting info for students

        // for debugging purpose:
        // logger.debug("size of the class: " + emplids.size());
        // for (String id : emplids) {
        // logger.debug("emplid: " + id + "\n");
        // }
        // no students return from API server.
        if (emplids.size() == 0) {
            Exception e = new Exception();
            logger.error("There is no student roster information from API server.");
            throw e;
        }
        groups.forEach((key, value) -> {
            StringBuffer s = new StringBuffer();
            value.meetings.forEach((k, v) -> {
                s.append(v);
            });
            value.meeting = value.coursename + " " + value.type + " " + value.id + " " + s.toString();
            resGroup.add(value);
        });
        if (catalogs != null && catalogs.size() > 1) {
            catalogs.forEach((k, v) -> {
                Group g = new Group();
                g.id = k;
                g.coursename = k;
                g.meeting = k;
                // add the course group to each user
                v.forEach(item -> {
                    User user = users.get(item);
                    if (user != null) {
                        user.precept.add(g);
                    }
                });
                // add the course group to the group object
                resGroup.add(g);
            });
        }
        Token token = dataFetcherService.authorize();
        if (token != null) {
            images = dataFetcherService.getImages(token, emplids);
            // logger.debug(images);
        }
        if (!"".equals(images)) {
            JSONArray json = (JSONArray) parser.parse(images);
            JSONArray res = new JSONArray();
            Iterator<JSONObject> x = json.iterator();
            while (x.hasNext()) {
                JSONObject j = x.next();
                String emplid = null;
                emplid = (String) j.get("emplid");
                User user = users.get(emplid);
                user.photo = (String) j.get("photo");
            }
            // users.forEach((key, value) -> {
            // logger.debug(key + " => " + value);
            // });
            // logger.debug("=========\n");
            // logger.debug("groups: " + groups.size());
            // groups.forEach((key, value) -> {
            // logger.debug(key + " => " + value.toString());
            // });
            List<User> sortedUsers = new ArrayList<User>();
            users.forEach((emplid, user) -> {
                if ("".equals(user.photo)) {
                    user.photo = Constants.default_image;
                }
                sortedUsers.add(user);
            });
            // logger.debug("=========\n");
            // logger.debug("users: " + sortedUsers.size());
            // sortedUsers.forEach(user -> {
            // logger.debug(user.toString());
            // });
            Collections.sort(sortedUsers);
            sortedUsers.forEach(user -> {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    res.add(mapper.writeValueAsString(user));
                    // logger.debug(mapper.writeValueAsString(user));
                } catch (JsonProcessingException e) {
                    logger.error("can't parse user json" + e.toString());
                }
            });
            result.put("crs_name", "The history of American history");
            result.put("students", res);
            Collections.sort(resGroup);
            result.put("groups", resGroup);
        }
        return result;
    }

    private String ToGroupNameFromSSRComponent(String ssrComponent) {
        if (ssrComponent == null) {
            return null;
        }
        if ("CLS".equals(ssrComponent.trim())) {
            return "Class";
        } else if ("DRL".equals(ssrComponent.trim())) {
            return "Drill";
        } else if ("EAR".equals(ssrComponent.trim())) {
            return "Ear Training";
        } else if ("FLM".equals(ssrComponent.trim())) {
            return "Film";
        } else if ("LAB".equals(ssrComponent.trim())) {
            return "Labortary";
        } else if ("PRE".equals(ssrComponent.trim())) {
            return "Precept";
        } else if ("SEM".equals(ssrComponent.trim())) {
            return "Seminar";
        } else if ("STU".equals(ssrComponent.trim())) {
            return "Studio";
        } else {
            return null;
        }
    }
}