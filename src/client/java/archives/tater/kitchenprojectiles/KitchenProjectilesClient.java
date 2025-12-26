package archives.tater.kitchenprojectiles;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ConditionalItemModel;
import net.minecraft.client.renderer.item.properties.conditional.IsUsingItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KitchenProjectilesClient implements ClientModInitializer {
	public static final Identifier THROWING_PREDICATE = KitchenProjectiles.id("throwing");

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
				itemId -> itemId,
				itemId -> KitchenProjectiles.id("item/" + itemId.getPath() + "_throwing")
		));

		ModelLoadingPlugin.register(context -> {
            for (var modelId : knives.values()) {
                context.addModel(ExtraModelKey.create(), SimpleUnbakedExtraModel.blockStateModel(modelId));
            }

            context.modifyItemModelBeforeBake().register((unbaked, context1) -> {
                for (var pair : knives.entrySet()) {
                    var itemId = pair.getKey();
                    var modelId = pair.getValue();

                    if (!itemId.equals(context1.itemId())) continue;

                    return new ConditionalItemModel.Unbaked(new IsUsingItem(), new BlockModelWrapper.Unbaked(modelId, List.of()), unbaked);
                }
                return unbaked;
            });
		});
	}
}
