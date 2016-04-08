package cn.com.xinli.portal.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Client App configuration.
 * @author zhoupeng, created on 2016/4/8.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppConfiguration {
    /** iOS app. */
    @JsonProperty("ios_app")
    private String iOSAppFileName;

    /** Android app. */
    @JsonProperty("android_app")
    private String androidAppFileName;

    /** linux app. */
    @JsonProperty("linux_app")
    private String linuxAppFileName;

    /** mac os x app. */
    @JsonProperty("mac_app")
    private String macAppFileName;

    /** Microsoft windows app. */
    @JsonProperty("windows_app")
    private String windowsAppFileName;

    public String getiOSAppFileName() {
        return iOSAppFileName;
    }

    public void setiOSAppFileName(String iOSAppFileName) {
        this.iOSAppFileName = iOSAppFileName;
    }

    public String getAndroidAppFileName() {
        return androidAppFileName;
    }

    public void setAndroidAppFileName(String androidAppFileName) {
        this.androidAppFileName = androidAppFileName;
    }

    public String getLinuxAppFileName() {
        return linuxAppFileName;
    }

    public void setLinuxAppFileName(String linuxAppFileName) {
        this.linuxAppFileName = linuxAppFileName;
    }

    public String getMacAppFileName() {
        return macAppFileName;
    }

    public void setMacAppFileName(String macAppFileName) {
        this.macAppFileName = macAppFileName;
    }

    public String getWindowsAppFileName() {
        return windowsAppFileName;
    }

    public void setWindowsAppFileName(String windowsAppFileName) {
        this.windowsAppFileName = windowsAppFileName;
    }
}
