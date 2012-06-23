package com.songscoreapp.server.generator;


public class Renderer {
    public static String renderHtmlFromAbc(String abc) {
        StringBuffer htmlOut = new StringBuffer();
        htmlOut.append("<html><head>");
        htmlOut.append("<script src='http://drawthedots.com/abcplugin/abcjs_plugin_1.0.13-min.js' type='text/javascript'></script>");
        htmlOut.append("<script type='text/javascript'>");
        htmlOut.append("  jQuery.noConflict();");
        htmlOut.append("</script>");

        htmlOut.append("<script type='text/javascript'>");
        htmlOut.append("  abc_plugin['show_midi'] = false;");
        htmlOut.append("  abc_plugin['hide_abc'] = true;");
        htmlOut.append("</script>");
        htmlOut.append("</head><body>");
        htmlOut.append("<pre>");
        htmlOut.append(abc);
        htmlOut.append("</pre>");
        htmlOut.append("</body></html>");

        return htmlOut.toString();
    }
}
