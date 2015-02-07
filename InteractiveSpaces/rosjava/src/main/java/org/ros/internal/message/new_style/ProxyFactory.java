/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.internal.message.new_style;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
class ProxyFactory {

  /**
   * @param interfaceClass
   *          the interface class to provide
   * @param implementation
   *          the instance to proxy
   * @return a new proxy for {@code implementation} that implements
   *         {@code interfaceClass}
   */
  @SuppressWarnings("unchecked")
  public static <T, S> T newProxy(Class<T> interfaceClass, final S implementation) {
    return (T) Proxy.newProxyInstance(implementation.getClass().getClassLoader(), new Class[] {
        interfaceClass, GetInstance.class }, new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(implementation, args);
      }
    });
  }
}
