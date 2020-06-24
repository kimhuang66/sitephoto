package edu.princeton.sitephoto.lti.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import edu.princeton.sitephoto.lti.data.LtiSession;
import edu.princeton.sitephoto.lti.exception.NoLtiSessionException;

@Service
public class LtiSessionService {
    public LtiSession getLtiSession() throws NoLtiSessionException {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest req = sra.getRequest();
        HttpSession session = req.getSession();
        LtiSession ltiSession = (LtiSession) session.getAttribute(LtiSession.class.getName());
        if (ltiSession == null) {
            throw new NoLtiSessionException();
        }
        return ltiSession;
    }

}