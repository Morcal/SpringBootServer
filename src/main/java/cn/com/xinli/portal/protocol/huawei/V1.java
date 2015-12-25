package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Protocol;

/**
 * Project: xpws
 *
 * @author zhoupeng 2015/12/22.
 */
public class V1 implements Protocol {
    /** Huawei protocol v1 version. */
    public static final int Version = 0x01;

    @Override
    public int getVersion() {
        return Version;
    }

    @Override
    public String[] getSupportedNasTypeName() {
        return new String[] { "Huawei v1", "HuaweiV1", "Huawei-v1" };
    }

    @Override
    public CodecFactory getCodecFactory() {
        return new HuaweiCodecFactory();
    }
}
