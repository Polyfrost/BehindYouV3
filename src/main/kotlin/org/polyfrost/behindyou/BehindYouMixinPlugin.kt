package org.polyfrost.behindyou

import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class BehindYouMixinPlugin : IMixinConfigPlugin {

    override fun getMixins(): MutableList<String> {
        val result = mutableListOf<String>()

        //#if MC >= 1.16.5
        //$$ result.add("client.Mixin_Camera_AdjustLevel")
        //#endif

        return result
    }

    override fun onLoad(mixinPackage: String) {
    }

    override fun getRefMapperConfig(): String? {
        return null
    }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
        return true
    }

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {
    }

    override fun preApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

    override fun postApply(
        targetClassName: String,
        targetClass: ClassNode,
        mixinClassName: String,
        mixinInfo: IMixinInfo
    ) {
    }

}
