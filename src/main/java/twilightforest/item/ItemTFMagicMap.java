package twilightforest.item;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import twilightforest.TFFeature;
import twilightforest.TFMagicMapData;
import twilightforest.biomes.TFBiomes;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ItemTFMagicMap extends FilledMapItem {
	public static final String STR_ID = "magicmap";
	private static final Map<ResourceLocation, MapColorBrightness> BIOME_COLORS = new HashMap<>();

	protected ItemTFMagicMap(Properties props) {
		super(props.maxStackSize(1));
	}

	private static class MapColorBrightness {
		public MaterialColor color;
		public int brightness;

		public MapColorBrightness(MaterialColor color, int brightness) {
			this.color = color;
			this.brightness = brightness;
		}

		public MapColorBrightness(MaterialColor color) {
			this.color = color;
			this.brightness = 1;
		}
	}

	// [VanillaCopy] super with own id
	public static ItemStack setupNewMap(World world, double worldX, double worldZ, byte scale, boolean trackingPosition, boolean unlimitedTracking) {
		// TF - own item and string id TODO: unique id may not be used in 1.14
		//ItemStack itemstack = new ItemStack(TFItems.magic_map, 1, world.getUniqueDataId(ItemTFMagicMap.STR_ID));
		ItemStack itemstack = new ItemStack(TFItems.magic_map.get());
		String s = ItemTFMagicMap.STR_ID + "_" + world.getNextMapId();
		MapData mapdata = new TFMagicMapData(s);
		world.registerMapData(mapdata);
		itemstack.getOrCreateTag().putString("map", s);
		mapdata.scale = scale;
		mapdata.calculateMapCenter(worldX, worldZ, mapdata.scale);
		mapdata.dimension = world.dimension.getType();
		mapdata.trackingPosition = trackingPosition;
		mapdata.unlimitedTracking = unlimitedTracking;
		mapdata.markDirty();
		return itemstack;
	}

	// [VanillaCopy] super, with own string ID and class, narrowed types
	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static TFMagicMapData loadMapData(int mapId, World worldIn) {
		String s = STR_ID + "_" + mapId;
		return (TFMagicMapData) worldIn.loadData(TFMagicMapData.class, s);
	}

	// [VanillaCopy] super, with own string ID and class
	@Override
	public TFMagicMapData getMapData(ItemStack stack, World worldIn) {
		String s = STR_ID + "_" + stack.getMetadata();
		TFMagicMapData mapdata = (TFMagicMapData) worldIn.loadData(TFMagicMapData.class, s);

		if (mapdata == null && !worldIn.isRemote) {
			stack.setDamage(worldIn.getUniqueDataId(STR_ID));
			s = STR_ID + "_" + stack.getMetadata();
			mapdata = new TFMagicMapData(s);
			mapdata.scale = 3;
			mapdata.calculateMapCenter((double) worldIn.getWorldInfo().getSpawnX(), (double) worldIn.getWorldInfo().getSpawnZ(), mapdata.scale);
			mapdata.dimension = worldIn.dimension.getType();
			mapdata.markDirty();
			worldIn.setData(s, mapdata);
		}

		return mapdata;
	}

	@Override
	public void updateMapData(World world, Entity viewer, MapData data) {
		if (world.dimension.getType() == data.dimension && viewer instanceof PlayerEntity) {
			int biomesPerPixel = 4;
			int blocksPerPixel = 16; // don't even bother with the scale, just hardcode it
			int centerX = data.xCenter;
			int centerZ = data.zCenter;
			int viewerX = MathHelper.floor(viewer.getX() - (double) centerX) / blocksPerPixel + 64;
			int viewerZ = MathHelper.floor(viewer.getZ() - (double) centerZ) / blocksPerPixel + 64;
			int viewRadiusPixels = 512 / blocksPerPixel;

			// use the generation map, which is larger scale than the other biome map
			int startX = (centerX / blocksPerPixel - 64) * biomesPerPixel;
			int startZ = (centerZ / blocksPerPixel - 64) * biomesPerPixel;
			Biome[] biomes = world.getBiomeProvider().getBiomesForGeneration((Biome[]) null, startX, startZ, 128 * biomesPerPixel, 128 * biomesPerPixel);

			for (int xPixel = viewerX - viewRadiusPixels + 1; xPixel < viewerX + viewRadiusPixels; ++xPixel) {
				for (int zPixel = viewerZ - viewRadiusPixels - 1; zPixel < viewerZ + viewRadiusPixels; ++zPixel) {
					if (xPixel >= 0 && zPixel >= 0 && xPixel < 128 && zPixel < 128) {
						int xPixelDist = xPixel - viewerX;
						int zPixelDist = zPixel - viewerZ;
						boolean shouldFuzz = xPixelDist * xPixelDist + zPixelDist * zPixelDist > (viewRadiusPixels - 2) * (viewRadiusPixels - 2);

						Biome biome = biomes[xPixel * biomesPerPixel + zPixel * biomesPerPixel * 128 * biomesPerPixel];

						// make streams more visible
						Biome overBiome = biomes[xPixel * biomesPerPixel + zPixel * biomesPerPixel * 128 * biomesPerPixel + 1];
						Biome downBiome = biomes[xPixel * biomesPerPixel + (zPixel * biomesPerPixel + 1) * 128 * biomesPerPixel];
						if (overBiome == TFBiomes.stream.get() || downBiome == TFBiomes.stream.get()) {
							biome = TFBiomes.stream.get();
						}

						MapColorBrightness colorBrightness = this.getMapColorPerBiome(world, biome);

						MaterialColor mapcolor = colorBrightness.color;
						int brightness = colorBrightness.brightness;

						if (zPixel >= 0 && xPixelDist * xPixelDist + zPixelDist * zPixelDist < viewRadiusPixels * viewRadiusPixels && (!shouldFuzz || (xPixel + zPixel & 1) != 0)) {
							byte orgPixel = data.colors[xPixel + zPixel * 128];
							byte ourPixel = (byte) (mapcolor.colorIndex * 4 + brightness);

							if (orgPixel != ourPixel) {
								data.colors[xPixel + zPixel * 128] = ourPixel;
								data.updateMapData(xPixel, zPixel);
							}

							// look for TF features
							int worldX = (centerX / blocksPerPixel + xPixel - 64) * blocksPerPixel;
							int worldZ = (centerZ / blocksPerPixel + zPixel - 64) * blocksPerPixel;
							if (TFFeature.isInFeatureChunk(world, worldX, worldZ)) {
								byte mapX = (byte) ((worldX - centerX) / (float) blocksPerPixel * 2F);
								byte mapZ = (byte) ((worldZ - centerZ) / (float) blocksPerPixel * 2F);
								TFFeature feature = TFFeature.getFeatureAt(worldX, worldZ, world);
								TFMagicMapData tfData = (TFMagicMapData) data;
								tfData.tfDecorations.add(new TFMagicMapData.TFMapDecoration(feature.ordinal(), mapX, mapZ, (byte) 8));
								//TwilightForestMod.LOGGER.info("Found feature at {}, {}. Placing it on the map at {}, {}", worldX, worldZ, mapX, mapZ);
							}
						}
					}
				}
			}
		}
	}

	private MapColorBrightness getMapColorPerBiome(World world, Biome biome) {
		if (BIOME_COLORS.isEmpty()) {
			setupBiomeColors();
		}
		MapColorBrightness color = BIOME_COLORS.get(biome.getRegistryName());
		if (color != null) {
			return color;
		} else {
			return new MapColorBrightness(biome.getSurfaceBuilderConfig().getTop().getMaterialColor(world, BlockPos.ZERO));
		}
	}

	private static void setupBiomeColors() {
		putBiomeColor(TFBiomes.twilightForest, new MapColorBrightness(MaterialColor.FOLIAGE, 1));
		putBiomeColor(TFBiomes.denseTwilightForest, new MapColorBrightness(MaterialColor.FOLIAGE, 0));
		putBiomeColor(TFBiomes.tfLake, new MapColorBrightness(MaterialColor.WATER, 3));
		putBiomeColor(TFBiomes.stream, new MapColorBrightness(MaterialColor.WATER, 1));
		putBiomeColor(TFBiomes.tfSwamp, new MapColorBrightness(MaterialColor.DIAMOND, 3));
		putBiomeColor(TFBiomes.fireSwamp, new MapColorBrightness(MaterialColor.NETHERRACK, 1));
		putBiomeColor(TFBiomes.clearing, new MapColorBrightness(MaterialColor.GRASS, 2));
		putBiomeColor(TFBiomes.oakSavanna, new MapColorBrightness(MaterialColor.GRASS, 0));
		putBiomeColor(TFBiomes.highlands, new MapColorBrightness(MaterialColor.DIRT, 0));
		putBiomeColor(TFBiomes.thornlands, new MapColorBrightness(MaterialColor.WOOD, 3));
		putBiomeColor(TFBiomes.highlandsCenter, new MapColorBrightness(MaterialColor.LIGHT_GRAY, 2));
		putBiomeColor(TFBiomes.fireflyForest, new MapColorBrightness(MaterialColor.EMERALD, 1));
		putBiomeColor(TFBiomes.darkForest, new MapColorBrightness(MaterialColor.GREEN, 3));
		putBiomeColor(TFBiomes.darkForestCenter, new MapColorBrightness(MaterialColor.ADOBE, 3));
		putBiomeColor(TFBiomes.snowy_forest, new MapColorBrightness(MaterialColor.SNOW, 1));
		putBiomeColor(TFBiomes.glacier, new MapColorBrightness(MaterialColor.ICE, 1));
		putBiomeColor(TFBiomes.mushrooms, new MapColorBrightness(MaterialColor.ADOBE, 0));
		putBiomeColor(TFBiomes.deepMushrooms, new MapColorBrightness(MaterialColor.PINK, 0));
		putBiomeColor(TFBiomes.enchantedForest, new MapColorBrightness(MaterialColor.LIME, 2));
		putBiomeColor(TFBiomes.spookyForest, new MapColorBrightness(MaterialColor.PURPLE, 0));
	}

	private static void putBiomeColor(RegistryObject<Biome> biome, MapColorBrightness color) {
		BIOME_COLORS.put(biome.get().getRegistryName(), color);
	}

	@Override
	public void onCreated(ItemStack stack, World world, PlayerEntity player) {
		// disable zooming
	}

	//TODO: How to packet?
//	@Override
//	@Nullable
//	public IPacket<?> getUpdatePacket(ItemStack stack, World world, PlayerEntity player) {
//		IPacket<?> p = super.getUpdatePacket(stack, world, player);
//		if (p instanceof SPacketMaps) {
//			TFMagicMapData mapdata = getMapData(stack, world);
//			return TFPacketHandler.CHANNEL.getPacketFrom(new PacketMagicMap(stack.getItemDamage(), mapdata, (SPacketMaps) p));
//		} else {
//			return p;
//		}
//	}
}
