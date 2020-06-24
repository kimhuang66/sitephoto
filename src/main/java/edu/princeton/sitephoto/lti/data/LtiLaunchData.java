package edu.princeton.sitephoto.lti.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class LtiLaunchData {
    private static final Logger logger = LoggerFactory.getLogger(LtiLaunchData.class);

    private String lti_version;

    private String context_label;

    private String resource_link_id;

    private String lis_person_name_family;

    private Integer launch_presentation_width;

    private String custom_canvas_user_login_id;

    private String custom_canvas_enrollment_state;

    private String launch_presentation_return_url;

    private String tool_consumer_info_version;

    private String tool_consumer_instance_contact_email;

    private String user_id;

    private String custom_canvas_api_domain;

    private String lti_message_type;

    private String tool_consumer_info_product_family_code;

    private String tool_consumer_instance_guid;

    private String user_image;

    private String launch_presentation_document_target;

    private String context_title;

    private String tool_consumer_instance_name;

    private String custom_canvas_course_id;

    private String lis_person_sourcedid;

    private String lis_person_name_full;

    private Integer launch_presentation_height;

    private String resource_link_title;

    private String context_id;

    private String custom_canvas_user_id;

    private String lis_person_contact_email_primary;

    private String lis_person_name_given;

    private String launch_presentation_locale;

    public enum InstitutionRole {
        Sysadmin, SysSupport, Creator, AccountAdmin, User, ContentDeveloper, Manager, Student, Faculty, Member, Learner,
        Instructor, TeachingAssistant, Mentor, Staff, Alumni, ProspectiveStudent, Guest, Other, Administrator, Observer,
        None;

        private static Map<String, InstitutionRole> roleMap = new HashMap<>();
        private List<InstitutionRole> rolesList;
        private String roles;

        static {
            // system roles
            roleMap.put("urn:lti:sysrole:ims/lis/SysAdmin", Sysadmin);
            roleMap.put("SysAdmin", Sysadmin);
            roleMap.put("urn:lti:sysrole:ims/lis/SysSupport", SysSupport);
            roleMap.put("SysSupport", SysSupport);
            roleMap.put("urn:lti:sysrole:ims/lis/Creator", Creator);
            roleMap.put("Creator", Creator);
            roleMap.put("urn:lti:sysrole:ims/lis/AccountAdmin", AccountAdmin);
            roleMap.put("AccountAdmin", AccountAdmin);
            roleMap.put("urn:lti:sysrole:ims/lis/User", User);
            roleMap.put("User", User);
            roleMap.put("urn:lti:sysrole:ims/lis/Administrator", Administrator);
            roleMap.put("Administrator", Administrator);
            roleMap.put("urn:lti:sysrole:ims/lis/None", None);
            roleMap.put("None", None);

            // institution roles
            roleMap.put("urn:lti:instrole:ims/lis/Student", Student);
            roleMap.put("Student", Student);
            roleMap.put("urn:lti:instrole:ims/lis/Faculty", Faculty);
            roleMap.put("Faculty", Faculty);
            roleMap.put("urn:lti:instrole:ims/lis/Member", Member);
            roleMap.put("Member", Member);
            roleMap.put("urn:lti:instrole:ims/lis/Learner", Learner);
            roleMap.put("Learner", Learner);
            roleMap.put("urn:lti:instrole:ims/lis/Instructor", Instructor);
            roleMap.put("Instructor", Instructor);
            roleMap.put("urn:lti:instrole:ims/lis/Mentor", Mentor);
            roleMap.put("Mentor", Mentor);
            roleMap.put("urn:lti:instrole:ims/lis/Staff", Staff);
            roleMap.put("Staff", Staff);
            roleMap.put("urn:lti:instrole:ims/lis/Alumni", Alumni);
            roleMap.put("Alumni", Alumni);
            roleMap.put("urn:lti:instrole:ims/lis/ProspectiveStudent", ProspectiveStudent);
            roleMap.put("ProspectiveStudent", ProspectiveStudent);
            roleMap.put("urn:lti:instrole:ims/lis/Guest", Guest);
            roleMap.put("Guest", Guest);
            roleMap.put("urn:lti:instrole:ims/lis/Other", Other);
            roleMap.put("Other", Other);
            roleMap.put("urn:lti:instrole:ims/lis/Administrator", Administrator);
            // short version of Administrator already done up above
            roleMap.put("urn:lti:instrole:ims/lis/Observer", Observer);
            roleMap.put("Observer", Observer);
            roleMap.put("urn:lti:instrole:ims/lis/None", None);
            // short version of None already done up above

            // context roles - does not include subroles yet
            roleMap.put("urn:lti:role:ims/lis/Learner", Learner);
            roleMap.put("urn:lti:role:ims/lis/Instructor", Instructor);
            roleMap.put("urn:lti:role:ims/lis/ContentDeveloper", ContentDeveloper);
            roleMap.put("ContentDeveloper", ContentDeveloper);
            roleMap.put("urn:lti:role:ims/lis/Member", Member);
            roleMap.put("urn:lti:role:ims/lis/Manager", Manager);
            roleMap.put("Manager", Manager);
            roleMap.put("urn:lti:role:ims/lis/Mentor", Mentor);
            roleMap.put("urn:lti:role:ims/lis/Administrator", Administrator);
            roleMap.put("urn:lti:role:ims/lis/TeachingAssistant", TeachingAssistant);
            roleMap.put("TeachingAssistant", TeachingAssistant);

        }

        public static InstitutionRole fromString(String roleStr) {
            InstitutionRole role = roleMap.get(roleStr);
            if (role == null) {
                throw new IllegalArgumentException("Unknown LTI Institution role string");
            }
            return role;
        }

        public void setRoles(String roles) {
            logger.debug("got LTI roles: " + roles);
            this.roles = roles;

            List<InstitutionRole> list = new ArrayList<>();
            String[] splitRoles = StringUtils.split(StringUtils.trimToEmpty(roles), ",");
            for (int i = 0; i < splitRoles.length; i++) {
                list.add(InstitutionRole.fromString(splitRoles[i]));
            }
            this.rolesList = list;
        }
    }

}