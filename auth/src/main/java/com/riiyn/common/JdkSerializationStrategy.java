package com.riiyn.common;

import org.springframework.core.serializer.support.SerializationFailedException;
import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/28 12:31
 * @description: 自定义jdk序列化策略（其实就是复制源码，做点小改动...）
 */
public class JdkSerializationStrategy extends StandardStringSerializationStrategy {
    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final List<String> ALLOWED_CLASSES;
    
    public JdkSerializationStrategy() {
    }
    
    protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        if (bytes != null && bytes.length != 0) {
            try {
                SaferObjectInputStream saferObjectInputStream = new SaferObjectInputStream(new ByteArrayInputStream(bytes), ALLOWED_CLASSES);
                return (T) saferObjectInputStream.readObject();
            } catch (Exception var4) {
                throw new SerializationFailedException("Failed to deserialize payload", var4);
            }
        } else {
            return null;
        }
    }
    
    protected byte[] serializeInternal(Object object) {
        if (object == null) {
            return EMPTY_ARRAY;
        } else if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " requires a Serializable payload but received an object of type [" + object.getClass().getName() + "]");
        } else {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(object);
                objectOutputStream.flush();
                return byteArrayOutputStream.toByteArray();
            } catch (Exception var4) {
                throw new SerializationFailedException("Failed to serialize object", var4);
            }
        }
    }
    
    static {
        List<String> classes = new ArrayList<>();
        classes.add("java.lang.");
        classes.add("java.util.");
        classes.add("org.springframework.security.");
        classes.add("com.riiyn.entity."); // 解决redis无法反序列化payload
        ALLOWED_CLASSES = Collections.unmodifiableList(classes);
    }
}
