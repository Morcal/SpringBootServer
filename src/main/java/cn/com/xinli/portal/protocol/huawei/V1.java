package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.Protocol;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class V1 implements Protocol {
    public static final int Version = 0x01;
    static final int MAX_PACKET_LENGTH = 1024;

    @Override
    public int getVersion() {
        return Version;
    }

    @Override
    public String getSupportedTypeName() {
        return "Huawei v1";
    }
}
