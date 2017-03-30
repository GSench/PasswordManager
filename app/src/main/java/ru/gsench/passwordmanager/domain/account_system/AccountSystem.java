package ru.gsench.passwordmanager.domain.account_system;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ru.gsench.passwordmanager.domain.utils.AESEncryptor;

/**
 * Created by grish on 25.02.2017.
 */

public class AccountSystem {

    public static final int KEY_SIZE = 32;

    private byte[] system;
    private ArrayList<Account> accounts;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public AccountSystem(byte[] system){
        this.system=system;
        accounts=null;
    }

    public void initSystem(String key) throws SAXException, IOException, GeneralSecurityException, ParserConfigurationException {
        this.key=key;
        if(system==null||system.length==0){
        	accounts = new ArrayList<Account>();
        	return;
        }
        String decrypted = new String(AESEncryptor.decrypt(system, key, KEY_SIZE), "UTF-8");
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser newSAXParser = saxParserFactory.newSAXParser();
        XMLReader parser = newSAXParser.getXMLReader();
        BaseParser base = new BaseParser();
        parser.setContentHandler(base);
        decrypted = BaseParser.SAXParserBugFix(decrypted);
        InputSource source = new InputSource(new ByteArrayInputStream(decrypted.getBytes("UTF-8")));
        parser.parse(source);
        accounts = base.getAccounts();
    }

    public byte[] encryptSystem() throws UnsupportedEncodingException, GeneralSecurityException {
        String base = BaseParser.toXML(accounts);
        return AESEncryptor.encrypt(base.getBytes(), key, KEY_SIZE);
    }

    public ArrayList<Account> getAccounts(){
        return accounts;
    }

    public int getAccountsCount(){
        return accounts!=null ? accounts.size() : -1;
    }

    public void editAccount(Account account){
        for(Account account1: accounts){
            if(account1.getId()==account.getId()){
                account1.setName(account.getName());
                account1.setLogin(account.getLogin());
                account1.setPassword(account.getPassword());
                return;
            }
        }
        accounts.add(account);
    }

    public void deleteAccount(Account account){
        accounts.remove(account);
    }

}
