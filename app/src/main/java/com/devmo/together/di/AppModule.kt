package com.devmo.together.di

import com.devmo.together.repositories.AuthRepo
import com.devmo.together.repositories.HomeRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun firebaseAuthProvider(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
    @Provides
    fun firebaseDatabaseProvider():FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }
    @Provides
    @Singleton
    fun authRepo(auth: FirebaseAuth , mDatabase: FirebaseDatabase):AuthRepo{
        return AuthRepo(auth , mDatabase)
    }
    @Provides
    @Singleton
    fun homeRepo(auth: FirebaseAuth , mDatabase: FirebaseDatabase , storage: FirebaseStorage):HomeRepo{
        return HomeRepo(mDatabase , mAuth = auth ,storage )
    }
    @Provides
    @Singleton
    fun storage():FirebaseStorage{
        return FirebaseStorage.getInstance()
    }
}
