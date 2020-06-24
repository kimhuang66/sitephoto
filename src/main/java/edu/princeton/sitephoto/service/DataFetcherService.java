package edu.princeton.sitephoto.service;

import java.util.List;

import edu.princeton.sitephoto.data.Token;

public interface DataFetcherService {
    public String getRoster(Token token, String crs_id) throws Exception;

    public Token authorize() throws Exception;

    public String getImages(Token token, List<String> emplids) throws Exception;

}