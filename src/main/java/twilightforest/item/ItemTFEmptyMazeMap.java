package twilightforest.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AbstractMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ItemTFEmptyMazeMap extends AbstractMapItem {
	boolean mapOres;

	protected ItemTFEmptyMazeMap(boolean mapOres, Properties props) {
		super(props.group(TFItems.creativeTab));
		this.mapOres = mapOres;
	}

	// [VanillaCopy] ItemEmptyMap.onItemRightClick calling own setup method
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = ItemTFMazeMap.setupNewMap(worldIn, playerIn.getX(), playerIn.getZ(), (byte) 0, true, false, playerIn.getY(), this.mapOres);
		ItemStack itemstack1 = playerIn.getHeldItem(handIn);
		itemstack1.shrink(1);

		if (itemstack1.isEmpty()) {
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		} else {
			if (!playerIn.inventory.addItemStackToInventory(itemstack.copy())) {
				playerIn.dropItem(itemstack, false);
			}

			playerIn.addStat(Stats.ITEM_USED.get(this));
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack1);
		}
	}
}
