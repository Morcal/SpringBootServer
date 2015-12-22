package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.Protocol;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class V2 implements Protocol{
    public static final int Version = 0x02;
    static final int MAX_PACKET_LENGTH = 1024;
    static final int MIN_PACKET_LENGTH = 32;

    @Override
    public int getVersion() {
        return Version;
    }

    @Override
    public String getSupportedTypeName() {
        return "Huawei v2";
    }

}
