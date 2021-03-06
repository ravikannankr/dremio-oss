/*
 * Copyright (C) 2017 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dremio.exec.server.options;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import com.dremio.exec.server.options.OptionValue.OptionType;
import com.dremio.exec.server.options.TypeValidators.BooleanValidator;
import com.dremio.exec.server.options.TypeValidators.DoubleValidator;
import com.dremio.exec.server.options.TypeValidators.LongValidator;
import com.dremio.exec.server.options.TypeValidators.StringValidator;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A read-only OptionManager that caches the response from the delegate OptionMangager.
 */
public class CachingOptionManager implements OptionManager {

  private final OptionManager delegate;
  private final LoadingCache<String, OptionValue> options = CacheBuilder.newBuilder()
      .maximumSize(10000)
      .build(
          new CacheLoader<String, OptionValue>() {
            public OptionValue load(String key) {
              return delegate.getOption(key);
            }
          });

  public CachingOptionManager(OptionManager delegate) {
    super();
    this.delegate = delegate;
  }

  @Override
  public Iterator<OptionValue> iterator() {
    throw new UnsupportedOperationException("NYI");
  }

  @Override
  public void setOption(OptionValue value) {
    throw new UnsupportedOperationException("NYI");
  }

  @Override
  public void deleteOption(String name, OptionType type) {
    throw new UnsupportedOperationException("NYI");
  }

  @Override
  public void deleteAllOptions(OptionType type) {
    throw new UnsupportedOperationException("NYI");
  }

  @Override
  public OptionValue getOption(String name) {
    try {
      return options.get(name);
    } catch (ExecutionException e) {
      throw Throwables.propagate(e.getCause());
    }
  }

  @Override
  public boolean getOption(BooleanValidator validator) {
    return getOption(validator.getOptionName()).bool_val;
  }

  @Override
  public double getOption(DoubleValidator validator) {
    return getOption(validator.getOptionName()).float_val;
  }

  @Override
  public long getOption(LongValidator validator) {
    return getOption(validator.getOptionName()).num_val;
  }

  @Override
  public String getOption(StringValidator validator) {
    return getOption(validator.getOptionName()).string_val;
  }

  @Override
  public OptionList getOptionList() {
    throw new UnsupportedOperationException("NYI");
  }

  @Override
  public OptionValidator getValidator(String name) {
    throw new UnsupportedOperationException("NYI");
  }

}
