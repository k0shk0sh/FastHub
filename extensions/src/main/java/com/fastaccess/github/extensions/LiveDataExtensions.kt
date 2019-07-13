package com.fastaccess.github.extensions


// https://github.com/oronbz/rxlivedata

import androidx.annotation.MainThread
import androidx.lifecycle.*


@MainThread
fun <X, Y> LiveData<X>.map(func: (X) -> Y): LiveData<Y> {
    return Transformations.map(this) { func(it) }
}

@MainThread
fun <X, Y> LiveData<X>.switchMap(func: (X) -> LiveData<Y>): LiveData<Y> {
    return Transformations.switchMap(this) { func(it) }
}

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, observer: (t: T) -> Unit) {
    this.observe(owner, Observer { it?.let(observer) })
}

fun <T> LiveData<T>.observeNull(owner: LifecycleOwner, observer: (t: T?) -> Unit) {
    this.observe(owner, Observer { observer.invoke(it) })
}

@MainThread
fun <X> LiveData<X>.filter(test: (X) -> Boolean): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        if (it != null && test(it)) {
            result.value = it
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.filterNullables(test: (X?) -> Boolean): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        if (test(it)) {
            result.value = it
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X?>.filterNull(): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        it?.let {
            result.value = it
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.take(count: Int): LiveData<X> {
    var taken = 0
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        it?.let {
            if (taken < count) {
                result.value = it
                taken++
            }

            if (taken >= count) {
                result.removeSource(this)
            }
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.skip(count: Int): LiveData<X> {
    var skipped = 0
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        it?.let {
            if (skipped >= count) {
                result.value = it
            }

            if (skipped < count) {
                skipped++
            }
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.distinctUntilChanged(comparer: (X, X) -> Boolean): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        it?.let {
            val currentValue = result.value
            if (currentValue != null) {
                if (!comparer(it, currentValue)) {
                    result.value = it
                }
            } else {
                result.value = it
            }
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.distinctUntilChanged(): LiveData<X> {
    return distinctUntilChanged { v1, v2 ->
        v1 == v2
    }
}

@MainThread
fun <X> List<LiveData<X>>.merge(): LiveData<X> {
    val result = MediatorLiveData<X>()

    this.forEach {
        result.addSource(it) {
            it?.let {
                result.value = it
            }
        }
    }

    return result
}

@MainThread
fun <X, Y> LiveData<X>.withLatestFrom(source: LiveData<Y>): LiveData<Y> {
    val result = MediatorLiveData<Y>()
    var sourceResult: Y? = null

    result.addSource<Y>(source) {
        sourceResult = it
    }

    result.addSource(this) {
        if (it != null && sourceResult != null) {
            result.value = sourceResult!!
        }
    }

    return result
}

@MainThread
fun <X, Y, Z> LiveData<X>.withLatestFrom(source: LiveData<Y>, resultSelector: (Pair<X, Y>) -> Z): LiveData<Z> {
    val result = MediatorLiveData<Z>()
    var sourceResult: Y? = null

    result.addSource(source) {
        sourceResult = it
    }

    result.addSource(this) {
        if (it != null && sourceResult != null) {
            result.value = resultSelector(Pair(it, sourceResult!!))
        }
    }

    return result
}

@MainThread
fun <X> LiveData<X>.startWith(initialValue: X): LiveData<X> {
    val result = MediatorLiveData<X>()

    result.addSource(this) {
        result.value = it
    }

    result.value = initialValue
    return result
}