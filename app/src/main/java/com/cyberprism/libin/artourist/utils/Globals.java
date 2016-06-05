package com.cyberprism.libin.artourist.utils;

import java.util.regex.Pattern;

/**
 * Created by Libin on 19-Mar-16.
 */
public class Globals {
    public static final String WIKI_KEY = "IIo2Il1Hk7NXw++pdOroAQvEnRlFcD/am/0k547XLutT54tTI+jVJYdaew9NiWUWv6vXR3Etpo5v4QxZSccewbRyAoB6hGs0fjJ/MGkYIhwi/qWhHNGx8KF9OmxS8nFeR5mJqo2HWTNSw2QhIeM1tc//GH11kOTpe3TGaCU9ZzFTYWx0ZWRfX6mwrpChT+2w3ddie2/0hHZtCCNvkeXo5pbIOj4NM1Kw4lPObV2gqat5V5w/TAYgB/6czw5UL6dHLBKFzoao75V1bW6RU19YXdBwRZmDRVZRy87tt4hXS4Xo52I0nWqC6Qu6fXXMX45LL5mhz7bd67Sh8s8tEseeNE+QSLp1xM1Kq071MYKNhsXiuxp189baiAtJNBhIcO5pn5BBwk4MvJMWJ7CPDEZCA7OWDOiEJi9QMg5OSniMyBTB8t2cPkUoXHvDfROp51Oapwr4Fn5kwp30fmm8VWDDCNKCRbiwahe+9mq7w+faKmibA1hEDaFcfzcXnaMTOxec3+RzVYiZmyaMEn1c2W5B4oZ4YutLgkg1VOwMstld2yic/vrCu7CBHQRtARWGSRGmmszVnYl031taUwV6+QQorwiYYhOC74s/LJ74MGxC3h00QsTneIW0wng+BVKlYD0MMacsuUlCK/KSEqIRKLSe0+4FfOU/2v0pxL0mhuJIHVA=";
    public static final String GOOGLE_PLACE_KEY = "AIzaSyBghzv9guI1QnlZtACIG6V5BC89B9Oxtig";

    public static final String PREFERENCE_NAME = "ARPREFERENCE";
    public static final String IP_KEY = "IPKEY";

    public static String SERVER_IP = "192.168.1.29";

    public static String NAMESPACE = "http://service.artourist.com/";

    public static String serviceUrl = "http://" + SERVER_IP + ":8080/ARTourist/ARService?xsd=1";

    public static void setServer(String ip) {
        serviceUrl = "http://" + ip + ":8080/ARTourist/ARService?xsd=1";
    }

    public static boolean ip_validate(String ip_address) {

        return Pattern
                .compile(
                        "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\"
                                + ".([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$")
                .matcher(ip_address).matches();

    }
}
