package com.songscoreapp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.songscoreapp.client.resources.AbcjsBundle;
import com.songscoreapp.shared.FieldVerifier;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SongScore implements EntryPoint {
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

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        AbcjsBundle bundle = GWT.create(AbcjsBundle.class);
        JavascriptInjector.inject(bundle.abcjs().getText());

        final Button sendButton = new Button("Send");
        final TextBox lyricsField = new TextBox();
        lyricsField.setText("Type lyrics here");
        lyricsField.setStylePrimaryName("lyricsField");
        final Label errorLabel = new Label();

        sendButton.setStylePrimaryName("submitButton");

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("lyricsContainer").add(lyricsField);
        RootPanel.get("buttonContainer").add(sendButton);
        RootPanel.get("errorLabelContainer").add(errorLabel);

        // Focus the cursor on the lyrics field when the app loads
        lyricsField.setFocus(true);
        lyricsField.selectAll();

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
                // First, we validate the input.
                errorLabel.setText("");
                String textToServer = lyricsField.getText();
                if (!FieldVerifier.isValidName(textToServer)) {
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
                    }
                });
            }
        }

        // Add a handler to send the name to the server
        MyHandler handler = new MyHandler();
        sendButton.addClickHandler(handler);
        lyricsField.addKeyUpHandler(handler);
    }

    native String renderAbcjs(String abcString) /*-{
      $wnd.renderAbc("songOutputContainer", abcString, null, null, {startingTune: 0});
      // I'm about 100% sure that there's a more robust way to apply CSS dynamically.
      $wnd.document.getElementById("songOutputContainer").className += "songSheet";
      $wnd.location.hash = "#songOutputContainer";
    }-*/;
}
