package com.wrbug.componentroutergradle

import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation

abstract class BaseTransform extends Transform {
    @Override
    final void transform(TransformInvocation transformInvocation) {
        try {
            println("BaseTransform.transform")
            safeTransform(transformInvocation)
        } catch (Throwable e) {
            e.printStackTrace()
        }
    }

    abstract void safeTransform(TransformInvocation transformInvocation);
}
