package stronglogic.ruviuz.content;


import android.app.LauncherActivity;

import java.io.Serializable;

/**
 * Created by logicp on 12/2/2016.
 * Yeah
 */
public class Customer extends LauncherActivity.ListItem implements Serializable  {

    private String email;
    private String phone;
    private String firstname;
    private String lastname;
    private String prefix;
    private boolean married;


    public void setPrefix(String prefix) { this.prefix = prefix;}

    public String getPrefix() { return this.prefix; }


    public void setEmail (String email)    {
        this.email = email;
    }

    public String getEmail()  {
        return this.email;
    }


    public void setPhone(String phone)  {
        this.phone = phone;
    }

    public String getPhone()  {
        return this.phone;
    }


    public void setFirstname(String firstname)  {
        this.firstname = firstname;
    }

    public String getFirstname()  {
        return this.firstname;
    }


    public void setLastname (String lastname)  {
        this.lastname = lastname;
    }

    public String getLastname()  {
        return this.lastname;
    }


    public void setMarried(boolean married) {
        this.married = married;
    }

    public boolean getMarried() {
        return this.married;
    }
}