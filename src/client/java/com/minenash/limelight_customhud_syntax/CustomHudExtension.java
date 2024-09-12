package com.minenash.limelight_customhud_syntax;

import com.minenash.customhud.HudElements.interfaces.ExecuteElement;
import com.minenash.customhud.HudElements.interfaces.HudElement;
import com.minenash.customhud.HudElements.interfaces.MultiElement;
import com.minenash.customhud.HudElements.list.ListProviderSet;
import com.minenash.customhud.VariableParser;
import com.minenash.customhud.complex.ComplexData;
import com.minenash.customhud.conditionals.ExpressionParser;
import com.minenash.customhud.conditionals.Operation;
import com.minenash.customhud.data.Profile;
import com.minenash.customhud.errors.Errors;
import io.wispforest.limelight.api.entry.*;
import io.wispforest.limelight.api.extension.LimelightExtension;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class CustomHudExtension implements LimelightExtension {
    public static final Identifier ID = Identifier.of("customhud_limelight_integration", "customhud");
    public static final CustomHudExtension INSTANCE = new CustomHudExtension();
    public static final String PROFILE_NAME = "␑Limelight";

    @Override
    public Identifier id() {
        return ID;
    }


    @Override
    public void gatherEntries(ResultGatherContext ctx, Consumer<ResultEntry> entryConsumer) {
        Errors.clearErrors(PROFILE_NAME);
        Profile profile = Profile.create(PROFILE_NAME);
        HudElement element = VariableParser.parseElement2("{" + ctx.searchText() + "}", profile, 0, profile.enabled, new ListProviderSet());

        if (element != null) {
            ComplexData.update(profile);
            entryConsumer.accept(new CustomHudResultEntry(element.getString(), false));
        }
    }

    @Override
    public @Nullable ResultEntryGatherer checkExclusiveGatherer(ResultGatherContext ctx) {
        Errors.clearErrors(PROFILE_NAME);
        String text = ctx.searchText();
        if (text.startsWith("$")) return expression(text.substring(1));
        if (text.startsWith("`")) return syntax(text.substring(1));
        return null;
    }


    public ResultEntryGatherer expression(String input) {
        Profile profile = Profile.create(PROFILE_NAME);
        Operation op = ExpressionParser.parseExpression(input, input, profile, 0, profile.enabled, new ListProviderSet(), false);
        ComplexData.update(profile);

        return (ctx1, entryConsumer) -> {
            String value = Double.toString(op.getValue());
            entryConsumer.accept(new CustomHudResultEntry(value, false));

            for (var e : Errors.getErrors(PROFILE_NAME)) {
                String str = "§4" + e.type().message + "§4" + e.context();
                entryConsumer.accept(new CustomHudResultEntry(str, true));
            }

        };
    }

    public ResultEntryGatherer syntax(String input) {
        Profile profile = Profile.create(PROFILE_NAME);
        List<HudElement> elements = VariableParser.addElements(input, profile, 0, profile.enabled, false, new ListProviderSet());
        ComplexData.update(profile);

        StringBuilder builder = new StringBuilder();
        for (HudElement element : elements) {
            if (element instanceof ExecuteElement ee)
                ee.run();
            else {
                String str = element.getString();
                if (str != null)
                    builder.append(str);
            }
        }

        String result = builder.toString();
        return (ctx1, entryConsumer) -> {
            entryConsumer.accept(new CustomHudResultEntry(result, false));
            for (var e : Errors.getErrors(PROFILE_NAME)) {
                String str = "§4" + e.type().message + "§4" + e.context();
                entryConsumer.accept(new CustomHudResultEntry(str, true));
            }

        };
    }

    public record CustomHudResultEntry(String result, boolean error) implements InvokeResultEntry {

        @Override
        public LimelightExtension extension() {
            return INSTANCE;
        }

        @Override
        public String entryId() {
            return result;
        }

        @Override
        public Text prefix() {
            return error ? Text.literal("§cError") : InvokeResultEntry.super.prefix();
        }

        @Override
        public Text text() {
            return Text.literal(result);
        }

        @Override
        public void run() {
            MinecraftClient.getInstance().setScreen(new ChatScreen(result));
        }
    }
}
