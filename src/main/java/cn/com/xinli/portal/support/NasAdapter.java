package cn.com.xinli.portal.support;

import cn.com.xinli.portal.repository.NasEntity;
import cn.com.xinli.portal.protocol.AuthType;
import cn.com.xinli.portal.protocol.CredentialsTranslation;
import cn.com.xinli.portal.protocol.Nas;
import cn.com.xinli.portal.protocol.NasType;

/**
 * Device (NAS/BRAS) supports Portal protocol configuration.
 * <p>
 * Project: xpws
 *
 * @author zhoupeng 2015/12/19.
 */
public class NasAdapter implements Nas {
    /**
     * Default nas type name.
     */
    NasType DEFAULT_NAS_TYPE = NasType.HuaweiV2;

    /**
     * Default nas listen port.
     */
    int DEFAULT_NAS_LISTEN_PORT = 2000;

    /**
     * Default NAS authentication type.
     */
    AuthType DEFAULT_NAS_AUTHENTICATION_TYPE = AuthType.CHAP;

    final NasEntity entity;

    public NasAdapter(NasEntity entity) {
        this.entity = entity;
//        this.type = StringUtils.isEmpty(type) ? DEFAULT_NAS_TYPE : NasType.valueOf(type);
//        this.listenPort = listenPort <= 0 ? DEFAULT_NAS_LISTEN_PORT : listenPort;
//        this.authType = StringUtils.isEmpty(authType) ? DEFAULT_NAS_AUTHENTICATION_TYPE : AuthType.of(authType);
    }

    @Override
    public long getId() {
        return entity.getId();
    }

    @Override
    public String getNasId() {
        return entity.getNasId();
    }

    @Override
    public String getIpv4Address() {
        return entity.getIpv4Address();
    }

    @Override
    public String getIpv6Address() {
        return entity.getIpv6Address();
    }

    @Override
    public NasType getType() {
        return entity.getType() == null ? DEFAULT_NAS_TYPE : entity.getType();
    }

    @Override
    public int getListenPort() {
        return entity.getListenPort() == 0 ? DEFAULT_NAS_LISTEN_PORT : entity.getListenPort();
    }

    @Override
    public AuthType getAuthType() {
        return entity.getAuthType() == null ? DEFAULT_NAS_AUTHENTICATION_TYPE : entity.getAuthType();
    }

    @Override
    public String getSupportedDomains() {
        return entity.getSupportedDomains();
    }

    @Override
    public String getIpv4end() {
        return entity.getIpv4end();
    }

    @Override
    public String getIpv4start() {
        return entity.getIpv4start();
    }

    @Override
    public String getSharedSecret() {
        return entity.getSharedSecret();
    }

    @Override
    public CredentialsTranslation getCreCredentialsTranslation() {
        return null;
    }

//
//    /**
//     * Create an unmodifiable NAS from configuration.
//     *
//     * @param configuration nas configuration.
//     * @return NAS.
//     */
//    public static Nas build(NasConfiguration configuration) {
//        if (StringUtils.isEmpty(configuration.getIpv4Address())
//                && StringUtils.isEmpty(configuration.getIpv6Address())) {
//            throw new RuntimeException("NAS must has ipv4 or ipv6 address at lest.");
//        }
//
//        String ipv4start = configuration.getIpv4start(),
//                ipv4end = configuration.getIpv4end();
//
//        int start, end;
//        try {
//            start = AddressUtil.convertIpv4Address(ipv4start);
//            end = AddressUtil.convertIpv4Address(ipv4end);
//        } catch (IllegalArgumentException iae) {
//            start = 0;
//            end = 0;
//        }
//
//        return new NasAdapter(configuration.getId(),
//                configuration.getNasId(),
//                configuration.getIpv4Address(),
//                configuration.getIpv6Address(),
//                configuration.getType(),
//                configuration.getListenPort(),
//                configuration.getAuthType().toUpperCase(),
//                configuration.getSharedSecret(),
//                start,
//                end,
//                CredentialsTranslations.getPrefixPostfixModifier(
//                        configuration.getUsernamePrefix(),
//                        configuration.getUsernamePostfix(),
//                        configuration.getPasswordPrefix(),
//                        configuration.getPasswordPostfix()));
//    }

}
