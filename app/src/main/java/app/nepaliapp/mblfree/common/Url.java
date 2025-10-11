package app.nepaliapp.mblfree.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Url {

    String gitUrl = "https://raw.githubusercontent.com/ai-developer63/just/gh-pages/";
    String officialUrl = "https://mobilerepairing.subhakhar.xyz/";
//    String officialUrl = "https://pmbl.subhakhar.xyz/";
    String app_checkup = "appcheck";
    String login = "api/auth/login";
    String signup = "api/auth/signup";
    String otpRequest = "api/auth/otpRequest";
    String checkOtp = "api/auth/verifyotp";
    String setPassword = "api/auth/setpassword";
    String homeImage = "api/banner";
    String CategoriesSystem = "api/categories";
    String schematicsCompanies = "api/schematric/companies";
    String schematicsLinks = "api/schematric/of/";
    String price = "NewMBLPrice";
    String homeVideos = "api/getAllHomeVideos";
     //workshop
    String requestWorkShopCompany = "api/topics/allcompanies";
    String requestWorkShopModel = "api/topics/subcompany/";
    String requestWorkShopTopic = "api/getAlltopics";
    String requestWorkShopSteps = "api/getSteps";

    public String getRequestWorkShopSteps() {
        return officialUrl+requestWorkShopSteps;
    }

    public String getRequestWorkShopTopic() {
       return officialUrl+requestWorkShopTopic;
    }


    public String getRequestWorkShopModel(String companyName) {
        return officialUrl+requestWorkShopModel+companyName;
    }

    public String getRequestWorkShopCompany() {
        return officialUrl+requestWorkShopCompany;
    }

    public String getHomeVideos() {
        return officialUrl + homeVideos;
    }


    public String getPrice() {
        return gitUrl+price;
    }

    public String getSchematicsLinks(String companyName) {
        return officialUrl+schematicsLinks+companyName;
    }

    public String getSchematicsCompanies() {
        return officialUrl+ schematicsCompanies;
    }

    public String getCategoriesSystem() {
        return officialUrl + CategoriesSystem;
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

    public String getHomeImage() {
        return officialUrl + homeImage;
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
