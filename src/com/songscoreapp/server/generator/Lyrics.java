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

        // intersection will be non-zero if this line contains bad words
        if(rhymes == null || Util.intersection(Arrays.asList(words), Arrays.asList(badWords)).size() > 0) {
            if (rhymes == null) {
                Util.log("rhymes == null !!!");
            } else {
                Util.log("contains bad word");
            }
            return getLastResortLyrics(seedLine, stanzaCount);
        }
        String significantWord = getSignificantWord(seedLine);
        Util.log("Let's write a song on the theme of " + significantWord);

        List<String> fullLines = new ArrayList<String>();
        for(String rhyme: rhymes) {
            List<String> fragments = TwitterUtil.getTwitterLines(significantWord, rhyme, true);
            fullLines.addAll(fragments);
        }
        fullLines.addAll(TwitterUtil.getTwitterLines(significantWord, "", true));

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
    private static final String[] badWords = {
        "4r5e",
		"5h1t",
		"5hit",
		"a55",
		"anal",
		"anus",
		"ar5e",
		"arrse",
		"arse",
		"ass",
		"ass-fucker",
		"asses",
		"assfucker",
		"assfukka",
		"asshole",
		"assholes",
		"asswhole",
		"a_s_s",
		"b!tch",
		"b00bs",
		"b17ch",
		"b1tch",
		"ballbag",
		"balls",
		"ballsack",
		"bastard",
		"beastial",
		"beastiality",
		"bellend",
		"bestial",
		"bestiality",
		"bi+ch",
		"biatch",
		"bitch",
		"bitcher",
		"bitchers",
		"bitches",
		"bitchin",
		"bitching",
		"bloody",
		"blow job",
		"blowjob",
		"blowjobs",
		"boiolas",
		"bollock",
		"bollok",
		"boner",
		"boob",
		"boobs",
		"booobs",
		"boooobs",
		"booooobs",
		"booooooobs",
		"breasts",
		"buceta",
		"bugger",
		"bum",
		"bunny fucker",
		"butt",
		"butthole",
		"buttmuch",
		"buttplug",
		"c0ck",
		"c0cksucker",
		"carpet muncher",
		"cawk",
		"chink",
		"cipa",
		"cl1t",
		"clit",
		"clitoris",
		"clits",
		"cnut",
		"cock",
		"cock-sucker",
		"cockface",
		"cockhead",
		"cockmunch",
		"cockmuncher",
		"cocks",
		"cocksuck",
		"cocksucked",
		"cocksucker",
		"cocksucking",
		"cocksucks",
		"cocksuka",
		"cocksukka",
		"cok",
		"cokmuncher",
		"coksucka",
		"coon",
		"cox",
		"crap",
		"cum",
		"cummer",
		"cumming",
		"cums",
		"cumshot",
		"cunilingus",
		"cunillingus",
		"cunnilingus",
		"cunt",
		"cuntlick",
		"cuntlicker",
		"cuntlicking",
		"cunts",
		"cyalis",
		"cyberfuc",
		"cyberfuck",
		"cyberfucked",
		"cyberfucker",
		"cyberfuckers",
		"cyberfucking",
		"d1ck",
		"damn",
		"dick",
		"dickhead",
		"dildo",
		"dildos",
		"dink",
		"dinks",
		"dirsa",
		"dlck",
		"dog-fucker",
		"doggin",
		"dogging",
		"donkeyribber",
		"doosh",
		"duche",
		"dyke",
		"ejaculate",
		"ejaculated",
		"ejaculates",
		"ejaculating",
		"ejaculatings",
		"ejaculation",
		"ejakulate",
		"f u c k",
		"f u c k e r",
		"f4nny",
		"fag",
		"fagging",
		"faggitt",
		"faggot",
		"faggs",
		"fagot",
		"fagots",
		"fags",
		"fanny",
		"fannyflaps",
		"fannyfucker",
		"fanyy",
		"fatass",
		"fcuk",
		"fcuker",
		"fcuking",
		"feck",
		"fecker",
		"felching",
		"fellate",
		"fellatio",
		"fingerfuck",
		"fingerfucked",
		"fingerfucker",
		"fingerfuckers",
		"fingerfucking",
		"fingerfucks",
		"fistfuck",
		"fistfucked",
		"fistfucker",
		"fistfuckers",
		"fistfucking",
		"fistfuckings",
		"fistfucks",
		"flange",
		"fook",
		"fooker",
		"fuck",
		"fucka",
		"fucked",
		"fucker",
		"fuckers",
		"fuckhead",
		"fuckheads",
		"fuckin",
		"fucking",
		"fuckings",
		"fuckingshitmotherfucker",
		"fuckme",
		"fucks",
		"fuckwhit",
		"fuckwit",
		"fudge packer",
		"fudgepacker",
		"fuk",
		"fuker",
		"fukker",
		"fukkin",
		"fuks",
		"fukwhit",
		"fukwit",
		"fux",
		"fux0r",
		"f_u_c_k",
		"gangbang",
		"gangbanged",
		"gangbangs",
		"gaylord",
		"gaysex",
		"goatse",
		"God",
		"god-dam",
		"god-damned",
		"goddamn",
		"goddamned",
		"hardcoresex",
		"hell",
		"heshe",
		"hoar",
		"hoare",
		"hoer",
		"homo",
		"hore",
		"horniest",
		"horny",
		"hotsex",
		"jack-off",
		"jackoff",
		"jap",
		"jerk-off",
		"jism",
		"jiz",
		"jizm",
		"jizz",
		"kawk",
		"knob",
		"knobead",
		"knobed",
		"knobend",
		"knobhead",
		"knobjocky",
		"knobjokey",
		"kock",
		"kondum",
		"kondums",
		"kum",
		"kummer",
		"kumming",
		"kums",
		"kunilingus",
		"l3i+ch",
		"l3itch",
		"labia",
		"lmfao",
		"lust",
		"lusting",
		"m0f0",
		"m0fo",
		"m45terbate",
		"ma5terb8",
		"ma5terbate",
		"masochist",
		"master-bate",
		"masterb8",
		"masterbat*",
		"masterbat3",
		"masterbate",
		"masterbation",
		"masterbations",
		"masturbate",
		"mo-fo",
		"mof0",
		"mofo",
		"mothafuck",
		"mothafucka",
		"mothafuckas",
		"mothafuckaz",
		"mothafucked",
		"mothafucker",
		"mothafuckers",
		"mothafuckin",
		"mothafucking",
		"mothafuckings",
		"mothafucks",
		"mother fucker",
		"motherfuck",
		"motherfucked",
		"motherfucker",
		"motherfuckers",
		"motherfuckin",
		"motherfucking",
		"motherfuckings",
		"motherfuckka",
		"motherfucks",
		"muff",
		"mutha",
		"muthafecker",
		"muthafuckker",
		"muther",
		"mutherfucker",
		"n1gga",
		"n1gger",
		"nazi",
		"nigg3r",
		"nigg4h",
		"nigga",
		"niggah",
		"niggas",
		"niggaz",
		"nigger",
		"niggers",
		"nob",
		"nob jokey",
		"nobhead",
		"nobjocky",
		"nobjokey",
		"numbnuts",
		"nutsack",
		"orgasim",
		"orgasims",
		"orgasm",
		"orgasms",
		"p0rn",
		"pawn",
		"pecker",
		"penis",
		"penisfucker",
		"phonesex",
		"phuck",
		"phuk",
		"phuked",
		"phuking",
		"phukked",
		"phukking",
		"phuks",
		"phuq",
		"pigfucker",
		"pimpis",
		"piss",
		"pissed",
		"pisser",
		"pissers",
		"pisses",
		"pissflaps",
		"pissin",
		"pissing",
		"pissoff",
		"poop",
		"porn",
		"porno",
		"pornography",
		"pornos",
		"prick",
		"pricks",
		"pron",
		"pube",
		"pusse",
		"pussi",
		"pussies",
		"pussy",
		"pussys",
		"rectum",
		"retard",
		"rimjaw",
		"rimming",
		"s hit",
		"s.o.b.",
		"sadist",
		"schlong",
		"screwing",
		"scroat",
		"scrote",
		"scrotum",
		"semen",
		"sex",
		"sh!+",
		"sh!t",
		"sh1t",
		"shag",
		"shagger",
		"shaggin",
		"shagging",
		"shemale",
		"shi+",
		"shit",
		"shitdick",
		"shite",
		"shited",
		"shitey",
		"shitfuck",
		"shitfull",
		"shithead",
		"shiting",
		"shitings",
		"shits",
		"shitted",
		"shitter",
		"shitters",
		"shitting",
		"shittings",
		"shitty",
		"skank",
		"slut",
		"sluts",
		"smegma",
		"smut",
		"snatch",
		"son-of-a-bitch",
		"spac",
		"spunk",
		"s_h_i_t",
		"t1tt1e5",
		"t1tties",
		"teets",
		"teez",
		"testical",
		"testicle",
		"tit",
		"titfuck",
		"tits",
		"titt",
		"tittie5",
		"tittiefucker",
		"titties",
		"tittyfuck",
		"tittywank",
		"titwank",
		"tosser",
		"turd",
		"tw4t",
		"twat",
		"twathead",
		"twatty",
		"twunt",
		"twunter",
		"v14gra",
		"v1gra",
		"vagina",
		"viagra",
		"vulva",
		"w00se",
		"wang",
		"wank",
		"wanker",
		"wanky",
		"whoar",
		"whore",
		"willies",
		"willy",
		"xrated",
		"xxx"
    };
}
