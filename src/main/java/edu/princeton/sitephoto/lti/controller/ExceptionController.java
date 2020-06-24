package edu.princeton.sitephoto.lti.controller;

import edu.princeton.sitephoto.lti.exception.NoLtiSessionException;

import org.springframework.stereotype.Controller;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Controller
@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NoLtiSessionException.class)
    public ModelAndView handleNoLtiSessionException(NoLtiSessionException e) {
        return new ModelAndView("NoLtiSession");
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    public ModelAndView handleSessionExpired(HttpSessionRequiredException e) {
        return new ModelAndView("NoLtiSession");
    }
}
