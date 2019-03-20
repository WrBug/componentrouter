package com.wrbug.componentroutergradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

class ComponentRouterPlugin extends BasePlugin {

    @Override
    void onAppApply(Project project) {
        println("onAppApply")
        MergeFinderTransform transform = new MergeFinderTransform(project)
        println(transform)
        ((BaseExtension) project.android).registerTransform(transform)
    }

}