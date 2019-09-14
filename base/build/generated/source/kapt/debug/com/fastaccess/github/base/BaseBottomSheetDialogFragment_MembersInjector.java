package com.fastaccess.github.base;

import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
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
public final class BaseBottomSheetDialogFragment_MembersInjector implements MembersInjector<BaseBottomSheetDialogFragment> {
  private final Provider<DispatchingAndroidInjector<Object>> childFragmentInjectorProvider;

  public BaseBottomSheetDialogFragment_MembersInjector(
      Provider<DispatchingAndroidInjector<Object>> childFragmentInjectorProvider) {
    this.childFragmentInjectorProvider = childFragmentInjectorProvider;
  }

  public static MembersInjector<BaseBottomSheetDialogFragment> create(
      Provider<DispatchingAndroidInjector<Object>> childFragmentInjectorProvider) {
    return new BaseBottomSheetDialogFragment_MembersInjector(childFragmentInjectorProvider);}

  @Override
  public void injectMembers(BaseBottomSheetDialogFragment instance) {
    injectChildFragmentInjector(instance, childFragmentInjectorProvider.get());
  }

  public static void injectChildFragmentInjector(BaseBottomSheetDialogFragment instance,
      DispatchingAndroidInjector<Object> childFragmentInjector) {
    instance.childFragmentInjector = childFragmentInjector;
  }
}
