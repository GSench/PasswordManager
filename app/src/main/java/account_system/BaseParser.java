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

    private ArrayList<Account> accounts;

    public BaseParser(){
        accounts = new ArrayList<Account>();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if(qName.equals(Account.ACCOUNT))
            accounts.add(new Account(
                    Integer.parseInt(attributes.getValue(Account.ID)),
                    attributes.getValue(Account.NAME),
                    attributes.getValue(Account.LOGIN),
                    attributes.getValue(Account.PASSWORD)));
    }

    public ArrayList<Account> getAccounts(){
        return accounts;
    }

    public static String toXML(ArrayList<Account> accounts){
        StringBuilder base = new StringBuilder();
        base.append("<"+ACCOUNTS+">");
        for(Account account: accounts){
            base
                    .append("\n<"+Account.ACCOUNT+" ")
                    .append(Account.ID + "=\"").append(account.getId()).append("\" ")
                    .append(Account.NAME + "=\"").append(account.getName()).append("\" ")
                    .append(Account.LOGIN + "=\"").append(account.getLogin()).append("\" ")
                    .append(Account.PASSWORD + "=\"").append(account.getPassword()).append("\"/>");
        }
        base.append("\n</"+ACCOUNTS+">");
        return base.toString();
    }

}