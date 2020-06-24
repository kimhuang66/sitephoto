package edu.princeton.sitephoto.lti.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LtiSession {

    private String applicationName;
    private String initialViewPath;
    private String eid;
    private LtiLaunchData ltiLaunchData;
    private String canvasDomain;

}