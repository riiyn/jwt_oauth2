package com.riiyn.common;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author: riiyn
 * @date: 2021/3/28 12:33
 * @description:
 */
public class SaferObjectInputStream extends ObjectInputStream {
    private final List<String> allowedClasses;
    
    SaferObjectInputStream(InputStream in, List<String> allowedClasses) throws IOException {
        super(in);
        this.allowedClasses = Collections.unmodifiableList(allowedClasses);
    }
    
    protected Class<?> resolveClass(ObjectStreamClass classDesc) throws IOException, ClassNotFoundException {
        if (this.isProhibited(classDesc.getName())) {
            throw new NotSerializableException("Not allowed to deserialize " + classDesc.getName());
        } else {
            return super.resolveClass(classDesc);
        }
    }
    
    private boolean isProhibited(String className) {
        Iterator<String> var2 = this.allowedClasses.iterator();
        
        String allowedClass;
        do {
            if (!var2.hasNext()) {
                return true;
            }
            
            allowedClass = var2.next();
        } while(!className.startsWith(allowedClass));
        
        return false;
    }
    
}
