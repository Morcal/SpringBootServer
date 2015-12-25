package cn.com.xinli.portal.protocol.huawei;

import cn.com.xinli.portal.protocol.CodecFactory;
import cn.com.xinli.portal.protocol.Protocol;

/**
 * Project: portal
 *
 * @author zhoupeng 2015/12/2.
 */
public class V2 implements Protocol{
    /** Huawei protocol v2 version. */
    public static final int Version = 0x02;

    @Override
    public int getVersion() {
        return Version;
    }

    @Override
    public String[] getSupportedNasTypeName() {
        return new String[] { "Huawei v2", "HuaweiV2", "Huawei-v2", "mock-huawei-nas" };
    }

    @Override
    public CodecFactory getCodecFactory() {
        return new HuaweiCodecFactory();
    }

}
