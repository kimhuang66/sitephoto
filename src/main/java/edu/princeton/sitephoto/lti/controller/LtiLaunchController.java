package edu.princeton.sitephoto.lti.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.princeton.sitephoto.lti.data.LtiLaunchData;
import edu.princeton.sitephoto.lti.data.LtiSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LtiLaunchController {

    private static final Logger logger = LoggerFactory.getLogger(LtiLaunchController.class);

    @RequestMapping(value = "/launch", method = { RequestMethod.GET, RequestMethod.POST })
    public String ltiLaunch(@ModelAttribute LtiLaunchData ltiData, HttpSession session) throws Exception {
        // Invalidate the session to clear out any old data
        session.invalidate();
        logger.debug("launch!");
        String canvasCourseId = ltiData.getCustom_canvas_course_id();

        String eID = ltiData.getCustom_canvas_user_login_id();
        LtiSession ltiSession = new LtiSession();
        ltiSession.setApplicationName(getApplicationName());
        ltiSession.setCanvasDomain(ltiData.getCustom_canvas_api_domain());
        ltiSession.setInitialViewPath(getInitialViewPath());
        ltiSession.setEid(eID);
        ltiSession.setLtiLaunchData(ltiData);
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpSession newSession = sra.getRequest().getSession();
        newSession.setAttribute(LtiSession.class.getName(), ltiSession);
        logger.info("launching LTI integration '" + getApplicationName() + "' from " + ltiSession.getCanvasDomain()
                + " for course: " + canvasCourseId + " as user " + eID);
        logger.debug("forwarding user to: " + getInitialViewPath());
        // return "/beginOauth";
        return "forward:" + getInitialViewPath();
    }

    protected String getInitialViewPath() {
        return "/sitephotomenu";
    }

    protected String getApplicationName() {
        return "Photo Roster";
    }

}