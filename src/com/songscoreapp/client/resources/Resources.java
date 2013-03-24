package com.songscoreapp.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);

    @Source("abcjs_basic_1.0.14-min.js")
    TextResource abcjs();
    
    @Source("loading.gif")
    ImageResource loading();
    
    @Source("robot.png")
    ImageResource robot();
    
    @Source("b.png")
    ImageResource spacer();
    
    @Source("SongScore.css")
    Style css();

	public interface Style extends CssResource {
		String wrapper();
		String gridWrapper();
		String contentWrapper();
		String lyricsTextBox();
		String lyricsLabel();
		String submitButton();
		String songSheet();
		String loadingPopup();
		String grid();
		String form();
		String error();
	}

}