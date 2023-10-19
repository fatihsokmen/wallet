package com.github.fatihsokmen.wallet.core

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface StringResources {
    fun getString(resourceId: Int): String
}

class StringResourcesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : StringResources {

    override fun getString(resourceId: Int) = context.getString(resourceId)
}