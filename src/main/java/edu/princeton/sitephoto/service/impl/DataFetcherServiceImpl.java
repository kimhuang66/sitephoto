package edu.princeton.sitephoto.service.impl;

import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import edu.princeton.sitephoto.Constants;
import edu.princeton.sitephoto.Util;
import edu.princeton.sitephoto.config.SitephotoProperties;
import edu.princeton.sitephoto.service.DataFetcherService;
import edu.princeton.sitephoto.data.CourseMemberRaw;
import edu.princeton.sitephoto.data.Token;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataFetcherServiceImpl implements DataFetcherService {

    @Autowired
    SitephotoProperties props;

    private static final Logger logger = LoggerFactory.getLogger(DataFetcherServiceImpl.class);

    private Token getToken(URI uri, String auth) {
        Token token = null;
        RestTemplate restTemplate = new RestTemplate();
        String hash = Base64.getEncoder().encodeToString(auth.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + hash);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> request = new HttpEntity<String>("grant_type=client_credentials", headers);
        // logger.debug("before sending out request for token.");
        ResponseEntity<Token> response = restTemplate.exchange(uri, HttpMethod.POST, request, Token.class);
        token = response.getBody();
        return token;
    }

    // @Cacheable("accessToken")
    public Token authorize() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        URI uri = null;
        Token token = null;

        try {
            uri = new URI(Constants.APIS_URL + Constants.AUTH_PATH);

            logger.debug("uri is : " + uri);
            // String auth = Constants.REST_KEY + ":" + Constants.REST_SECRET;
            String auth = props.getRestKey() + ":" + props.getRestSecret();
            logger.debug("the auth: " + auth);
            token = getToken(uri, auth);
            // if (token != null) {
            // logger.debug("the access_token is " + token.getAccess_token());
            // logger.debug("token will expire in : " + token.getExpires_in());
            // } else {
            // logger.debug("token is null");
            // }
        } catch (URISyntaxException e) {
            throw new Exception(e);
        }
        // logger.debug("token is: " + token.getAccess_token());
        return token;
    }

    public String getRoster(Token token, String crs_id) throws Exception {
        List<CourseMemberRaw> cmrList = new ArrayList<CourseMemberRaw>();
        RestTemplate restTemplate = new RestTemplate();
        URI uri = null;
        ResponseEntity<String> response = null;
        String crsDetails = "";

        try {
            uri = new URI(Constants.APIS_URL + Constants.ROSTER_PATH);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token.getAccess_token());
            // headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(getBody(crs_id), headers);
            // logger.info("Request Body: " + request.getBody());
            response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            logger.debug("Excption in getRoster: " + e.toString());
            HttpStatus code = response.getStatusCode();
            if (code.value() == 401) {
                try {
                    token = authorize();
                    HttpHeaders headers = new HttpHeaders();
                    headers.add("Authorization", "Bearer " + token.getAccess_token());
                    // headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> request = new HttpEntity<String>(getBody(crs_id), headers);
                    response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
                } catch (Exception ex) {
                    logger.debug("Exception: " + ex.toString());
                }
            } else {
                logger.debug("Excption: " + e.toString());
                throw new Exception(e);
            }
        }
        if (response != null) {
            crsDetails = response.getBody();
            // logger.debug("crsDetails: " + crsDetails);
        }
        return crsDetails;
    }

    // {"strm":"1184","courses":["AAS200", "AAS254", "AAS235",
    // "ANT208"]}}
    private String getBody(String crs_id) {
        if (crs_id == null || crs_id.equals("")) {
            return "";
        }
        String strm = "";
        // AAS200_S2019
        String term = crs_id.split("_")[1];
        String first = crs_id.split("_")[0];
        String[] crsS = first.split("-");
        Map<String, Object> map = new LinkedHashMap();
        try {
            strm = Util.semesterToStrm(term);
        } catch (Exception e) {
            logger.error("can't convert strm.");
            return null;
        }
        logger.debug("term: " + term + "," + "first: " + first + " ," + "strm: " + strm);
        map.put("strm", strm);
        map.put("courses", crsS);
        ObjectMapper objMapper = new ObjectMapper();
        String body = "";
        try {
            // body = objMapper.writeValueAsString(node);
            body = objMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.debug("body is : " + body);
        return (body);
    }

    @Override
    public String getImages(Token token, List<String> emplids) throws Exception {

        String[] s = emplids.toArray(new String[0]);
        Map<String, Object> map = new LinkedHashMap();
        ObjectMapper objMapper = new ObjectMapper();
        String body = "";
        map.put("size", "360x270");
        map.put("ids", s);
        try {
            // body = objMapper.writeValueAsString(node);
            body = objMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        logger.debug("body is : " + body);
        RestTemplate restTemplate = new RestTemplate();
        URI uri = null;
        ResponseEntity<String> response = null;
        String images = "";

        try {
            uri = new URI(Constants.APIS_URL + Constants.IMAGE_PATH);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token.getAccess_token());
            // headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<String>(body, headers);
            logger.info("Request Body: " + request.getBody());
            response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            logger.debug("Excption in getRoster: " + e.toString());
            HttpStatus code = response.getStatusCode();
            if (code.value() == 401) {
                token = authorize();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token.getAccess_token());
                // headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> request = new HttpEntity<String>(body, headers);
                response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            } else {
                logger.debug("Exception: " + e.toString());
                throw new Exception(e);
            }
        }
        if (response != null) {
            images = response.getBody();
            // logger.debug("crsDetails: " + images);
        }
        return images;
    }

}