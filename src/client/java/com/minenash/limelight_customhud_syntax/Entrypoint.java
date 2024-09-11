package com.minenash.limelight_customhud_syntax;

import io.wispforest.limelight.api.LimelightEntrypoint;
import io.wispforest.limelight.api.extension.LimelightExtension;

import java.util.function.Consumer;

public class Entrypoint implements LimelightEntrypoint {

	@Override
	public void registerExtensions(Consumer<LimelightExtension> extensionRegistry) {
		extensionRegistry.accept(CustomHudExtension.INSTANCE);
	}

}