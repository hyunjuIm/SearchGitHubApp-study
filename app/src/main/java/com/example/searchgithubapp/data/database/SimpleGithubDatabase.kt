package com.example.searchgithubapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.searchgithubapp.data.dao.RepositoryDao
import com.example.searchgithubapp.data.entity.GithubRepoEntity

@Database(entities = [GithubRepoEntity::class], version = 1)
abstract class SimpleGithubDatabase:RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}