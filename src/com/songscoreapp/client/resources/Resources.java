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
    
    @Source("SongScore.css")
    Style css();

	public interface Style extends CssResource {
		String wrapper();
		String container();
		String button();
		String submitButton();
		String songSheet();
		String form();
		String lyricsField();
		String lyrics();
		String border();
		String gender();
		String voice();
		String dropdownStuff();
		String reset();
		String loadingPopup();
	}

}