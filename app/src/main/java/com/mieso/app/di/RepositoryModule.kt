package com.mieso.app.di

import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.data.repository.HomeRepositoryImpl
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.data.repository.CartRepositoryImpl
import com.mieso.app.data.repository.LocationRepository
import com.mieso.app.data.repository.LocationRepositoryImpl
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.data.repository.OrderRepositoryImpl
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.data.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}
