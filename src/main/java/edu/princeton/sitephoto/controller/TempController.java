package edu.princeton.sitephoto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.net.URLDecoder;
import java.util.Iterator;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthValidator;
import net.oauth.SimpleOAuthValidator;
import net.oauth.server.HttpRequestMessage;
import net.oauth.server.OAuthServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.princeton.sitephoto.config.SitephotoProperties;
import edu.princeton.sitephoto.lti.data.LtiSession;
import edu.princeton.sitephoto.lti.exception.NoLtiSessionException;
import edu.princeton.sitephoto.lti.service.LtiSessionService;

@RestController
public class TempController {
    private static final Logger logger = LoggerFactory.getLogger(TempController.class);

    public static final String HELLO_TEXT = "Hello from Spring Boot Backend!";
    public static final String SECURED_TEXT = "Hello from the secured resource!";
    public static final String LAUNCH_TEXT = "Hello from launch new!";

    @Autowired
    SitephotoProperties props;

    @Autowired
    LtiSessionService ltiSessionService;

    @RequestMapping(value = { "/lti/config.xml" }, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String lti_config(HttpServletRequest request, HttpServletResponse response) {
        StringBuffer url = request.getRequestURL();
        String uri = request.getRequestURI();
        int idx = (((uri != null) && (uri.length() > 0)) ? url.indexOf(uri) : url.length());
        String host = url.substring(0, idx); // base url
        String launchUrl = host + "/launch";
        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
                + "<head><title>Student Attendance Reporting LTI integration</title></head>" + "<body>"
                + "<h2>Student Photo Roster LTI integration</h2>"
                + "<p>In order to use this LTI plugin for Canvas, please follow these steps:</p>" + "<ol>"
                + "    <li>Navigate to the account settings</li>" + "    <li>Click on the \"Apps\" tab</li>"
                + "    <li>Click \"View App Configurations\"</li>" + "    <li>Click \"Add New App\"</li>"
                + "    <li>Enter \"Photo Roster\" in the Name field</li>"
                + "    <li>Obtain Consumer Key and Shared Secret from an administrator</li>"
                + "    <li>In the \"Configuration Type\" dropdown select: Paste XML</li>"
                + "    <li>Copy and paste XML configuration listed below</li>" + "    <li>Submit the form</li>"
                + "    <li>Navigate to your institution account page. There should now be a \"Photo Roster\" link in the account navigation bar on the left.</li>"
                + "</ol>" + "" + "    <pre>" + "----------- Start copying the next line ----------- <br />"
                + "&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;<br />"
                + "&lt;cartridge_basiclti_link xmlns=\"http://www.imsglobal.org/xsd/imslticc_v1p0\"<br />"
                + "    xmlns:blti = \"http://www.imsglobal.org/xsd/imsbasiclti_v1p0\"<br />"
                + "    xmlns:lticm =\"http://www.imsglobal.org/xsd/imslticm_v1p0\"<br />"
                + "    xmlns:lticp =\"http://www.imsglobal.org/xsd/imslticp_v1p0\"<br />"
                + "    xmlns:xsi = \"http://www.w3.org/2001/XMLSchema-instance\"<br />"
                + "    xsi:schemaLocation = \"http://www.imsglobal.org/xsd/imslticc_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticc_v1p0.xsd<br />"
                + "    http://www.imsglobal.org/xsd/imsbasiclti_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imsbasiclti_v1p0.xsd<br />"
                + "    http://www.imsglobal.org/xsd/imslticm_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticm_v1p0.xsd<br />"
                + "    http://www.imsglobal.org/xsd/imslticp_v1p0 http://www.imsglobal.org/xsd/lti/ltiv1p0/imslticp_v1p0.xsd\"&gt;<br />"
                + "    &lt;blti:title&gt;Class Photos&lt;/blti:title&gt;<br />"
                + "    &lt;blti:description&gt;Provides searchable students' photos of the class&lt;/blti:description&gt;<br />"
                + "    &lt;blti:launch_url&gt;" + launchUrl + "&lt;/blti:launch_url&gt;<br />"
                + "    &lt;blti:extensions platform=\"canvas.instructure.com\"&gt;<br />"
                + "      &lt;lticm:property name=\"privacy_level\"&gt;public&lt;/lticm:property&gt;<br />"
                + "      &lt;lticm:options name=\"course_navigation\"&gt;<br />"
                + "        &lt;lticm:property name=\"enabled\"&gt;true&lt;/lticm:property&gt;<br />"
                + "        &lt;lticm:property name=\"visibility\"&gt;members&lt;/lticm:property&gt;<br />"
                + "      &lt;/lticm:options&gt;<br />" + "&lt;/blti:extensions&gt;<br />"
                + "&lt;/cartridge_basiclti_link&gt;<br />" + "----------- Stop. Do not copy this line -----------<br />"
                + "    </pre>" + "" + "</body>" + "</html>";
        return str;

    }

    @RequestMapping(value = { "/hello" }, method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String hello(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("inside of lti");
        return "Hello";
    }

    // @RequestMapping(value = { "/params" }, method = { RequestMethod.GET,
    // RequestMethod.POST })
    // LTI parameters: tool_consumer_info_product_family_code: BlackboardLearn
    // resource_link_title: vue on oas-kimhuang18
    // context_title: AAS235-SOC236_S2019 Race Is Socially Constructed: Now What?
    // roles: urn:lti:role:ims/lis/Instructor
    // lis_person_name_family: Huang
    // tool_consumer_instance_name: Blackboard, Inc.
    // tool_consumer_instance_guid: eb9d73bd45564819a6b0ba42a25ece49
    // custom_context_memberships_url:
    // http://dev.bbdn.local:1234/learn/api/v1/lti/external/memberships/4df9f6fa1e69417288873e2ff894fb0a?placement_id=_8_1
    // resource_link_id: vue on oas-kimhuang18_8_1
    // oauth_signature_method: HMAC-SHA1 oauth_version: 1.0
    // custom_caliper_profile_url:
    // http://oas-kimhuang18.princeton.edu:80/learn/api/v1/telemetry/caliper/profile/vue%20on%20oas-kimhuang18_8_1
    // //
    // launch_presentation_return_url:
    // http://dev.bbdn.local:1234/webapps/blackboard/execute/blti/launchReturn?course_id=_8_1&launch_time=1564691273734&launch_id=84926cfc-528f-4d8a-ba60-72ffa60fa863&link_id=vue
    // on oas-kimhuang18_8_1
    // ext_launch_id: 84926cfc-528f-4d8a-ba60-72ffa60fa863 ext_lms:
    // bb-3400.0.0-rel.39+2253b01
    // lti_version: LTI-1p0 lis_person_contact_email_primary: kimhuang@Princeton.EDU
    // oauth_signature: Sfm5fqYH4CNADK1lbPIeBeFcu+I=
    // tool_consumer_instance_description: Blackboard, Inc.
    // oauth_consumer_key: keyPhotoRoster
    // launch_presentation_locale: en-US
    // custom_caliper_federated_session_id:
    // https://caliper-mapping.cloudbb.blackboard.com/v1/sites/5dee2ff2-960c-4f17-be9d-d339cb063ad4/sessions/FE19D9B94C63C4398B845C81EF8B28E1
    // lis_person_sourcedid: 961097276
    // oauth_timestamp: 1564691273
    // lis_person_name_full: Kim Huang
    // tool_consumer_instance_contact_email: dev@bbdn.local
    // lis_person_name_given: Kim
    // custom_tc_profile_url:
    // http://dev.bbdn.local:1234/learn/api/v1/lti/profile?lti_version=LTI-1p0
    // oauth_nonce: 430037542943
    // lti_message_type: basic-lti-launch-request
    // user_id: 91a8191b0466498092081fff4f56aef7
    // oauth_callback: about:blank
    // tool_consumer_info_version: 3400.0.0-rel.39+2253b01
    // context_id: 4df9f6fa1e69417288873e2ff894fb0a
    // context_label: AAS235_SOC236_S2019
    // launch_presentation_document_target: window
    // ext_launch_presentation_css_url:
    // http://dev.bbdn.local:1234/common/shared.css,http://dev.bbdn.local:1234/themes/as_2015/theme.css,http://dev.bbdn.local:1234/branding/_1_1/brand.css?ts=1549984227098
    @RequestMapping(path = "/params")
    public String params(HttpServletRequest request, HttpServletResponse response) {
        String parmsForDisplay = "LTI parameters: ";
        Map parameters = request.getParameterMap();
        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            parmsForDisplay += key + ": ";
            String[] values = (String[]) (parameters.get(key));
            for (int j = 0; j < values.length; j++) {
                if (j != 0) {
                    parmsForDisplay += ",&nbsp";
                }
                parmsForDisplay += values[j];
                parmsForDisplay += "&nbsp&nbsp";
            }
        }
        return parmsForDisplay;
    }

    @RequestMapping(path = "/launchnew")
    public @ResponseBody String launchnew(HttpServletRequest request, HttpServletResponse response) {
        String crs_id = request.getParameter("crs_id");
        String crs_name = "American History";

        // String res = "<html><script
        // src=\"https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js\"></script>" +
        // "<body>"
        // + "<div id=\"app\">" + "<ph :crs_id=" + crs_id + " ></ph> " +
        // "<h2>{{product}}</h2>" + "</div>"
        // + "<script src=\"../js/main.js\"></script></body></html>";

        // String res = "<html><head>" + "<link rel = \"stylesheet\"" + " type =
        // \"text/css\""
        // + " href = \"../css/product.css\"" + "</head>"
        // + "<script
        // src=\"https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js\"></script>"
        // + "<body class=\"sitephotos\"><div id=\"app\">"
        // + "<div class=\"cart\"><p>Cart({{cart.length}})</p></div>"
        // + "<product :premium=\"premium\" :cart=\"cart.length\"
        // @add-to-cart=\"addCart\" @remove-from-cart=\"removeCart\"></product></div>"
        // + "<script src=\"../js/main.js\"></script></body></html>";

        String res = "<html><head>" + "<link rel = \"stylesheet\"" + " type = \"text/css\""
                + " href = \"../css/style.css\"" + "</head>"
                + "<script src=\"https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js\"></script>"
                + "<script src=\"https://unpkg.com/axios/dist/axios.min.js\"></script>"
                // + "<script src=\"https://unpkg.com/vuex@3.1.1/dist/vuex.js\"></script>"
                + "<body class=\"sitephotos\"><div id=\"app\" crs_id=\"" + crs_id + "\" crs_name=\"" + crs_name + "\">"
                + "</div>" + "<script src=\"../js/main.js?dev=" + Math.floor(Math.random() * 100)
                + "\"></script></body></html>";
        logger.debug("html string: " + res);
        return res;
    }

    @RequestMapping(path = "/test")
    // http://localhost:8080/test?course=COS126_S2020&name=test
    public @ResponseBody String test(HttpServletRequest request, HttpServletResponse response) {
        try {
            // if (authenticate(request, props)) {
            // logger.debug("authenticated");
            String crs_id = request.getParameter("course");
            String crs_name = request.getParameter("name");
            // String user_uuid = request.getParameter("user_id");
            // String[] roles = request.getParameterValues("roles");
            // for (String role : roles) {
            // logger.debug("roles: " + role);
            // if (isAuthorized(role) == true) {
            String res = "<html><head>" + "<link rel = \"stylesheet\"" + " type = \"text/css\""
                    + " href = \"../css/style.css\"" + "</head>"
                    + "<script src=\"https://cdn.jsdelivr.net/npm/vue/dist/vue.js\"></script>"
                    + "<script src=\"https://unpkg.com/axios/dist/axios.min.js\"></script>"
                    // + "<script src=\"https://unpkg.com/vuex@3.1.1/dist/vuex.js\"></script>"
                    + "<body class=\"sitephotos\"><div id=\"app\" crs_id=\"" + crs_id + "\" crs_name=\"" + crs_name
                    + "\">" + "</div>" + "<script src=\"../js/main.js?dev=" + Math.floor(Math.random() * 100)
                    + "\"></script></body></html>";
            logger.debug("html string: " + res);
            return res;
            // }
            // }
            // }
        } catch (Exception e) {
            logger.debug("Exception thrown when launch, " + e.toString());
            return "Error launch the photo roster: " + e.toString();
        }
    }

    @RequestMapping(path = "/sitephotomenu")
    public @ResponseBody String launch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // try {
        // if (authenticate(request, props)) {
        // logger.debug("authenticated");
        // String crs_id = request.getParameter("context_label");
        // String crs_name = request.getParameter("context_title");
        // String user_uuid = request.getParameter("user_id");
        // String[] roles = request.getParameterValues("roles");
        // for (String role : roles) {
        // // logger.debug("roles: " + role);
        // if (isAuthorized(role) == true) {
        LtiSession ltiSession = null;
        try {
            ltiSession = ltiSessionService.getLtiSession();
        } catch (NoLtiSessionException e) {
            logger.debug("no lti session ");
        }
        if (ltiSession.getEid() == null || ltiSession.getEid().isEmpty()) {
            throw new Exception("You cannot access this content without a valid session");
        }
        String crs_id = ltiSession.getLtiLaunchData().getContext_label();
        String crs_name = ltiSession.getLtiLaunchData().getContext_title();
        String res = "<html><head>" + "<link rel = \"stylesheet\"" + " type = \"text/css\""
                + " href = \"../css/style.css\"" + "</head>"
                + "<script src=\"https://cdn.jsdelivr.net/npm/vue@2.6.10/dist/vue.js\"></script>"
                + "<script src=\"https://unpkg.com/axios/dist/axios.min.js\"></script>"
                // + "<script src=\"https://unpkg.com/vuex@3.1.1/dist/vuex.js\"></script>"
                + "<body class=\"sitephotos\"><div id=\"app\" crs_id=\"" + crs_id + "\" crs_name=\"" + crs_name + "\">"
                + "</div>" + "<script src=\"../js/main.js?dev=" + Math.floor(Math.random() * 100)
                + "\"></script></body></html>";
        logger.debug("html string: " + res);
        return res;
    }

    private boolean hasRole(String roles, String title) {
        logger.debug("roles: " + roles);
        String[] each = roles.split(",");
        if (each != null) {
            for (String role : each) {
                if (role.trim().equals(title.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStaff(String role) {
        if (hasRole(role, "Instructor") || hasRole(role, "urn:lti:role:ims/lis/Instructor")
                || hasRole(role, "urn:lti:role:ims/lis/TeachingAssistant")
                || hasRole(role, "urn:lti:role:ims/lis/ContentDeveloper")) {
            return true;
        }
        return false;
    }

    private boolean isAdmin(String role) {
        if (hasRole(role, "Administrator") || hasRole(role, "urn:lti:sysrole:ims/lis/SysAdmin")
                || hasRole(role, "urn:lti:sysrole:ims/lis/Administrator")
                || hasRole(role, "urn:lti:instrole:ims/lis/Administrator")) {
            return true;
        }
        return false;
    }

    private boolean isAuthorized(String roles) {

        if (isStaff(roles) || isAdmin(roles)) {
            return true;
        }
        return false;
    }

    public boolean authenticate(HttpServletRequest request, SitephotoProperties props) throws Exception {

        OAuthValidator validator = new SimpleOAuthValidator();
        String oauth_consumer_key = null;

        try {
            OAuthMessage oauthMessage = OAuthServlet.getMessage(request, null);
            oauth_consumer_key = oauthMessage.getConsumerKey();

            // callback URL syntax per LTI spec
            OAuthConsumer consumer = new OAuthConsumer("about:blank", oauth_consumer_key, props.getSecret(), null);

            String signatureMethod = oauthMessage.getSignatureMethod();
            String signature = URLDecoder.decode(oauthMessage.getSignature(), "UTF-8");

            // all tokens are empty
            OAuthAccessor accessor = new OAuthAccessor(consumer);
            validator.validateMessage(oauthMessage, accessor);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            throw new Exception(e);
        }

        return true;
    }
}