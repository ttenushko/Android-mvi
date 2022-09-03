package com.ttenushko.androidmvi.demo.presentation.dialogs

import android.content.DialogInterface

interface DialogFragmentDismissListener {
    fun onDialogFragmentDismiss(dialogFragment: BaseDialogFragment, dialog: DialogInterface)
}
