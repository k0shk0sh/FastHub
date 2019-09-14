package com.fastaccess.fasthub.reactions;

import android.app.Application;
import androidx.work.WorkerParameters;
import com.apollographql.apollo.ApolloClient;
import dagger.internal.Factory;
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
public final class ReactionWorker_Factory implements Factory<ReactionWorker> {
  private final Provider<Application> contextProvider;

  private final Provider<WorkerParameters> workerParamsProvider;

  private final Provider<ApolloClient> apolloClientProvider;

  public ReactionWorker_Factory(Provider<Application> contextProvider,
      Provider<WorkerParameters> workerParamsProvider,
      Provider<ApolloClient> apolloClientProvider) {
    this.contextProvider = contextProvider;
    this.workerParamsProvider = workerParamsProvider;
    this.apolloClientProvider = apolloClientProvider;
  }

  @Override
  public ReactionWorker get() {
    return new ReactionWorker(contextProvider.get(), workerParamsProvider.get(), apolloClientProvider.get());
  }

  public static ReactionWorker_Factory create(Provider<Application> contextProvider,
      Provider<WorkerParameters> workerParamsProvider,
      Provider<ApolloClient> apolloClientProvider) {
    return new ReactionWorker_Factory(contextProvider, workerParamsProvider, apolloClientProvider);
  }

  public static ReactionWorker newInstance(Application context, WorkerParameters workerParams,
      ApolloClient apolloClient) {
    return new ReactionWorker(context, workerParams, apolloClient);
  }
}
