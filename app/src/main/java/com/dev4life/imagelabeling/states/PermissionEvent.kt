package com.dev4life.imagelabeling.states

sealed class PermissionEvent {
    data object Idle : PermissionEvent()
    data object RequestPermissions : PermissionEvent()
    data object ShowRationale : PermissionEvent()
    data class PermissionResult(
        val grantedPermissions: List<String>,
        val deniedPermissions: List<String>
    ) : PermissionEvent()
}