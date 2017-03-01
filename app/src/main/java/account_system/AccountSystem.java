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

    private byte[] system;
    private ArrayList<Account> accounts;
    private String key;

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
        String decrypted = new String(AESEncryptor.decrypt(system, key, 16), "UTF-8");
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser newSAXParser = saxParserFactory.newSAXParser();
        XMLReader parser = newSAXParser.getXMLReader();
        BaseParser base = new BaseParser();
        parser.setContentHandler(base);
        InputSource source = new InputSource(new ByteArrayInputStream(decrypted.getBytes("UTF-8")));
        parser.parse(source);
        accounts = base.getAccounts();
    }

    public byte[] encryptSystem() throws UnsupportedEncodingException, GeneralSecurityException {
        String base = BaseParser.toXML(accounts);
        return AESEncryptor.encrypt(base.getBytes(), key, 16);
    }

    public Account getAccount(int i){
        return accounts!=null ? accounts.get(i) : null;
    }

    public int getAccountsCount(){
        return accounts!=null ? accounts.size() : -1;
    }

    public ArrayList<Account> getAccounts(){
        return accounts;
    }

    public void addAccount(Account account){
        accounts.add(account);
    }

    public void deleteAccount(int id){
        for(Account account: accounts)
            if(account.getId()==id){
                accounts.remove(account);
                break;
            }
    }

}
