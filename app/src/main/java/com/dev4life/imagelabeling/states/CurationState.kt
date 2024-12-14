package com.dev4life.imagelabeling.states

sealed class CurationState {
    object Idle : CurationState()
    object ModulesAvailable : CurationState()
    object ModulesNotAvailable : CurationState()
    object Offline : CurationState()
    object Error : CurationState()
}