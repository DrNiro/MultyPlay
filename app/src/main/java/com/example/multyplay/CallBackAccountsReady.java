package com.example.multyplay;

import java.util.ArrayList;

public interface CallBackAccountsReady {
    void accountsReady(ArrayList<Account> accounts);
    void error();
}
