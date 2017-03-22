package account_system;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by grish on 26.02.2017.
 */

public class BaseParser extends DefaultHandler {

    public static final String ACCOUNTS = "accounts";
    public static final String VERSION = "version";
    public static final String CURRENT_VERSION = "1.0";

    private ArrayList<Account> accounts;

    public BaseParser(){
        accounts = new ArrayList<Account>();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals(Account.ACCOUNT))
            accounts.add(new Account(
                    Integer.parseInt(attributes.getValue(Account.ID)),
                    reformat(attributes.getValue(Account.NAME)),
                    reformat(attributes.getValue(Account.LOGIN)),
                    reformat(attributes.getValue(Account.PASSWORD))));
        if(qName.equals(ACCOUNTS)){
            String version = attributes.getValue(VERSION);
            if(!version.equals(CURRENT_VERSION)) onUpdateVersion(version);
        }
    }

    private void onUpdateVersion(String baseVersion){
        //New base version stub
    }

    public ArrayList<Account> getAccounts(){
        return accounts;
    }

    public static String toXML(ArrayList<Account> accounts){
        StringBuilder base = new StringBuilder();
        base.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        base.append("<"+ACCOUNTS+" ");
        base.append(VERSION+"=\"");
        base.append(CURRENT_VERSION+"\" >");
        for(Account account: accounts){
            base
                    .append("\n<"+Account.ACCOUNT+" ")
                    .append(Account.ID + "=\"").append(account.getId()).append("\" ")
                    .append(Account.NAME + "=\"").append(format(account.getName())).append("\" ")
                    .append(Account.LOGIN + "=\"").append(format(account.getLogin())).append("\" ")
                    .append(Account.PASSWORD + "=\"").append(format(account.getPassword())).append("\"/>");
        }
        base.append("\n</"+ACCOUNTS+">");
        return base.toString();
    }

    private static String format(String val) {
        return val
                .replaceAll("&", "&amp;")
                .replaceAll("\"", "&quot;")
                .replaceAll("\'", "&apos;")
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;");
    }

    private String reformat(String val){
        return val
                .replaceAll("&quot;", "\"")
                .replaceAll("&apos;", "\'")
                .replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&");
    }

    public static String SAXParserBugFix(String input){
        return input.replaceAll("&amp;", "&amp;amp;");
    }

}