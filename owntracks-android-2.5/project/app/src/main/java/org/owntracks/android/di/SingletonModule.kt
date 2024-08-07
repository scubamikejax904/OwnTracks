package org.owntracks.android.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.security.KeyStore
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import org.owntracks.android.model.messages.MessageBase
import org.owntracks.android.test.CountingIdlingResourceShim
import org.owntracks.android.test.IdlingResourceWithData
import org.owntracks.android.test.SimpleIdlingResource

@InstallIn(SingletonComponent::class)
@Module
class SingletonModule {

  /**
   * This idling resource is idled when the outgoing message queue becomes empty
   *
   * @return a [CountingIdlingResourceShimShim] representing the size of the outgoing message queue
   */
  @Provides
  @Named("outgoingQueueIdlingResource")
  @Singleton
  fun provideOutgoingQueueIdlingResource(): CountingIdlingResourceShim =
      CountingIdlingResourceShim("outgoingQueueIdlingResource", true)

  /**
   * This idling resource is idled when a message with is published in response to a remote command.
   * Useful for tests that want to trigger a response message and then wait for the app to publish
   * it.
   *
   * @return a [SimpleIdlingResource]
   */
  @Provides
  @Named("publishResponseMessageIdlingResource")
  @Singleton
  fun provideResponseMessageIdlingResource(): SimpleIdlingResource =
      SimpleIdlingResource("publishResponseMessageIdlingResource", false)

  /**
   * Idles once a configuration import has been completed
   *
   * @return a [SimpleIdlingResource]
   */
  @Provides
  @Named("importConfigurationIdlingResource")
  @Singleton
  fun provideImportConfigurationIdlingResource(): SimpleIdlingResource =
      SimpleIdlingResource("importConfigurationIdlingResource", false)

  /**
   * Used to help determine that a mock location set by a test has been received by the device.
   *
   * @return a [SimpleIdlingResource]
   */
  @Provides
  @Singleton
  @Named("mockLocationIdlingResource")
  fun provideLocationIdlingResource(): SimpleIdlingResource =
      SimpleIdlingResource("mockLocationIdlingResource", false)

  /**
   * Idles once the ContactsActivity has finished loading
   *
   * @return a [CountingIdlingResourceShim
   */
  @Provides
  @Singleton
  @Named("contactsActivityIdlingResource")
  fun provideContactsActivityIdlingResource(): CountingIdlingResourceShim =
      CountingIdlingResourceShim("contactsActivityIdlingResource", true)

  /**
   * This idling resource is used to detect that a clear message has propagated and updated through
   * to the ContactsAdapter
   *
   * @return a [SimpleIdlingResource]
   */
  @Provides
  @Singleton
  @Named("contactsClearedIdlingResource")
  fun provideContactsClearedIdlingResource(): SimpleIdlingResource =
      SimpleIdlingResource("contactsClearedIdlingResource", true)

  @Provides
  @Singleton
  @Named("messageReceivedIdlingResource")
  fun provideLocationMessageIdlingResource(): IdlingResourceWithData<MessageBase> =
      IdlingResourceWithData("messageReceivedIdlingResource", compareBy { it.messageId })

  @ApplicationScope
  @Provides
  fun providesCoroutineScope(
      @CoroutineScopes.DefaultDispatcher defaultDispatcher: CoroutineDispatcher
  ): CoroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)

  @Provides
  fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
      NotificationManagerCompat.from(context)

  @Provides fun provideOkHttpClient(): OkHttpClient = OkHttpClient()

  @Provides
  @Named("CAKeyStore")
  @Singleton
  fun privateAndroidCaKeyStore(): KeyStore {
    return KeyStore.getInstance("AndroidCAStore").apply { load(null) }
  }
}
