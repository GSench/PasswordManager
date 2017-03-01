package account_system;

/**
 * Created by grish on 26.02.2017.
 */

public class Account{

    public static final String ACCOUNT = "account",
            NAME = "name",
            LOGIN = "login",
            PASSWORD = "password",
            ID = "id";

    public String getName() {
        return name;
    }
    public String getLogin() {
        return login;
    }
    public String getPassword() {
        return password;
    }
    public int getId() {
        return id;
    }
    private String name;
    private String login;

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    private String password;
    private int id;

    public Account(int id, String name, String login, String password){
        this.id=id;
        this.name=name;
        this.login=login;
        this.password=password;
    }
}