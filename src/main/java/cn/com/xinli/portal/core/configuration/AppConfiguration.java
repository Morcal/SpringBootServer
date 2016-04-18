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

    /** iOS app version. */
    @JsonProperty("ios_app_version")
    private String iOSAppVersion;

    /** Android app. */
    @JsonProperty("android_app")
    private String androidAppFileName;

    /** Android app version. */
    @JsonProperty("android_app_version")
    private String androidAppVersion;

    /** linux app. */
    @JsonProperty("linux_app")
    private String linuxAppFileName;

    /** linux app version. */
    @JsonProperty("linux_app_version")
    private String linuxAppVersion;

    /** mac os x app. */
    @JsonProperty("mac_app")
    private String macAppFileName;

    /** mac os x app version. */
    @JsonProperty("mac_app_version")
    private String macAppVersion;

    /** Microsoft windows app. */
    @JsonProperty("windows_app")
    private String windowsAppFileName;

    /** Microsoft windows app. */
    @JsonProperty("windows_app_version")
    private String windowsAppVersion;

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

    public String getiOSAppVersion() {
        return iOSAppVersion;
    }

    public void setiOSAppVersion(String iOSAppVersion) {
        this.iOSAppVersion = iOSAppVersion;
    }

    public String getAndroidAppVersion() {
        return androidAppVersion;
    }

    public void setAndroidAppVersion(String androidAppVersion) {
        this.androidAppVersion = androidAppVersion;
    }

    public String getLinuxAppVersion() {
        return linuxAppVersion;
    }

    public void setLinuxAppVersion(String linuxAppVersion) {
        this.linuxAppVersion = linuxAppVersion;
    }

    public String getMacAppVersion() {
        return macAppVersion;
    }

    public void setMacAppVersion(String macAppVersion) {
        this.macAppVersion = macAppVersion;
    }

    public String getWindowsAppVersion() {
        return windowsAppVersion;
    }

    public void setWindowsAppVersion(String windowsAppVersion) {
        this.windowsAppVersion = windowsAppVersion;
    }
}
