package com.example.multyplay;

public class Email {

    private String name;
    private String company;
    private String ending;

    public Email() {
    }

    public Email(String name, String company, String ending) {
        setName(name);
        setCompany(company);
        setEnding(ending);
    }

    public Email(String address) {
        setAddress(address);
    }

    private void setAddress(String address) {
        boolean seenStrud = false;
        boolean seenDot = false;
        String name = "";
        String domain = "";
        String ending = "";
        for(int i = 0; i < address.length(); i++) {
            if(!seenStrud) {
                if(address.charAt(i) == '@')
                    seenStrud = true;
                else
                    name += address.charAt(i);
            }
            else if(!seenDot) {
                if(address.charAt(i) == '.')
                    seenDot = true;
                else
                    domain += address.charAt(i);
            } else {
                ending += address.charAt(i);
            }
        }
        setName(name);
        setCompany(domain);
        setEnding(ending);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEnding() {
        return ending;
    }

    public void setEnding(String ending) {
        this.ending = ending;
    }

    public String toString() {
        return getName() + "@" + getCompany() + "." + getEnding();
    }

    public boolean equals(Email email) {
        return this.toString().equals(email.toString());
    }
}
