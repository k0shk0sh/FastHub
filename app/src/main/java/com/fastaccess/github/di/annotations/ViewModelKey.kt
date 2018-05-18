package com.fastaccess.github.di.annotations

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Created by Kosh on 12.05.18.
 */

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)