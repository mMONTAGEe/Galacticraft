package micdoodle8.mods.galacticraft.core.items;

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.util.EnumSortCategoryItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemOxygenMask extends Item implements ISortableItem, IClickableItem
{
    public ItemOxygenMask(Item.Properties properties)
    {
        super(properties);
//        this.setUnlocalizedName(assetName);
        //this.setTextureName(Constants.TEXTURE_PREFIX + assetName);
    }

//    @Override
//    public ItemGroup getCreativeTab()
//    {
//        return GalacticraftCore.galacticraftItemsTab;
//    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Rarity getRarity(ItemStack par1ItemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    @Override
    public EnumSortCategoryItem getCategory(int meta)
    {
        return EnumSortCategoryItem.GEAR;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = player.getHeldItem(hand);

        if (player instanceof ServerPlayerEntity)
        {
            if (itemStack.getItem() instanceof IClickableItem)
            {
                itemStack = ((IClickableItem)itemStack.getItem()).onItemRightClick(itemStack, worldIn, player);
            }

            if (itemStack.isEmpty())
            {
                return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
            }
        }
        return new ActionResult<>(ActionResultType.PASS, itemStack);
    }
    
    public ItemStack onItemRightClick(ItemStack itemStack, World worldIn, PlayerEntity player)
    {
        GCPlayerStats stats = GCPlayerStats.get(player);
        ItemStack gear = stats.getExtendedInventory().getStackInSlot(0);

        if (gear.isEmpty())
        {
            stats.getExtendedInventory().setInventorySlotContents(0, itemStack.copy());
            itemStack = ItemStack.EMPTY;
        }
        
        return itemStack;
    }
}