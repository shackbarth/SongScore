package com.songscoreapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("song")
public interface SongService extends RemoteService {
    String songServer(String name) throws IllegalArgumentException;
    boolean containsBadWord(String seedLine);
}
