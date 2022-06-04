package com.mahama.parent.config;


import com.mahama.common.utils.AESUtil;
import com.mahama.common.utils.StringUtil;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionProperty {
    @Bean(name = "encryptablePropertyResolver")
    public EncryptablePropertyResolver encryptablePropertyResolver() {
        return new EncryptionPropertyResolver();
    }

    class EncryptionPropertyResolver implements EncryptablePropertyResolver {

        @Override
        public String resolvePropertyValue(String value) {
            if (StringUtil.isNullOrEmpty(value)) {
                return value;
            }
            // 值以AES@开头需要解密
            if (value.startsWith("AES@")) {
                return resolveDESValue(value.substring(4));
            }
            // 不需要解密的值直接返回
            return value;
        }

        private String resolveDESValue(String value) {
            // AES密文解密
            return AESUtil.decrypt(value);
        }
    }
}
