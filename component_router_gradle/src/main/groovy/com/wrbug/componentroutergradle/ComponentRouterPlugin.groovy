package com.wrbug.componentroutergradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

class ComponentRouterPlugin extends BasePlugin {

    @Override
    void onAppApply(Project project) {
        MergeFinderTransform transform = new MergeFinderTransform(project)
        ((BaseExtension) project.android).registerTransform(transform)
    }

}