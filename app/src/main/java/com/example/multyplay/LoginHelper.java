package com.example.multyplay;

public class LoginHelper {

    private Account currentAccount;
    private boolean isLoggedIn;
    private boolean isFinishedInitAccount;

    public LoginHelper() {
    }

    public LoginHelper(Account currentAccount, boolean isLoggedIn, boolean isFinishedInitAccount) {
        setCurrentAccount(currentAccount);
        setLoggedIn(isLoggedIn);
        setFinishedInitAccount(isFinishedInitAccount);
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public void setCurrentAccount(Account currentAccount) {
        this.currentAccount = currentAccount;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isFinishedInitAccount() {
        return isFinishedInitAccount;
    }

    public void setFinishedInitAccount(boolean finishedInitAccount) {
        isFinishedInitAccount = finishedInitAccount;
    }
}
