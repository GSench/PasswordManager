package account_system;

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

import utils.AESEncryptor;

/**
 * Created by grish on 25.02.2017.
 */

public class AccountSystem {

    //TODO System version in XML

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

    public void decryptSystem(String key) throws SAXException, IOException, GeneralSecurityException, ParserConfigurationException {
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
        decrypted = decrypted.replaceAll("&", "&amp;"); //SAX Parser &amp bug fix
        InputSource source = new InputSource(new ByteArrayInputStream(decrypted.getBytes("UTF-8")));
        parser.parse(source);
        accounts = base.getAccounts();
    }

    public byte[] encryptSystem() throws UnsupportedEncodingException, GeneralSecurityException {
        String base = BaseParser.toXML(accounts);
        return AESEncryptor.encrypt(base.getBytes(), key, KEY_SIZE);
    }

    public Account getAccount(int i){
        return accounts!=null ? accounts.get(i) : null;
    }

    public int getAccountsCount(){
        return accounts!=null ? accounts.size() : -1;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void deleteAccount(Account account){
        accounts.remove(account);
    }

}
