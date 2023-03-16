package com.foggyskies.petapp.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.foggyskies.petapp.presentation.ui.profile.MENUS

open class MenuVisibilityHelper(
    private val action: ((Boolean) -> Unit)? = null
) {

    private val hashMapMenus = hashMapOf<MENUS, MutableState<Boolean>>()

    fun setVisibilityMenu(menu: MENUS, value: Boolean) {
        if (hashMapMenus.keys.contains(menu)) {
            hashMapMenus[menu]?.value = value
        } else {
            hashMapMenus[menu] = mutableStateOf(value)
        }
    }

    fun changeVisibilityMenu(menuVariable: MENUS, secondAction: (() -> Unit)? = null) {
        if (hashMapMenus.keys.contains(menuVariable)) {
            hashMapMenus[menuVariable]?.value = !hashMapMenus[menuVariable]?.value!!
        } else {
            hashMapMenus[menuVariable] = mutableStateOf(true)
        }
        action?.let { it(!hashMapMenus[menuVariable]?.value!!) }
        secondAction?.invoke()
    }

    fun getMenuVisibleValue(menu: MENUS): MutableState<Boolean> {
        if (!hashMapMenus.keys.contains(menu)) {
            hashMapMenus[menu] = mutableStateOf(false)
        }
        return hashMapMenus[menu] ?: mutableStateOf(false)
    }
}