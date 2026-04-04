package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;

import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.client.data.models.model.ItemModelUtils.*;

public class KitchenProjectilesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRenderers.register(KitchenProjectiles.KNIFE_ENTITY, KnifeEntityRenderer::new);

		var knives = Stream.of(
                ModItems.FLINT_KNIFE.get(),
                ModItems.COPPER_KNIFE.get(),
                ModItems.IRON_KNIFE.get(),
                ModItems.GOLDEN_KNIFE.get(),
                ModItems.DIAMOND_KNIFE.get(),
                ModItems.NETHERITE_KNIFE.get()
		).map(BuiltInRegistries.ITEM::getKey).collect(Collectors.toMap(
				Function.identity(),
				itemId -> KitchenProjectiles.id("item/" + itemId.getPath() + "_throwing")
		));

		ModelLoadingPlugin.register(context -> {
            for (var modelId : knives.values()) {
                context.addModel(ExtraModelKey.create(), SimpleUnbakedExtraModel.blockStateModel(modelId));
            }

            context.modifyItemModelBeforeBake().register(ModelModifier.WRAP_PHASE, (unbaked, context1) -> {
                var usedModelId = knives.get(context1.itemId());

                return usedModelId == null ? unbaked : conditional(isUsingItem(), plainModel(usedModelId), unbaked);
            });
		});
	}
}
