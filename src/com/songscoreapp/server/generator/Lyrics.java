package com.songscoreapp.server.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.googlecode.objectify.Objectify;
import com.songscoreapp.server.objectify.RhymingDictionary;
import com.songscoreapp.server.twitter.TwitterUtil;

public class Lyrics {

    RhymingDictionary dictionary;

    public Lyrics(RhymingDictionary dictionary) {
        this.dictionary = dictionary;
    }

    static private int LAST_NAME = 2000;
    static private int CAPITALIZED = 1000;
    /**
     * Finds the most significant word from a phrase.
     *
     * Rules:
     * look for words surrounded by asterisks
     * look for what looks like a last name
     * look at the longest capitalized word (unless its the very first word or like "I")
     * look for the longest word
     * @param phrase
     * @return
     */
    public static String getSignificantWord(String phrase) {
        String[] words = phrase.split(" ");
        String bestWord = "";
        int highestSignificance = 0;
        boolean previousWordCapitalized = false;
        boolean firstWord = true;
        for(String word : words) {
            String trimmedWord = trimPunctuation(word);
            if(word.startsWith("*") && word.endsWith("*")) {
                // This is the word we want. No need to dally.
                return trimmedWord;
            }

            int significance = trimmedWord.length();
            if(isCapitalized(trimmedWord)) {
                if(previousWordCapitalized) {
                   significance += LAST_NAME;
                } else if(!firstWord) {
                    significance += CAPITALIZED;
                }
                previousWordCapitalized = true;
            } else {
                previousWordCapitalized = false;
            }

            if(significance > highestSignificance) {
                bestWord = word;
                highestSignificance = significance;
            }

            firstWord = false;
        }
        return bestWord;
    }

    /**
     * Gets rid of all the punctuation
     *
     * @param phrases
     * @return
     */
    public static List<String> trimPhrasePunctuation(List<String> phrases) {
        List<String> trimmedPhrases = new ArrayList<String>();
        for(String phrase : phrases) {
            trimmedPhrases.add(trimPhrasePunctuation(phrase));
        }
        return trimmedPhrases;
    }

    public static String trimPhrasePunctuation(String phrase) {
        StringBuffer newPhrase = new StringBuffer("");
        String[] words = phrase.split("\\s+");
        for(String word : words) {
            String newWord = trimPunctuation(word);
            if(newWord != null && newWord.length() > 0) {
                newPhrase.append(" " + newWord);
            }
        }
        if(newPhrase.length() > 0) {
            // remove the leading space
            newPhrase.deleteCharAt(0);
        }
        return newPhrase.toString();
    }

    public static String trimPunctuation(String word) {
        while(word.length() > 0 && !Character.isLetter(word.charAt(0))) {
            word = word.substring(1);
        }
        while(word.length() > 0 && !Character.isLetter(word.charAt(word.length() - 1))) {
            word = word.substring(0, word.length() - 1);
        }
        return word;
    }

    public static boolean isCapitalized(String word) {
        if(word.equals("I") || word.equals("I'm") || word.equals("I'll") || word.equals("I'd")) {
            // these don't count
            return false;
        }
        return word != null && word.length() > 0 && Character.isUpperCase(word.charAt(0));
    }

    /**
     * If everything else goes wrong, make up a verse of mostly la la la.
     *
     * @param seedLine
     * @param verseCount
     * @return
     */
    private List<List<String>> getLastResortLyrics(String seedLine, int verseCount) {
        List<List<String>> lyrics = new ArrayList<List<String>>();

        List<String> verse = getLastResortVerse(seedLine);
        for(int i = 0; i < verseCount; i++) {
            lyrics.add(verse);
        }
        return lyrics;
    }

    private List<String> getLastResortVerse(String seedLine) {
        List<String> verse = Arrays.asList(new String[] {
                "la la la la la la la la",
                "la la la la la la la la",
                "la la la la la la la la",
                seedLine
        });
        return verse;
    }

    /**
     * The master function that puts it all together.
     * @param seedLine The one good line of input from the user
     * @param ofy
     * @param stanzaCount This is how many stanzas of lyrics have been requested
     * @return
     */
    public List<List<String>> getAllLyrics(String seedLine, Objectify ofy, int stanzaCount) {
        String[] words = seedLine.split("\\s+");
        String lastWord = words[words.length - 1];

        Util.log("Let's see what rhymes with " + lastWord);
        List<String> rhymes = dictionary.getRhymes(lastWord, 10);
        Util.log(rhymes != null ? rhymes.toString() : "Um, I need to look this word up.");

        if(rhymes == null) {
            Util.log("rhymes == null !!!");
            return getLastResortLyrics(seedLine, stanzaCount);
        }
        String significantWord = getSignificantWord(seedLine);
        Util.log("Let's write a song on the theme of " + significantWord);

        List<String> fullLines = new ArrayList<String>();
        for(String rhyme: rhymes) {
            List<String> fragments = TwitterUtil.getTwitterLines(significantWord, rhyme, true);
            fullLines.addAll(fragments);
        }

        List<List<String>> verses = assembleVerses(seedLine, fullLines, rhymes, true);
        if(verses.get(verses.size() - 1).get(0).equals("__Leads__")) {
            List<String> leads = verses.remove(verses.size() - 1);
            leads.remove(0); // this was the __leads__ label
            // TODO: recurse prettily?
            for(String secondarySeedLine : leads) {
                String[] secondaryWords = secondarySeedLine.split("\\s+");
                String secondaryLastWord = secondaryWords[secondaryWords.length - 1];

                Util.log("Let's see what rhymes with " + secondaryLastWord);
                List<String> secondaryRhymes = dictionary.getRhymes(secondaryLastWord, 10);
                if(secondaryRhymes == null) {
                    Util.log("Um, I need to look this word up.");
                    continue;
                }
                Util.log(secondaryRhymes.toString());

                List<String> secondaryFullLines = new ArrayList<String>();
                for(String rhyme: secondaryRhymes) {
                    List<String> fragments = TwitterUtil.getTwitterLines(significantWord, rhyme, true);
                    secondaryFullLines.addAll(fragments);
                }

                List<List<String>> secondaryVerses = assembleVerses(secondarySeedLine, secondaryFullLines, secondaryRhymes, false);
                if(secondaryVerses.size() > 0) {
                    // just add one verse for each secondary seed. These will become our refrains
                    verses.add(secondaryVerses.get(0));
                }
            }
        }

        while(verses.size() < stanzaCount) {
            verses.add(getLastResortVerse(seedLine));
        }
        return verses;
    }


    public static int IDEAL_LINE_LENGTH = 7;
    public static int SYLLABLE_DIFFERENCE_THRESHOLD = 2;
    /**
     * So we've got a lot of material from Twitter. Let's try to assemble it together into a song.
     *
     * @param seedLine
     * @param fullLines The material from twitter
     * @param rhymes
     * @param lookForLeads Do we want to use leftover lines from this verse to seed other verses?
     * @return
     */
    public List<List<String>> assembleVerses(String seedLine, List<String> fullLines, List<String> rhymes, boolean lookForLeads) {
        List<List<String>> verses = getVersesFromLines(seedLine, TwitterUtil.chopLines(fullLines, null), rhymes, lookForLeads);
        if(verses.size() == 0) {
            System.out.println("tough crowd. Let's try using rhymechopping");
            verses = getVersesFromLines(seedLine, TwitterUtil.chopLines(fullLines, rhymes), rhymes, lookForLeads);
        }
        return verses;
    }

    /**
     * This is where all the work gets done for assembleVerses
     *
     * @param seedLine
     * @param lines
     * @param rhymes
     * @param lookForLeads
     * @return
     */
    public List<List<String>> getVersesFromLines(String seedLine, List<String> lines, List<String> rhymes, boolean lookForLeads) {
        //Util.log("All lines:", lines);
        boolean seedLineIsDouble = false;
        int lineLength = SyllableUtil.getSyllableCountFromLine(seedLine);
        if(lineLength > 18) {
            Util.log("What, are you Bob Dylan? This line is way too long to make a song out of");
        } else if(lineLength > 10) {
            // we'll want to split the seed line across two lines of lyrics
            lineLength /= 2;
            seedLineIsDouble = true;
        }
        // we have to help some people out!
        int targetLength = (lineLength + IDEAL_LINE_LENGTH) / 2;

        List<List<String>> verses = new ArrayList<List<String>>();
        List<String> doubleLineRhymes = new ArrayList<String>();
        List<String> singleLineRhymes = new ArrayList<String>();
        List<String> doubleLineFiller = new ArrayList<String>();
        List<String> singleLineFiller = new ArrayList<String>();

        List<List<String>> groupedLines = groupLinesByRhyme(lines, rhymes);

        for(int i = 0; i < groupedLines.size(); i++) {
            List<String> group = groupedLines.get(i);
            for(String line : group) {
                int syllableCount = SyllableUtil.getSyllableCountFromLine(line);
                boolean isRhymingLine = i + 1 < groupedLines.size();
                if(isRhymingLine && isDoubleLine(syllableCount, targetLength, SYLLABLE_DIFFERENCE_THRESHOLD)) {
                    doubleLineRhymes.add(line);
                } else if(isRhymingLine && isSingleLine(syllableCount, targetLength, SYLLABLE_DIFFERENCE_THRESHOLD)) {
                    singleLineRhymes.add(line);
                } else if(!isRhymingLine && isSingleLine(syllableCount, targetLength, SYLLABLE_DIFFERENCE_THRESHOLD)) {
                    singleLineFiller.add(line);
                } else if(!isRhymingLine && isDoubleLine(syllableCount, targetLength, SYLLABLE_DIFFERENCE_THRESHOLD)) {
                    doubleLineFiller.add(line);
                }
            }
        }

        while(true) {
            List<String> verse = new ArrayList<String>();
            if(doubleLineRhymes.size() > 0) {
                verse.addAll(splitLine(doubleLineRhymes.remove(0)));
            } else if(singleLineRhymes.size() > 0 && singleLineFiller.size() > 0) {
                verse.add(singleLineFiller.remove(0));
                verse.add(singleLineRhymes.remove(0));
            } else {
                break;
            }

            if(seedLineIsDouble) {
                verse.addAll(splitLine(seedLine));
            } else if(singleLineFiller.size() > 0) {
                verse.add(singleLineFiller.remove(0));
                verse.add(seedLine);
            } else {
                break;
            }
            verses.add(trimPhrasePunctuation(verse));
        }
        if(lookForLeads) {
            // when treating a line as a possible lead we give ourselves the best
            // chance of it working out by stripping out the punctuation before we
            // evaluate it as a lead.
            List<String> leads = dictionary.getTopLeads(trimPhrasePunctuation(doubleLineFiller), 5);
            leads.add(0, "__Leads__");
            Util.log("Promising leads:", leads);
            verses.add(leads);
        }
        return verses;
    }

    public static boolean isSingleLine(int syllableCount, int targetLength, int epsilon) {
        return syllableCount >= targetLength - epsilon &&
                syllableCount <= targetLength + epsilon;
    }

    public static boolean isDoubleLine(int syllableCount, int targetLength, int epsilon) {
        return syllableCount >= 2 * (targetLength - epsilon) &&
                syllableCount <= 2 * (targetLength + epsilon);
    }

    /**
     * Take one long line and split it up into two better-size lines
     *
     * @param line
     * @return
     */
    public static List<String> splitLine(String line) {
        int targetSize = SyllableUtil.getSyllableCountFromLine(line) / 2;
        String firstLine = "";
        String secondLine = "";
        int syllableTally = 0;
        String[] words = line.split("\\s+");
        for(String word : words) {
            syllableTally += SyllableUtil.getSyllableCountFromWord(word);
            if(syllableTally <= targetSize) {
                firstLine = firstLine + " " + word;
            } else {
                secondLine = secondLine + " " + word;
            }
        }
        return Arrays.asList(new String[] {firstLine.trim(), secondLine.trim()});
    }

    /**
     * Does this line end in one of the known rhyming words?
     *
     * @param line
     * @param rhymes
     * @return
     */
    public static boolean isRhymingLine(String line, List<String> rhymes) {
        String[] words = line.split("\\s+");
        String lastWord = words[words.length - 1].toLowerCase();
        return rhymes.indexOf(lastWord) >= 0;
    }

    /**
     * Takes a list of lines and groups them together by which rhyme they end with. The
     * last group will have all the unrhymed lines.
     *
     * @param lines
     * @param rhymes
     * @return
     */
    public static List<List<String>> groupLinesByRhyme(List<String> lines, List<String> rhymes) {
        List<List<String>> groupedLines = new ArrayList<List<String>>();
        int rhymeCount = rhymes.size();
        for(int i = 0; i <= rhymeCount; i++) {
            groupedLines.add(new ArrayList<String>());
        }

        for(String line : lines) {
            String[] words = line.split("\\s+");
            String lastWord = words[words.length - 1].toLowerCase();
            int rhymeIndex = rhymes.indexOf(lastWord);
            if(rhymeIndex < 0) {
                // unrhymed lines will be grouped at the end
                groupedLines.get(rhymeCount).add(line);
            } else {
                groupedLines.get(rhymeIndex).add(line);
            }
        }

        // remove all empty groups, except the unrhymed
        for(int i = rhymeCount - 1; i >= 0; i--) {
            if(groupedLines.get(i).size() == 0) {
                groupedLines.remove(i);
            }
        }
        return groupedLines;
    }
}
