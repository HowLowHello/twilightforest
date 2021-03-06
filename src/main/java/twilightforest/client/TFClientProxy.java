package twilightforest.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.EnumHelperClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import twilightforest.TFCommonProxy;
import twilightforest.TFSounds;
import twilightforest.client.model.entity.*;
import twilightforest.client.model.entity.finalcastle.ModelTFCastleGuardian;
import twilightforest.client.renderer.entity.*;
import twilightforest.client.renderer.tileentity.*;
import twilightforest.client.shader.ShaderManager;
import twilightforest.compat.TFCompat;
import twilightforest.entity.*;
import twilightforest.entity.boss.*;
import twilightforest.entity.finalcastle.EntityTFCastleGuardian;
import twilightforest.entity.passive.*;
import twilightforest.tileentity.TileEntityTFTrophy;
import twilightforest.tileentity.critters.*;

public class TFClientProxy extends TFCommonProxy {

	private boolean isDangerOverlayShown;

	public static MusicTicker.MusicType TFMUSICTYPE;

	@Override
	public void preInit() {
		RenderingRegistry.registerEntityRenderingHandler(EntityTFBoar.class, m -> new RenderTFBoar(m, new ModelTFBoar()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFBighorn.class, m -> new RenderTFBighorn(m, new ModelTFBighorn(), new ModelTFBighornFur(), 0.7F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFDeer.class, m -> new RenderTFDeer(m, new ModelTFDeer(), 0.7F));

		RenderingRegistry.registerEntityRenderingHandler(EntityTFRedcap.class, m -> new RenderTFBiped<>(m, new ModelTFRedcap(), 0.4F, "redcap.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSkeletonDruid.class, m -> new RenderTFBiped<>(m, new ModelTFSkeletonDruid(), 0.5F, "skeletondruid.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFWraith.class, m -> new RenderTFWraith(m, new ModelTFWraith(), 0.5F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHydra.class, m -> new RenderTFHydra(m, new ModelTFHydra(), 4.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFLich.class, m -> new RenderTFLich(m, new ModelTFLich(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFPenguin.class, m -> new RenderTFBird(m, new ModelTFPenguin(), 0.375F, "penguin.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFLichMinion.class, m -> new RenderTFBiped<>(m, new ModelTFLichMinion(), 0.5F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFLoyalZombie.class, m -> new RenderTFBiped<>(m, new ModelTFLoyalZombie(), 0.5F, "textures/entity/zombie/zombie.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTinyBird.class, m -> new RenderTFTinyBird(m, new ModelTFTinyBird(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSquirrel.class, m -> new RenderTFGenericLiving<>(m, new ModelTFSquirrel(), 1.0F, "squirrel2.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFBunny.class, m -> new RenderTFBunny(m, new ModelTFBunny(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFRaven.class, m -> new RenderTFBird(m, new ModelTFRaven(), 1.0F, "raven.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFQuestRam.class, RenderTFQuestRam::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFKobold.class, m -> new RenderTFKobold(m, new ModelTFKobold(), 0.4F, "kobold.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFBoggard.class, m -> new RenderTFBiped<>(m, new ModelTFLoyalZombie(), 0.625F, "kobold.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMosquitoSwarm.class, m -> new RenderTFGenericLiving<>(m, new ModelTFMosquitoSwarm(), 0.0F, "mosquitoswarm.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFDeathTome.class, m -> new RenderTFGenericLiving<>(m, new ModelTFDeathTome(), 0.3F, "textures/entity/enchanting_table_book.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMinotaur.class, m -> new RenderTFBiped<>(m, new ModelTFMinotaur(), 0.625F, "minotaur.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMinoshroom.class, m -> new RenderTFMinoshroom(m, new ModelTFMinoshroom(), 0.625F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFFireBeetle.class, m -> new RenderTFGenericLiving<>(m, new ModelTFFireBeetle(), 0.8F, "firebeetle.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSlimeBeetle.class, m -> new RenderTFSlimeBeetle(m, new ModelTFSlimeBeetle(), 0.6F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFPinchBeetle.class, m -> new RenderTFGenericLiving<>(m, new ModelTFPinchBeetle(), 0.6F, "pinchbeetle.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMistWolf.class, RenderTFMistWolf::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMobileFirefly.class, RenderTFMobileFirefly::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMiniGhast.class, m -> new RenderTFGhast<>(m, new ModelTFGhast(), 0.625F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTowerGolem.class, m -> new RenderTFTowerGolem(m, new ModelTFTowerGolem(), 0.75F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTowerTermite.class, m -> new RenderTFGenericLiving<>(m, new ModelSilverfish(), 0.3F, "towertermite.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTowerGhast.class, m -> new RenderTFTowerGhast(m, new ModelTFGhast(), 3.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFUrGhast.class, m -> new RenderTFUrGhast(m, new ModelTFTowerBoss(), 8.0F, 24F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFBlockGoblin.class, m -> new RenderTFBlockGoblin(m, new ModelTFBlockGoblin(), 0.4F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFGoblinChain.class, m -> new RenderTFSpikeBlock(m, new ModelTFGoblinChain()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSpikeBlock.class, m -> new RenderTFSpikeBlock(m, new ModelTFSpikeBlock()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFGoblinKnightUpper.class, m -> new RenderTFGoblinKnightUpper(m, new ModelTFGoblinKnightUpper(), 0.625F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFGoblinKnightLower.class, m -> new RenderTFBiped<>(m, new ModelTFGoblinKnightLower(), 0.625F, "doublegoblin.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHelmetCrab.class, m -> new RenderTFGenericLiving<>(m, new ModelTFHelmetCrab(), 0.625F, "helmetcrab.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFKnightPhantom.class, m -> new RenderTFKnightPhantom(m, new ModelTFKnightPhantom2(), 0.625F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFNaga.class, m -> new RenderTFNaga(m, new ModelTFNaga(), 1.45F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFNagaSegment.class, m -> new RenderTFNagaSegment(m, new ModelTFNaga()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSwarmSpider.class, RenderTFSwarmSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFKingSpider.class, RenderTFKingSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTowerBroodling.class, RenderTFTowerBroodling::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHedgeSpider.class, RenderTFHedgeSpider::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFRedcapSapper.class, m -> new RenderTFBiped<>(m, new ModelTFRedcap(), 0.4F, "redcapsapper.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMazeSlime.class, m -> new RenderTFMazeSlime(m, 0.625F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFYeti.class, m -> new RenderTFYeti(m, new ModelTFYeti(), 0.625F, "yeti2.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFProtectionBox.class, RenderTFProtectionBox::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFYetiAlpha.class, m -> new RenderTFYeti(m, new ModelTFYetiAlpha(), 1.75F, "yetialpha.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFWinterWolf.class, RenderTFWinterWolf::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSnowGuardian.class, RenderTFSnowGuardian::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFIceShooter.class, RenderTFIceShooter::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFIceExploder.class, RenderTFIceExploder::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSnowQueen.class, RenderTFSnowQueen::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSnowQueenIceShield.class, RenderTFSnowQueenIceShield::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTroll.class, m -> new RenderTFBiped<>(m, new ModelTFTroll(), 0.625F, "troll.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFGiantMiner.class, RenderTFGiant::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFIceCrystal.class, RenderTFIceCrystal::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFChainBlock.class, m -> new RenderTFChainBlock(m, new ModelTFSpikeBlock()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFCubeOfAnnihilation.class, RenderTFCubeOfAnnihilation::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHarbingerCube.class, RenderTFHarbingerCube::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFAdherent.class, m -> new RenderTFAdherent(m, new ModelTFAdherent(), 0.625F, "adherent.png"));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFRovingCube.class, RenderTFRovingCube::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFRisingZombie.class, m -> new RenderTFBiped<>(m, new ModelTFRisingZombie(), 0.5F, "textures/entity/zombie/zombie.png"));

		RenderingRegistry.registerEntityRenderingHandler(EntityTFCastleGuardian.class, m -> new RenderTFCastleGuardian(m, new ModelTFCastleGuardian(), 2.0F, "finalcastle/castle_guardian.png"));

		// projectiles
		RenderingRegistry.registerEntityRenderingHandler(EntityTFNatureBolt.class, m -> new SpriteRenderer<>(m, Items.WHEAT_SEEDS, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFLichBolt.class, m -> new SpriteRenderer<>(m, Items.ENDER_PEARL, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTwilightWandBolt.class, m -> new SpriteRenderer<>(m, Items.ENDER_PEARL, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFTomeBolt.class, m -> new SpriteRenderer<>(m, Items.PAPER, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHydraMortar.class, RenderTFHydraMortar::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSlimeProjectile.class, m -> new SpriteRenderer<>(m, Items.SLIME_BALL, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFMoonwormShot.class, RenderTFMoonwormShot::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFCharmEffect.class, m -> new RenderTFCharm(m, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFLichBomb.class, m -> new SpriteRenderer<>(m, Items.MAGMA_CREAM, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFThrownWep.class, RenderTFThrownWep::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFFallingIce.class, RenderTFFallingIce::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFIceBomb.class, RenderTFThrownIce::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityTFIceSnowball.class, m -> new SpriteRenderer<>(m, Items.SNOWBALL, Minecraft.getInstance().getRenderItem()));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFSlideBlock.class, RenderTFSlideBlock::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySeekerArrow.class, RenderDefaultArrow::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityIceArrow.class, RenderDefaultArrow::new);

		// I guess the hydra gets its own section
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHydraHead.class, m -> new RenderTFHydraHead(m, new ModelTFHydraHead(), 1.0F));
		RenderingRegistry.registerEntityRenderingHandler(EntityTFHydraNeck.class, m -> new RenderTFGenericLiving<>(m, new ModelTFHydraNeck(), 1.0F, "hydra4.png"));
	}

	@Override
	public void init() {

		MinecraftForge.EVENT_BUS.register(new LoadingScreenListener());

		// tile entities
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFFireflyTicking.class, new TileEntityTFFireflyRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFCicadaTicking.class, new TileEntityTFCicadaRenderer());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFNagaSpawner.class, new TileEntityMobSpawnerRenderer());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFLichSpawner.class, new TileEntityMobSpawnerRenderer());
//		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFHydraSpawner.class, new TileEntityMobSpawnerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFMoonwormTicking.class, new TileEntityTFMoonwormRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTFTrophy.class, new TileEntityTFTrophyRenderer());

		TFMUSICTYPE = EnumHelperClient.addMusicType("TFMUSIC", TFSounds.MUSIC, 1200, 12000);

		ShaderManager.initShaders();

		ClientCommandHandler.instance.registerCommand(new CommandBase() {
			@Override
			public String getName() {
				return "tfreload";
			}

			@Override
			public String getUsage(ICommandSender sender) {
				return "commands.tffeature.reload";
			}

			@Override
			public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
				if(FMLCommonHandler.instance().getEffectiveSide().isClient()) {
					Minecraft.getInstance().player.sendMessage(new StringTextComponent("Reloading Twilight Forest Shaders!"));
					twilightforest.client.shader.ShaderManager.getShaderReloadListener().onResourceManagerReload(net.minecraft.client.Minecraft.getInstance().getResourceManager());
					if (TFCompat.IMMERSIVEENGINEERING.isActivated())
						twilightforest.compat.ie.IEShaderRegister.initShaders();
				}
			}
		});
	}

	public boolean isDangerOverlayShown() {
		return isDangerOverlayShown;
	}

	public void setDangerOverlayShown(boolean isDangerOverlayShown) {
		this.isDangerOverlayShown = isDangerOverlayShown;
	}

	@Override
	public boolean doesPlayerHaveAdvancement(PlayerEntity player, ResourceLocation advId) {
		if (player instanceof ClientPlayerEntity) {
			ClientAdvancementManager manager = ((ClientPlayerEntity) player).connection.getAdvancementManager();
			Advancement adv = manager.getAdvancementList().getAdvancement(advId);
			if (adv == null) return false;
			AdvancementProgress progress = manager.advancementToProgress.get(adv);
			return progress != null && progress.isDone();
		}

		return super.doesPlayerHaveAdvancement(player, advId);
	}

	@Override
	public TileEntityTFCicada getNewCicadaTE() {
		return new TileEntityTFCicadaTicking();
	}

	@Override
	public TileEntityTFFirefly getNewFireflyTE() {
		return new TileEntityTFFireflyTicking();
	}

	@Override
	public TileEntityTFMoonworm getNewMoonwormTE() {
		return new TileEntityTFMoonwormTicking();
	}

	@Override
	public void registerCritterTileEntities() {
		GameRegistry.registerTileEntity(TileEntityTFFireflyTicking.class,  prefix("firefly" ));
		GameRegistry.registerTileEntity(TileEntityTFCicadaTicking.class,   prefix("cicada"  ));
		GameRegistry.registerTileEntity(TileEntityTFMoonwormTicking.class, prefix("moonworm"));
	}
}
