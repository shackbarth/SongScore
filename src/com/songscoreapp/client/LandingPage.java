/**
 * 
 */
package com.songscoreapp.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.songscoreapp.client.resources.Resources;

public class LandingPage extends Composite{
	
	interface LandingPageUiBinder extends UiBinder<Widget, LandingPage> {
	}
	
	static {
        JavascriptInjector.inject(Resources.INSTANCE.abcjs().getText());
        Resources.INSTANCE.css().ensureInjected();
	}

	private static LandingPageUiBinder uiBinder = GWT.create(LandingPageUiBinder.class);
	
	/**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    /**
     * Create a remote service proxy to talk to the server-side song service.
     */
    private final SongServiceAsync songService = GWT.create(SongService.class);
	
	@UiField
	Button sendButton;
	@UiField
	TextBox lyricsField;
	@UiField
	Label errorLabel;
	private PopupPanel loading = new PopupPanel();

	/**

	 */
	public LandingPage() {
		initWidget(uiBinder.createAndBindUi(this));
		
        // Focus the cursor on the lyrics field when the app loads
        lyricsField.setFocus(true);
        lyricsField.selectAll();
        lyricsField.getElement().setAttribute("placeholder", "Type seed lyric here");
        
        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        lyricsField.addKeyUpHandler(handler);
        
        //loading.setStyle("loadingPopup")
        loading.setWidget(new Label("Loading"));  
        loading.setGlassEnabled(true);
        loading.setModal(true);
	}
	
    // Create a handler for the sendButton and lyricsField
    class MyHandler implements ClickHandler, KeyUpHandler {
        /**
         * Fired when the user clicks on the sendButton.
         */
        @Override
        public void onClick(ClickEvent event) {
            requestSong();
        }

        /**
         * Fired when the user types in the nameField.
         */
        @Override
        public void onKeyUp(KeyUpEvent event) {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                requestSong();
            }
        }

        /**
         * Send the line of lyrics to the server and wait for a response.
         */
        private void requestSong() {
        	// show loading popup
            loading.setPopupPosition(Window.getClientWidth() / 2 - 50,  
                    Window.getClientHeight() / 2 - 45);  
            loading.show(); 
        	
            // First, we validate the input.
            errorLabel.setText("");
            String textToServer = lyricsField.getText();
            if (textToServer == null || textToServer.length() < 4) {
                errorLabel.setText("Please enter at least four characters");
                return;
            }

            // Then, we send the input to the server.
            songService.songServer(textToServer, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable caught) {
                    // Show the RPC error message to the user
                    errorLabel.setText(SERVER_ERROR);
                }

                @Override
                public void onSuccess(String result) {
                    renderAbcjs(result);
                    loading.setVisible(false);
                }
            });
        }
    }
    
  native String renderAbcjs(String abcString) /*-{
    $wnd.renderAbc("songOutputContainer", abcString, null, null, {startingTune: 0});
    $wnd.location.hash = "#songOutputContainer";
  }-*/;

}
