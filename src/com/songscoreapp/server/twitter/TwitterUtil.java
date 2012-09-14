package com.songscoreapp.server.twitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.songscoreapp.server.generator.Lyrics;
import com.songscoreapp.server.generator.SyllableUtil;

public class TwitterUtil {

    /**
     * The master function that puts all the twitter work together and returns a usable list
     * of lines
     *
     * @param contextWord The word that tries to keep the whole verse on the same-ish topic
     * @param rhymeWord A word we're looking to use as a rhyme
     * @param hashtagContext True if we want to try putting a hashtag on the context work. False if we
     *  need to relax the search.
     * @return A usable list of lines of lyrics
     */
    public static List<String> getTwitterLines(String contextWord, String rhymeWord, boolean hashtagContext) {
        List<String> lyrics = new ArrayList<String>();
        String query = (hashtagContext ? "#" : "")
                + contextWord + " " + rhymeWord;

        try {
            List<String> lines = TwitterAPI.getTweetsFromQuery(query);
            for(String line : lines) {
                String strippedLine = stripJunk(line);
                //String fragment = getFragment(strippedLine, rhymeWord);
                if(strippedLine != null
                        && isUsableLine(strippedLine)
                        && lyrics.indexOf(strippedLine) < 0) {
                    lyrics.add(strippedLine);
                }
            }

        } catch (Exception e) {
            // oh well
            e.printStackTrace();
        }
        return lyrics;
    }

    /**
     * Passes final judgment whether this tweet is really salvageable
     *
     * @param line
     * @return
     */
    public static boolean isUsableLine(String line) {
        //System.out.println("Is usable? " + line);
        if(line == null || line.length() == 0) {
            return false;
        } else if(SyllableUtil.getSyllableCountFromLine(line) < 4) {
            return false;
        } else if(line.contains("http")) {
            return false;
        }

        // 3 bogus in a row is a bad sign
        String[] words = line.split("\\s+");
        int consecutiveBogus = 0;
        for(String word : words) {
            if(isBogusWord(word)) {
                consecutiveBogus++;
            } else {
                consecutiveBogus = 0;
            }
            if(consecutiveBogus > 2) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tweets are usually too long for a line of lyrics, so we chop them up into usable
     * chunks. We prefer to chop on punctuation marks, and hopefully some of the lines
     * will end with a rhyme word. If none do, then we have to "rhymeChop", by chopping
     * the lines based on the presence of rhymewords. This guarantees rhyming but
     * sometimes the line feels cutoff which is why we only do it as a last resort.
     *
     * @param lines
     * @param rhymes if null then assume we want to chop on punctuation only
     * @return
     */
    public static List<String> chopLines(List<String> lines, List<String> rhymes) {
        List<String> choppedLines = new ArrayList<String>();
        for(String line : lines) {
            choppedLines.addAll(chopLine(line));
            if(rhymes != null) {
                choppedLines.add(rhymechopLine(line, rhymes));
            }
        }
        return choppedLines;
    }

    /**
     * Chop on punctuation
     *
     * @param line
     * @return
     */
    public static List<String> chopLine(String line) {
        int lineLength = Lyrics.IDEAL_LINE_LENGTH; // TODO: actually get this from input
        if(SyllableUtil.getSyllableCountFromLine(line) <
                (lineLength + Lyrics.SYLLABLE_DIFFERENCE_THRESHOLD) * 2) {
            // we don't want to chop up the line unless it's too long to use
            // even if it has punctuation
            return Arrays.asList(new String[] {line});
        }
        List<String> choppedLines = new ArrayList<String>();
        boolean lastFragment = false;
        do {
            int punctuationIndex = indexOfPunctuation(line);
            String fragment;

            if(punctuationIndex >= 0) {
                fragment = line.substring(0, punctuationIndex + 1).trim();
            } else {
                // last fragment
                fragment = line.trim();
                lastFragment = true;
            }
            int fragmentSyllableCount = SyllableUtil.getSyllableCountFromLine(fragment);
            String previousLine = choppedLines.size() == 0 ? null : choppedLines.get(choppedLines.size() - 1);
            int previousLineSyllableCount = SyllableUtil.getSyllableCountFromLine(previousLine);
            if(fragmentSyllableCount < lineLength - Lyrics.SYLLABLE_DIFFERENCE_THRESHOLD
                    && previousLine != null
                    && previousLineSyllableCount + fragmentSyllableCount <= (lineLength + Lyrics.SYLLABLE_DIFFERENCE_THRESHOLD) * 2) {
                // this new fragment is too short. Add it to the previous line unless that one's getting too long
                int indexToUpdate = choppedLines.size() - 1;
                choppedLines.set(indexToUpdate, (previousLine + " " + fragment).trim());

            } else {
                // this fragment deserves a new line
                choppedLines.add(fragment);
            }
            line = line.substring(punctuationIndex + 1);

        } while (!lastFragment);
        return choppedLines;
    }

    // could probably be done with regex but indexOf doesn't use regex
    protected static int indexOfPunctuation(String line) {
        String[] punctuationArray = {".", ";", ",", "!", "?"};
        int lowestIndex = 9999;
        for(String mark : punctuationArray) {
            int currentIndex = line.indexOf(mark);
            if(currentIndex >= 0 && currentIndex < lowestIndex) {
                lowestIndex = currentIndex;
            }
        }
        return lowestIndex < 9999 ? lowestIndex : -1;
    }

    /**
     * Typically we chop lines based on punctuation marks because those lines
     * make the most sense. But if we're struggling to find rhymes we can
     * shop based on the appearance of rhymes
     *
     * @param line
     * @param rhyme
     * @return
     */
    public static String rhymechopLine(String line, List<String> rhymes) {
        String fragment = "";
        String[] words = line.split("\\s+");
        for(String word : words) {
            fragment = fragment + word + " ";
            if(rhymes.indexOf(Lyrics.trimPunctuation(word).toLowerCase()) >= 0) {
                break;
            }
        }
        return fragment.trim();
    }

    /**
     * The beginning and the end of a typical tweet is full of jargonny junk. Try to
     * strip that out so only the real text remains.
     *
     * @param line
     * @return
     */
    public static String stripJunk(String line) {
        List<String> words = Arrays.asList(line.split("\\s+"));

        /**
         * Strip out retweet text. The may be more than one of these.
         */
        int retweetIndex = words.indexOf("RT");
        while(retweetIndex >= 0 && retweetIndex + 2 < words.size()) {
            words = words.subList(retweetIndex + 2, words.size());
            retweetIndex = words.indexOf("RT");
        }


        /**
         * Strip off leading and trailing boguswords (and even a final non-bogusword if it's
         * preceded by a bogusword
         */
        if(words.size() > 1 && isBogusWord(words.get(words.size() - 2))) {
            words = words.subList(0, words.size() - 2);
        }
        while(words.size() > 0 && isBogusWord(words.get(words.size() - 1))) {
            words = words.subList(0, words.size() - 1);
        }
        while(words.size() > 0 && isBogusWord(words.get(0))) {
            words = words.subList(1, words.size());
        }

        StringBuffer strippedLine = new StringBuffer("");
        for(String word : words) {
            strippedLine.append(word + " ");
        }
        return strippedLine.toString().trim();
    }

    public static boolean isBogusWord(String word) {
        return word.contains("#")
                || word.startsWith("http")
                || word.contains("@")
                || word.equalsIgnoreCase("lol");

    }


}
