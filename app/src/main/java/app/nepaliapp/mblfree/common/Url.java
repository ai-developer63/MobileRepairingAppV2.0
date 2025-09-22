package app.nepaliapp.mblfree.common;

public class Url {

    String gitUrl = "https://raw.githubusercontent.com/ai-developer63/just/gh-pages/";
    String officialUrl = "https://mobilerepairing.subhakhar.xyz/";
    String app_checkup = "appcheck";
    String login = "api/auth/login";
    String signup = "api/auth/signup";
    String otpRequest = "api/auth/otpRequest";
    String checkOtp = "api/auth/verifyotp";
    String setPassword = "api/auth/setpassword";

    //Some period of time
    String homeImage ="MblHomeimage";
    String CategoriesSystem = "MblCategoryHome";
    public String getHomeImage() {
        return gitUrl+homeImage;
    }
    public String getCategoriesSystem() {
        return gitUrl+CategoriesSystem;
    }

    public String getSetPassword() {
        return officialUrl + setPassword;
    }

    public String getApp_checkup() {
        return gitUrl + app_checkup;
    }

    public String getLogin() {
        return officialUrl + login;
    }


    public String getSignup() {
        return officialUrl + signup;
    }

    public String getOtpRequest() {
        return officialUrl + otpRequest;
    }

    public String getCheckOtp() {
        return officialUrl + checkOtp;
    }
}
