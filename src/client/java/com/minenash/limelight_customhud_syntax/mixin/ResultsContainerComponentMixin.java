package com.minenash.limelight_customhud_syntax.mixin;

import com.minenash.limelight_customhud_syntax.CustomHudExtension;
import io.wispforest.limelight.api.entry.ResultEntry;
import io.wispforest.limelight.impl.ui.ResultsContainerComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ResultsContainerComponent.class, remap = false)
public class ResultsContainerComponentMixin {

    @Inject(method = "lambda$rebuildContents$2", at = @At(value = "RETURN"), cancellable = true)
    private static void bumpToTop(ResultEntry entry, CallbackInfoReturnable<Integer> cir) {
        if (entry instanceof CustomHudExtension.CustomHudResultEntry e && !e.error())
            cir.setReturnValue(-1);
    }

}
