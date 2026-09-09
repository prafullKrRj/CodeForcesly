package com.prafullkumar.codeforcesly.visualizer.domain

interface VisualizerRepository {
    suspend fun getUserData(forceRefresh: Boolean = false): UserData
}
