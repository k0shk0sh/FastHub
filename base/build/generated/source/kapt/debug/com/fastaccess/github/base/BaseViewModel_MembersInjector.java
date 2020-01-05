package com.fastaccess.github.base;

import com.google.gson.Gson;
import dagger.MembersInjector;
import dagger.internal.InjectedFieldSignature;
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
public final class BaseViewModel_MembersInjector implements MembersInjector<BaseViewModel> {
  private final Provider<Gson> gsonProvider;

  public BaseViewModel_MembersInjector(Provider<Gson> gsonProvider) {
    this.gsonProvider = gsonProvider;
  }

  public static MembersInjector<BaseViewModel> create(Provider<Gson> gsonProvider) {
    return new BaseViewModel_MembersInjector(gsonProvider);}

  @Override
  public void injectMembers(BaseViewModel instance) {
    injectGson(instance, gsonProvider.get());
  }

  @InjectedFieldSignature("com.fastaccess.github.base.BaseViewModel.gson")
  public static void injectGson(BaseViewModel instance, Gson gson) {
    instance.gson = gson;
  }
}
