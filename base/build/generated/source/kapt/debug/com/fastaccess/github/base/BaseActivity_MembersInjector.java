package com.fastaccess.github.base;

import com.fastaccess.data.storage.FastHubSharedPreference;
import com.fastaccess.domain.di.AuthenticationInterceptor;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.DaggerAppCompatActivity_MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class BaseActivity_MembersInjector implements MembersInjector<BaseActivity> {
  private final Provider<DispatchingAndroidInjector<Object>> androidInjectorProvider;

  private final Provider<FastHubSharedPreference> preferenceProvider;

  private final Provider<AuthenticationInterceptor> interceptorProvider;

  public BaseActivity_MembersInjector(
      Provider<DispatchingAndroidInjector<Object>> androidInjectorProvider,
      Provider<FastHubSharedPreference> preferenceProvider,
      Provider<AuthenticationInterceptor> interceptorProvider) {
    this.androidInjectorProvider = androidInjectorProvider;
    this.preferenceProvider = preferenceProvider;
    this.interceptorProvider = interceptorProvider;
  }

  public static MembersInjector<BaseActivity> create(
      Provider<DispatchingAndroidInjector<Object>> androidInjectorProvider,
      Provider<FastHubSharedPreference> preferenceProvider,
      Provider<AuthenticationInterceptor> interceptorProvider) {
    return new BaseActivity_MembersInjector(androidInjectorProvider, preferenceProvider, interceptorProvider);}

  @Override
  public void injectMembers(BaseActivity instance) {
    DaggerAppCompatActivity_MembersInjector.injectAndroidInjector(instance, androidInjectorProvider.get());
    injectPreference(instance, preferenceProvider.get());
    injectInterceptor(instance, interceptorProvider.get());
  }

  public static void injectPreference(BaseActivity instance, FastHubSharedPreference preference) {
    instance.preference = preference;
  }

  public static void injectInterceptor(BaseActivity instance,
      AuthenticationInterceptor interceptor) {
    instance.interceptor = interceptor;
  }
}
