package cn.com.xinli.portal.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Portal context.
 *
 * <p>Project: xpws
 *
 * @author zhoupeng 2016/2/21.
 */
public class Context {
    private String ip;
    private String mac;
    private String nasIp;
    private String extendedInformation;
    private String session;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getNasIp() {
        return nasIp;
    }

    public void setNasIp(String nasIp) {
        this.nasIp = nasIp;
    }

    public String getExtendedInformation() {
        return extendedInformation;
    }

    public void setExtendedInformation(String extendedInformation) {
        this.extendedInformation = extendedInformation;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public static Context parse(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("context value can not be blank.");
        }

        final Context context = new Context();

        Arrays.stream(value.split("&"))
                .forEach(pair -> {
                    String[] values = pair.split("=");
                    if (values.length > 1) {
                        switch (values[0]) {
                            case "session":
                                context.session = values[1];
                                break;
                            case "ip":
                                context.ip = values[1];
                                break;
                            case "mac":
                                context.mac = values[1];
                                break;
                            case "nas_ip":
                                context.nasIp = values[1];
                                break;
                            case "extended_information":
                                context.extendedInformation = values[1];
                                break;
                            default:
                                break;
                        }
                    }
                });

        return context;
    }

    public boolean isValid() {
        return !StringUtils.isEmpty(ip);
    }

    public String encode() {
        StringJoiner joiner = new StringJoiner("&");
        joiner.add("session=" + session)
                .add("ip=" + ip)
                .add("mac=" + mac)
                .add("nas_ip=" + nasIp)
                .add("extended_information=" + extendedInformation);
        return joiner.toString();
    }

    @Override
    public String toString() {
        return "Context{" +
                "session='" + session + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", nasIp='" + nasIp + '\'' +
                ", extendedInformation='" + extendedInformation + '\'' +
                '}';
    }
}
