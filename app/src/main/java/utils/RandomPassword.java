package utils;

import org.apache.commons.lang3.RandomStringUtils;

import main.java.com.maximeroussy.invitrode.RandomWord;
import main.java.com.maximeroussy.invitrode.WordLengthException;

/**
 * Created by grish on 01.03.2017.
 */

public class RandomPassword {

    public static String getRandomLogin(){
        int length = (int) (5+Math.round(Math.random()*3));
        String login = "";
        try {
            login =  RandomWord.getNewWord(length);
        } catch (WordLengthException e) {
            e.printStackTrace();
        }
        login=login.substring(0, 1).toUpperCase()+login.substring(1).toLowerCase();
        return login;
    }

    private static String getRandomSymbolString(boolean withoutSym){
        String from = "0123456789!\"#$%&'()*+,-./:;<=>?@[\\]^_{|}~";
        return RandomStringUtils.random(4, 0, from.length(), false, withoutSym, from.toCharArray());
        /**
        double r = Math.random();
        if(r<1d/28*15) return (char) (0x21+Math.round(r*(0x2f-0x21)));
        else if(r>=1d/28*15&&r<1d/28*22) return (char) (0x3a+Math.round(r*(0x40-0x3a)));
        else return (char) (0x5b+Math.round(r*(0x60-0x5b)));*/
    }

    private static String getRandomWord(boolean withoutSym){
        String word = "", sym;
        try {
            word = RandomWord.getNewWord(8);
        } catch (WordLengthException e) {
            e.printStackTrace();
        }
        word=word.substring(0, 1).toUpperCase()+word.substring(1).toLowerCase();
        sym = getRandomSymbolString(withoutSym);
        return word+sym;
    }

    public static String getRandomPINCode(){
        return String.format("%04d", (int)Math.round(Math.random()*9999));
    }

    private static String getRandomString(boolean withoutSym){
        return RandomStringUtils.random(8, 0x21, 0x7f, false, withoutSym);
    }

    public static String getRandomPassword(boolean fullyRandom, boolean withoutSym){
        if(fullyRandom) return getRandomString(withoutSym);
        else return getRandomWord(withoutSym);
    }

}
