package com.sam.ayaana.Utils

sealed class DownloadState {
    object Idle : DownloadState()
    object Downloading : DownloadState()
    object Success : DownloadState()
    data class Failed(val message: String) : DownloadState()
}