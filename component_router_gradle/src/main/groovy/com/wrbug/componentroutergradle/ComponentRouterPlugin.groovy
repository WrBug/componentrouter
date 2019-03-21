package com.wrbug.componentroutergradle

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project

class ComponentRouterPlugin extends BasePlugin {

    @Override
    void onAppApply(Project project) {
        MergeMethodRouterFinderTransform transform = new MergeMethodRouterFinderTransform(project)
        ((BaseExtension) project.android).registerTransform(transform)
        ((BaseExtension) project.android).registerTransform(new MergeInstanceRouterFinderTransform(project))
    }

}